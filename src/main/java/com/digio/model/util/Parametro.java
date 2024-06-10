package com.digio.model.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Component
@Getter
@Setter
@ToString
public class Parametro {

	@Value( "${rest.urlProdutos}" )
	private String urlProdutos;
	
	@Value( "${rest.urlClientes}" )
	private String urlClientes;
	
}
