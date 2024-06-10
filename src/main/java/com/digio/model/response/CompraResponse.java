package com.digio.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponse {

	@JsonAlias( "codigo" )
	private String codigo;
	
	@JsonAlias( "quantidade" )
    private Integer quantidade;

    private Double valorTotal;
    
    private ProdutoResponse produto;
}
