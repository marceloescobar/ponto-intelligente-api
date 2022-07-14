package com.mescobar.pontointeligente.api.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mescobar.pontointeligente.api.model.Empresa;
import com.mescobar.pontointeligente.api.repository.EmpresaRepository;
import com.mescobar.pontointeligente.api.service.EmpresaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpresaServiceImpl implements EmpresaService {

	private final EmpresaRepository empresaRepository;
	
	@Override
	public Optional<Empresa> buscarPorCnpj(String cnpj) {
		log.info("Buscando uma empresa para o cnpj {}", cnpj);
		return Optional.ofNullable(empresaRepository.findByCnpj(cnpj));
	}

	@Override
	public Empresa persistir(Empresa empresa) {
		log.info("Persistindo empresa {}", empresa);
		return this.empresaRepository.save(empresa);
	}

}
