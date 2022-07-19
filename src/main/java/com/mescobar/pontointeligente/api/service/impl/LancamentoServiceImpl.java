package com.mescobar.pontointeligente.api.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mescobar.pontointeligente.api.model.Lancamento;
import com.mescobar.pontointeligente.api.repository.LancamentoRepository;
import com.mescobar.pontointeligente.api.service.LancamentoService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Service
public class LancamentoServiceImpl implements LancamentoService {

	private final LancamentoRepository lancamentoRepository;

	@Override
	public Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, PageRequest pageRequest) {
		log.info("buscando lancamentos para o funcionario ID {}", funcionarioId);
		return this.lancamentoRepository.findByFuncionarioId(funcionarioId, pageRequest);
	}

	@Override
	public Optional<Lancamento> buscarPorId(Long id) {
		log.info("buscando lancamentos por ID {}", id);
		return this.lancamentoRepository.findById(id);
	}

	@Override
	public Lancamento persistir(Lancamento lancamento) {
		log.info("persistindo o lancamento {}", lancamento);
		return this.lancamentoRepository.save(lancamento);
	}

	@Override
	public void remover(Long id) {
		log.info("persistindo o lancamento id {}", id);
		this.lancamentoRepository.deleteById(id);
	}

}
