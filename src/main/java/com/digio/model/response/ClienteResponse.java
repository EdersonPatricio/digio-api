package com.digio.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class ClienteResponse {

	@JsonAlias( "nome" )
	private String nome;
	
	@JsonAlias( "cpf" )
    private String cpf;
    
    @JsonAlias( "compras" )
    private List<CompraResponse> compras;
    
    private Double valorTotalCompras;
}
