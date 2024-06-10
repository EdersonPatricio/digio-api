package com.digio.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digio.dao.DigioApiDao;
import com.digio.model.response.ClienteResponse;
import com.digio.model.response.CompraResponse;
import com.digio.model.response.ProdutoResponse;
import com.digio.model.response.RecomendacaoVinhoResponse;

@Service
public class DigioApiService {

	@Autowired
	private DigioApiDao digioApiDao;

	/**
	 * Método que prenche as informações das compras dos clientes
	 * 
	 * @return lista de clientes ordenando 
	 * as compras pelo valor total
	 */
	public List<ClienteResponse> findComprasClientes() {
		List<ProdutoResponse> produtos = findProdutos();

		Map<String, ProdutoResponse> mapaProdutos = produtos.stream()
			.collect( Collectors.toMap( ProdutoResponse::getCodigo, Function.identity() ) );

		List<ClienteResponse> clientes = findClientes();

		if ( Objects.nonNull( clientes ) ) {
			List<ClienteResponse> clientesAtualizados = clientes.stream().peek( cliente -> {
				cliente.setCompras( cliente.getCompras().stream().peek( compra -> {
					ProdutoResponse produto = mapaProdutos.get( compra.getCodigo() );
					compra.setProduto( produto );
					if ( Objects.nonNull( produto ) ) {
						compra.setValorTotal( new BigDecimal( compra.getQuantidade() * produto.getPreco() ).setScale( 2, RoundingMode.HALF_UP ).doubleValue() );
					}
				} ).sorted( Comparator.comparingDouble( CompraResponse::getValorTotal ) ).collect( Collectors.toList() ) );
				
				Double valorTotalCompras = cliente.getCompras().stream().mapToDouble( CompraResponse::getValorTotal ).sum();
				cliente.setValorTotalCompras( new BigDecimal( valorTotalCompras ).setScale( 2, RoundingMode.HALF_UP ).doubleValue() );
			} ).collect( Collectors.toList() );
			
			return clientesAtualizados;
		}

		return clientes;
	}
	
	/**
	 * Método que busca os produtos a partir de uma
	 * url mock
	 * 
	 * @return lista de produtos
	 */
	private List<ProdutoResponse> findProdutos() {
		return digioApiDao.findProdutos();
	}

	/**
	 * Método que busca os clientes e suas compras 
	 * a partir de uma url mock
	 * 
	 * @return lista de clientes
	 */
	private List<ClienteResponse> findClientes() {
		return digioApiDao.findClientes();
	}
	
	/**
	 * Método que calcula a maior compra de um cliente
	 * no ano informado com base nas suas compras e o
	 * valor total gasto
	 * 
	 * @return cliente e dados da maior compra realizada
	 * no ano informado
	 */
	public ClienteResponse obterMaiorCompra( Integer ano ) {
		List<ClienteResponse> clientes = findComprasClientes();

		Optional<CompraResponse> maiorCompraOptional = clientes.stream()
			.flatMap( cliente -> cliente.getCompras().stream() )
			.filter( compra -> {
				return compra.getProduto().getAnoCompra().equals( ano );
			} )
			.max( Comparator.comparingDouble( CompraResponse::getValorTotal ) );
		
		if ( maiorCompraOptional.isPresent() ) {
			CompraResponse maiorCompra = maiorCompraOptional.get();
		
			ClienteResponse clienteComMaiorCompra = clientes.stream()
				.filter( cliente -> cliente.getCompras().contains( maiorCompra ) ).findFirst().get();
			clienteComMaiorCompra.setCompras( List.of( maiorCompra ) );
	
			return clienteComMaiorCompra;
		} else {
			return null;
		}
	}
	
	/**
	 * Método que busca o top 3 clientes mais fiéis
	 * 
	 * @return lista dos 3 clientes mais fiéis e
	 * suas compras
	 */
	public List<ClienteResponse> findTopClientesFieis() {
		List<ClienteResponse> clientes = findComprasClientes();

		if ( Objects.nonNull( clientes ) ) {
			List<ClienteResponse> clientesFieis = clientes.stream()
				.sorted( Comparator.comparingDouble( ClienteResponse::getValorTotalCompras ).reversed() )
				.limit( 3 ).collect( Collectors.toList() );
			
			return clientesFieis;
		}

		return clientes;
	}
	
	/**
	 * Método que analisa as compras feitas por um cliente
	 * e a partir de uma tipo de vinho informado verifica
	 * uma recomendação de vinho
	 * 
	 * @return recomendação de vinho com base nas compras
	 * feitas para aquele tipo de vinho
	 */
	public RecomendacaoVinhoResponse obterRecomendacaoVinho( String cpf, String tipo ) {
		List<ClienteResponse> clientes = findComprasClientes();
		
		Optional<ClienteResponse> clienteOptional = clientes.stream()
			.filter( cliente -> cliente.getCpf().equals( cpf ) ).findFirst();
		
		if ( clienteOptional.isPresent() ) {
			ClienteResponse cliente = clienteOptional.get();
			
            Map<String, Integer> mapaTotalCompraPorVinho = cliente.getCompras().stream()
				.filter( compra -> compra.getProduto().getTipoVinho().equals( tipo ) )
				.collect( Collectors.groupingBy(
					CompraResponse::getCodigo,
					Collectors.summingInt( CompraResponse::getQuantidade )
            ));

	        Map.Entry<String, Integer> mapaVinhoMaisComprado = mapaTotalCompraPorVinho.entrySet().stream()
				.max( Map.Entry.comparingByValue() ).orElse( null );
	        
	        if ( Objects.nonNull( mapaVinhoMaisComprado ) ) {
	        	ProdutoResponse produto = cliente.getCompras().stream()
					.filter( compra -> compra.getProduto().getCodigo().equals( mapaVinhoMaisComprado.getKey() ) )
					.findFirst().get().getProduto();
	        	
	        	return new RecomendacaoVinhoResponse( cliente.getNome(), cliente.getCpf(), tipo, produto );
	        } else {
	        	return new RecomendacaoVinhoResponse( cliente.getNome(), cliente.getCpf(), "Tipo de vinho não encontrado nas compras realizadas." );
	        }
		} else {
			return new RecomendacaoVinhoResponse( "Cliente não encontrado", cpf, tipo );
		}
	}
}
