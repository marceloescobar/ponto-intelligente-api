package com.mescobar.pontointeligente.api.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mescobar.pontointeligente.api.model.Funcionario;
import com.mescobar.pontointeligente.api.repository.FuncionarioRepository;
import com.mescobar.pontointeligente.api.service.FuncionarioService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
public class FuncionarioServiceImpl implements FuncionarioService {

	private final FuncionarioRepository funcionarioRepository;
	
	@Override
	public Funcionario persistir(Funcionario funcionario) {
		log.info("persistindo funcionario: {}", funcionario);
		return this.funcionarioRepository.save(funcionario);
	}

	@Override
	public Optional<Funcionario> buscarPorCpf(String cpf) {
		log.info("buscando funcioanrio por CPF: {}", cpf);
		return Optional.ofNullable(this.funcionarioRepository.findByCpf(cpf));
	}

	@Override
	public Optional<Funcionario> buscarPorEmail(String email) {
		log.info("buscando funcioanrio por email: {}", email);
		return Optional.ofNullable(this.funcionarioRepository.findByEmail(email));

	}

	@Override
	public Optional<Funcionario> buscarPorId(Long id) {
		log.info("buscando funcioanrio por id: {}", id);
		return this.funcionarioRepository.findById(id);
	}

}
