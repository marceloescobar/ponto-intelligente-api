package com.mescobar.pontointeligente.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mescobar.pontointeligente.api.model.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

	Funcionario findByCpf(String cpf);

	Funcionario findByEmail(String email);

	Funcionario findByCpfOrEmail(String cpf, String email);
}
