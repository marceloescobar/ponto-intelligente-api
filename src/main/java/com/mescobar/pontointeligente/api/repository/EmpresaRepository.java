package com.mescobar.pontointeligente.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mescobar.pontointeligente.api.model.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

	Empresa findByCnpj(String cnpj);
}
