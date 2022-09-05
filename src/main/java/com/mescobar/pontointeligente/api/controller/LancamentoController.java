package com.mescobar.pontointeligente.api.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mescobar.pontointeligente.api.controller.response.Response;
import com.mescobar.pontointeligente.api.model.Funcionario;
import com.mescobar.pontointeligente.api.model.Lancamento;
import com.mescobar.pontointeligente.api.model.enums.TipoEnum;
import com.mescobar.pontointeligente.api.request.LancamentoRequest;
import com.mescobar.pontointeligente.api.service.FuncionarioService;
import com.mescobar.pontointeligente.api.service.LancamentoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoController {

	private final LancamentoService lancamentoService;

	private final FuncionarioService funcionarioService;

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Value("${paginacao.qtde_por_pagina}")
	private int qtdePorPagina;

	@GetMapping(value = "/funcionario/{funcionarioId}")
	public ResponseEntity<Response<Page<LancamentoRequest>>> listarPorFuncionarioId(
			@PathVariable("funcionarioId") Long funcionarioId, @RequestParam(value = "pag", defaultValue = "0") int pag,
			@RequestParam(value = "ord", defaultValue = "id") String ord,
			@RequestParam(value = "dir", defaultValue = "DESC") String dir) {
		log.info("buscando lancamentos por id do funcionario {}, pagina {}", funcionarioId, pag);

		Response<Page<LancamentoRequest>> response = new Response<Page<LancamentoRequest>>();

		PageRequest pageRequest = PageRequest.of(pag, this.qtdePorPagina, Direction.valueOf(dir), ord);

		Page<Lancamento> lancamentos = this.lancamentoService.buscarPorFuncionarioId(funcionarioId, pageRequest);

		Page<LancamentoRequest> lrequest = lancamentos.map(lancamento -> this.converterLancamento(lancamento));
		response.setData(lrequest);

		return ResponseEntity.ok(response);
	}

	/**
	 * @param id
	 * @return
	 */
	public ResponseEntity<Response<LancamentoRequest>> listarPorId(@PathVariable("id") Long id) {
		log.info("buscando lancamento id: {}", id);
		Response<LancamentoRequest> response = new Response<LancamentoRequest>();

		Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);

		if (!lancamento.isPresent()) {
			log.info("lancamento nao encontrado para o id: {}", id);
			response.getErrors().add("Lancamento nao encontrado para o id " + id);

			return ResponseEntity.badRequest().body(response);
		}

		response.setData(this.converterLancamento(lancamento.get()));

		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<Response<LancamentoRequest>> adicionar(
			@Valid @RequestBody LancamentoRequest lancamentoRequest, BindingResult result) throws ParseException {
		log.info("Adicionando lancamento: {}", lancamentoRequest.toString());
		Response<LancamentoRequest> response = new Response<LancamentoRequest>();
		this.validarFuncionario(lancamentoRequest, result);

		Lancamento lancamento = this.converterRequestParaLancamento(lancamentoRequest, result);

		if (result.hasErrors()) {
			log.error("erro validando lançamento: {}", result.getAllErrors());

			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		lancamento = this.lancamentoService.persistir(lancamento);
		response.setData(this.converterLancamento(lancamento));

		return ResponseEntity.ok(response);
	}

	/**
	 * @param id
	 * @param lancamentoDto
	 * @param result
	 * @return
	 * @throws ParseException
	 */
	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<LancamentoRequest>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody LancamentoRequest lancamentoRequest, BindingResult result) throws ParseException {
		log.info("Atualizando lançamento: {}", lancamentoRequest.toString());
		Response<LancamentoRequest> response = new Response<LancamentoRequest>();

		validarFuncionario(lancamentoRequest, result);
		lancamentoRequest.setId(Optional.of(id));
		Lancamento lancamento = this.converterRequestParaLancamento(lancamentoRequest, result);

		if (result.hasErrors()) {
			log.error("Erro validando lançamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		lancamento = this.lancamentoService.persistir(lancamento);
		response.setData(this.converterLancamento(lancamento));

		return ResponseEntity.ok(response);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Response<String>> remover(@PathVariable("id") Long id) {
		log.info("Removendo lançamento: {}", id);
		Response<String> response = new Response<String>();
		Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);

		if (!lancamento.isPresent()) {
			log.info("Erro ao remover devido ao lançamento ID: {} ser inválido.", id);
			response.getErrors().add("Erro ao remover lançamento. Registro não encontrado para o id " + id);
			return ResponseEntity.badRequest().body(response);
		}

		this.lancamentoService.remover(id);
		return ResponseEntity.ok(new Response<String>());
	}

	private LancamentoRequest converterLancamento(Lancamento lancamento) {
		return LancamentoRequest.builder().id(Optional.of(lancamento.getId()))
				.data(this.dateFormat.format(lancamento.getData())).tipo(lancamento.getTipo().toString())
				.descricao(lancamento.getDescricao()).localizacao(lancamento.getLocalizacao())
				.funcionarioId(lancamento.getFuncionario().getId()).build();
	}

	private void validarFuncionario(LancamentoRequest lrequest, BindingResult result) {
		if (lrequest.getFuncionarioId() == null) {
			result.addError(new ObjectError("funcionario", "funcionario não informado"));
			return;
		}

		log.info("validando funcionario id {}", lrequest.getFuncionarioId());
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(lrequest.getFuncionarioId());

		if (!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "funcionario não encontrado pelo id informado"));
		}
	}

	/**
	 * @param lancmentoRequest
	 * @param result
	 * @return
	 * @throws ParseException
	 */
	private Lancamento converterRequestParaLancamento(LancamentoRequest lancamentoRequest, BindingResult result)
			throws ParseException {
		Lancamento lancamento = new Lancamento();

		if (lancamentoRequest.getId().isPresent()) {
			Optional<Lancamento> lanc = this.lancamentoService.buscarPorId(lancamentoRequest.getId().get());
			if (lanc.isPresent()) {
				lancamento = lanc.get();
			} else {
				result.addError(new ObjectError("lancamento", "Lançamento não encontrado."));
			}
		} else {
			lancamento.setFuncionario(Funcionario.builder().id(lancamentoRequest.getFuncionarioId()).build());
		}

		lancamento.setDescricao(lancamentoRequest.getDescricao());
		lancamento.setLocalizacao(lancamentoRequest.getLocalizacao());
		lancamento.setData(this.dateFormat.parse(lancamentoRequest.getData()));

		if (EnumUtils.isValidEnum(TipoEnum.class, lancamentoRequest.getTipo())) {
			lancamento.setTipo(TipoEnum.valueOf(lancamentoRequest.getTipo()));
		} else {
			result.addError(new ObjectError("tipo", "Tipo inválido."));
		}

		return lancamento;
	}

}
