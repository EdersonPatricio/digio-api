package com.digio.dao;

import java.util.List;

import com.digio.model.response.ClienteResponse;
import com.digio.model.response.ProdutoResponse;

public interface DigioApiDao {

	public List<ProdutoResponse> findProdutos();

	public List<ClienteResponse> findClientes();
}
