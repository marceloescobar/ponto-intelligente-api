package com.mescobar.pontointeligente.api.service;

import java.util.Optional;

import com.mescobar.pontointeligente.api.model.Funcionario;

public interface FuncionarioService {

	/**
	 * @param funcionario
	 * @return
	 */
	Funcionario persistir(Funcionario funcionario);

	/**
	 * @param cpf
	 * @return
	 */
	Optional<Funcionario> buscarPorCpf(String cpf);

	/**
	 * @param email
	 * @return
	 */
	Optional<Funcionario> buscarPorEmail(String email);
	
	/**
	 * @param id
	 * @return
	 */
	Optional<Funcionario> buscarPorId(Long id);
}
