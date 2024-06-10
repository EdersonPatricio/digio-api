package com.digio;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.digio.model.response.ClienteResponse;
import com.digio.model.response.CompraResponse;
import com.digio.model.response.ProdutoResponse;
import com.digio.model.response.RecomendacaoVinhoResponse;
import com.digio.service.DigioApiService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class DigioApiApplicationTests {
	
	private static ObjectMapper objectMapper;

	@Autowired
    private MockMvc mockMvc;
	
	@MockBean
	private DigioApiService digioApiService;
	
	private final String BASE_URL = "/digio-api";
	
	@BeforeAll
    public static void setUp() {
        objectMapper = new ObjectMapper();
    }
	
	@Test
    @Order( 1 )
    public void getComprasClientes() throws Exception {
		List<ClienteResponse> clientes = generateClientesResponse();
		
		when( digioApiService.findComprasClientes() ).thenReturn( clientes );
		
		MvcResult result = this.mockMvc.perform( get( BASE_URL + "/compras" ) )
			.andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
			.andExpect( status().isOk() )
			.andExpect( content().string( containsString( "74302668512" ) ) )
			.andExpect( content().string( containsString( "20623850567" ) ) )
			.andExpect( content().string( containsString( "20634031392" ) ) )
			.andReturn();
		
		List<ClienteResponse> clientesRetornados = objectMapper.readValue( result.getResponse().getContentAsString(), new TypeReference<List<ClienteResponse>>() {} );
		
		assertEquals( clientesRetornados.size(), 3 );
    }
	
	@Test
    @Order( 2 )
    public void getMaiorCompraAno() throws Exception {
		ClienteResponse cliente = generateClientesResponse().get( 0 );
		
		when( digioApiService.obterMaiorCompra( 2020 ) ).thenReturn( cliente );
		
		MvcResult result = this.mockMvc.perform( get( BASE_URL + "//maior-compra/2020" ) )
			.andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
			.andExpect( status().isOk() )
			.andExpect( jsonPath( "$.nome", is( "Pietra Antônia Brenda Silva" ) ) )
			.andExpect( jsonPath( "$.cpf", is( "74302668512" ) ) )
			.andReturn();
		
		ClienteResponse clienteRetornado = objectMapper.readValue( result.getResponse().getContentAsString(), new TypeReference<ClienteResponse>() {} );
		
		assertEquals( clienteRetornado.getCompras().size(), 1 );
		assertEquals( clienteRetornado.getValorTotalCompras(), Double.valueOf( 365.25 ) );
    }
	
	@Test
    @Order( 3 )
    public void getClientesFieis() throws Exception {
		List<ClienteResponse> clientes = generateClientesResponse();
		
		when( digioApiService.findTopClientesFieis() ).thenReturn( clientes );
		
		MvcResult result = this.mockMvc.perform( get( BASE_URL + "/clientes-fieis" ) )
			.andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
			.andExpect( status().isOk() )
			.andExpect( content().string( containsString( "74302668512" ) ) )
			.andExpect( content().string( containsString( "20623850567" ) ) )
			.andExpect( content().string( containsString( "20634031392" ) ) )
			.andReturn();
		
		List<ClienteResponse> clientesRetornados = objectMapper.readValue( result.getResponse().getContentAsString(), new TypeReference<List<ClienteResponse>>() {} );
		
		assertEquals( clientesRetornados.size(), 3 );
    }
	
	@Test
    @Order( 4 )
    public void getRecomendacaoVinho() throws Exception {
		RecomendacaoVinhoResponse recomendacaoVinhoResponse = new RecomendacaoVinhoResponse( "Pietra Antônia Brenda Silva", "74302668512", "Rosé", new ProdutoResponse( "3", "Rosé", Double.valueOf( 121.75 ), 2019, 2020 ) );
		
		when( digioApiService.obterRecomendacaoVinho( "74302668512", "Rosé" ) ).thenReturn( recomendacaoVinhoResponse );
		
		this.mockMvc.perform( get( BASE_URL + "/recomendacao/74302668512/Rosé" ) )
			.andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
			.andExpect( status().isOk() )
			.andExpect( jsonPath( "$.nome", is( "Pietra Antônia Brenda Silva" ) ) )
			.andExpect( jsonPath( "$.cpf", is( "74302668512" ) ) )
			.andExpect( jsonPath( "$.tipoVinho", is( "Rosé" ) ) );
    }

	private List<ClienteResponse> generateClientesResponse() {
		ClienteResponse client = new ClienteResponse();
		client.setNome( "Pietra Antônia Brenda Silva" );
		client.setCpf( "74302668512" );
		client.setCompras( List.of( new CompraResponse( "3", 3, Double.valueOf( 365.25 ), new ProdutoResponse( "3", "Rosé", Double.valueOf( 121.75 ), 2019, 2020 ) ) ) );
		client.setValorTotalCompras( Double.valueOf( 365.25 ) );
		
		ClienteResponse client2 = new ClienteResponse();
		client2.setNome( "Vitória Alícia Mendes" );
		client2.setCpf( "20623850567" );
		client2.setCompras( List.of( new CompraResponse( "1", 8, Double.valueOf( 1839.92 ), new ProdutoResponse( "1", "Tinto", Double.valueOf( 229.99 ), 2017, 2018 ) ) ) );
		client2.setValorTotalCompras( Double.valueOf( 1839.92 ) );
		
		ClienteResponse client3 = new ClienteResponse();
		client3.setNome( "Kaique Danilo Alves" );
		client3.setCpf( "20634031392" );
		client3.setCompras( List.of( new CompraResponse( "8", 3, Double.valueOf( 362.97 ), new ProdutoResponse( "8", "Rosé", Double.valueOf( 120.99 ), 2018, 2019 ) ) ) );
		client3.setValorTotalCompras( Double.valueOf( 362.97 ) );

		return List.of( client, client2, client3 );
	}
}
