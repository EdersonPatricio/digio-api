package com.digio.dao.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.digio.dao.DigioApiDao;
import com.digio.model.response.ClienteResponse;
import com.digio.model.response.ProdutoResponse;
import com.digio.model.util.Parametro;

@Repository
public class DigioApiDaoImpl implements DigioApiDao {

	private static final RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private Parametro parametro;

	@Override
	public List<ProdutoResponse> findProdutos() {
		HttpEntity<String> httpEntity = getHttpEntity();
		
		ParameterizedTypeReference<List<ProdutoResponse>> retorno = new ParameterizedTypeReference<List<ProdutoResponse>>() {};

		ResponseEntity<List<ProdutoResponse>> response = restTemplate.exchange( parametro.getUrlProdutos(), HttpMethod.GET, httpEntity, retorno );

		if ( !response.getStatusCode().equals( HttpStatus.OK ) ) {
			throw new RuntimeException( "HTTP error code : " + response.getStatusCode() );
		}
		
		return response.getBody();
	}

	@Override
	public List<ClienteResponse> findClientes() {
		HttpEntity<String> httpEntity = getHttpEntity();
		
		ParameterizedTypeReference<List<ClienteResponse>> retorno = new ParameterizedTypeReference<List<ClienteResponse>>() {};

		ResponseEntity<List<ClienteResponse>> response = restTemplate.exchange( parametro.getUrlClientes(), HttpMethod.GET, httpEntity, retorno );

		if ( !response.getStatusCode().equals( HttpStatus.OK ) ) {
			throw new RuntimeException( "HTTP error code : " + response.getStatusCode() );
		}
		
		return response.getBody();
	}

	private static HttpEntity<String> getHttpEntity() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_JSON );
		httpHeaders.setAccept( Arrays.asList( MediaType.APPLICATION_JSON ) );
		httpHeaders.setConnection( "keep-alive" );
		httpHeaders.setCacheControl( CacheControl.noCache() );

		return new HttpEntity<String>( httpHeaders );
	}
}
