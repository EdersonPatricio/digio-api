package com.digio.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoResponse {

	@JsonAlias( "codigo" )
	private String codigo;
	
	@JsonAlias( "tipo_vinho" )
    private String tipoVinho;
    
	@JsonAlias( "preco" )
    private Double preco;
    
	@JsonAlias( "safra" )
    private Integer safra;

    @JsonAlias( "ano_compra" )
    private Integer anoCompra;
}
