package com.digio.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecomendacaoVinhoResponse {

	private String nome;
	
    private String cpf;

    private String tipoVinho;
    
	@JsonInclude( JsonInclude.Include.NON_NULL )
    private ProdutoResponse produto;

	public RecomendacaoVinhoResponse( String nome, String cpf, String tipoVinho ) {
		this.nome = nome;
		this.cpf = cpf;
		this.tipoVinho = tipoVinho;
	}
}
