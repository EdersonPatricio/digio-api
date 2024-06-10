package com.digio.controller;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digio.model.response.ClienteResponse;
import com.digio.model.response.RecomendacaoVinhoResponse;
import com.digio.service.DigioApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin
@RestController
@RequestMapping( "/digio-api" )
@Tag( name = "DigioApi" )
public class DigioApiController {

	private static Logger LOG = Logger.getLogger( DigioApiController.class );
	
	@Autowired
	private DigioApiService digioApiService;
	
	@GetMapping( "/compras" )
	@Operation( description = "Retorna uma lista das compras ordenadas de forma crescente por valor" )
	public ResponseEntity<List<ClienteResponse>> getComprasClientes() {
		LOG.info( this.getClass().getName() + " - getComprasClientes - INICIO" );
		
		List<ClienteResponse> clientes = digioApiService.findComprasClientes();
		
		if ( Objects.nonNull( clientes ) ) {
			return ResponseEntity.ok( clientes );
		}
		
		LOG.info( this.getClass().getName() + " - getComprasClientes - FIM" );
		
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping( "/maior-compra/{ano}" )
	@Operation( description = "Retorna a maior compra do ano informando os dados do cliente e da compra" )
	public ResponseEntity<ClienteResponse> getMaiorCompraAno( @PathVariable Integer ano ) {
		LOG.info( this.getClass().getName() + " - getMaiorCompraAno - INICIO" );
		
		ClienteResponse clienteComMaiorCompra = digioApiService.obterMaiorCompra( ano );
		
		if ( Objects.nonNull( clienteComMaiorCompra ) ) {
			return ResponseEntity.ok( clienteComMaiorCompra );
		}
		
		LOG.info( this.getClass().getName() + " - getMaiorCompraAno - FIM" );
		
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping( "/clientes-fieis" )
	@Operation( description = "Retorna o Top 3 clientes mais fiéis, clientes que possuem mais compras recorrentes com maiores valores" )
	public ResponseEntity<List<ClienteResponse>> getClientesFieis() {
		LOG.info( this.getClass().getName() + " - getClientesFieis - INICIO" );
		
		List<ClienteResponse> clientes = digioApiService.findTopClientesFieis();
		
		if ( Objects.nonNull( clientes ) ) {
			return ResponseEntity.ok( clientes );
		}
		
		LOG.info( this.getClass().getName() + " - getClientesFieis - FIM" );
		
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping( "/recomendacao/{cpf}/{tipoVinho}" )
	@Operation( description = "Retorna uma recomendação de vinho baseado nos tipos de vinho que o cliente mais compra" )
	public ResponseEntity<RecomendacaoVinhoResponse> getRecomendacaoVinho( @PathVariable String cpf, @PathVariable String tipoVinho ) {
		LOG.info( this.getClass().getName() + " - getRecomendacaoVinho - INICIO" );
		
		RecomendacaoVinhoResponse recomendacaoVinhoResponse = digioApiService.obterRecomendacaoVinho( cpf, tipoVinho );
		
		if ( Objects.nonNull( recomendacaoVinhoResponse ) ) {
			return ResponseEntity.ok( recomendacaoVinhoResponse );
		}
		
		LOG.info( this.getClass().getName() + " - getRecomendacaoVinho - FIM" );
		
		return ResponseEntity.noContent().build();
	}
}
