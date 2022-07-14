package com.mescobar.pontointeligente.api.service;

import java.util.Optional;

import com.mescobar.pontointeligente.api.model.Empresa;

public interface EmpresaService {

	/**
	 * @param cnpj
	 * @return
	 */
	Optional<Empresa> buscarPorCnpj(String cnpj);

	/**
	 * @param empresa
	 * @return
	 */
	Empresa persistir(Empresa empresa);
}
