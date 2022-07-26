package com.mescobar.pontointeligente.api.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mescobar.pontointeligente.api.controller.response.Response;
import com.mescobar.pontointeligente.api.model.Empresa;
import com.mescobar.pontointeligente.api.model.Funcionario;
import com.mescobar.pontointeligente.api.model.enums.PerfilEnum;
import com.mescobar.pontointeligente.api.request.CadastroPFRequest;
import com.mescobar.pontointeligente.api.service.EmpresaService;
import com.mescobar.pontointeligente.api.service.FuncionarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cadastrar-pf")
@CrossOrigin(origins = "*")
public class CadastroPFController {

	private final FuncionarioService funcionarioService;

	private final EmpresaService empresaService;
	
	@PostMapping
	public ResponseEntity<Response<CadastroPFRequest>> cadastrar(@Valid @RequestBody CadastroPFRequest cadastroPFRequest,
			BindingResult result){
		log.info("Cadastrando PF: {}", cadastroPFRequest.toString());
		
		Response<CadastroPFRequest> response = new Response<CadastroPFRequest>();

		validarDadosExistentes(cadastroPFRequest, result);
		Funcionario funcionario = this.converterParaFuncionario(cadastroPFRequest, result);

		if (result.hasErrors()) {
			log.error("Erro validando dados de cadastro PF: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPFRequest.getCnpj());
		empresa.ifPresent(emp -> funcionario.setEmpresa(emp));
		this.funcionarioService.persistir(funcionario);

		response.setData(this.converterParaRequest(funcionario));
		return ResponseEntity.ok(response);
	}
	
	/**
	 * @param cadastroPFRequest
	 * @param result
	 */
	private void validarDadosExistentes(CadastroPFRequest cadastroPFRequest, BindingResult result) {
		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPFRequest.getCnpj());
		
		if (!empresa.isPresent()) {
			result.addError(new ObjectError("empresa", "Empresa não cadastrada."));
		}
		
		this.funcionarioService.buscarPorCpf(cadastroPFRequest.getCpf())
			.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente.")));

		this.funcionarioService.buscarPorEmail(cadastroPFRequest.getEmail())
			.ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente.")));
	}
	
	private Funcionario converterParaFuncionario(CadastroPFRequest cadastroPFRequest, BindingResult result)
			 {
		Funcionario funcionario = Funcionario.builder()
				.nome(cadastroPFRequest.getNome())
				.email(cadastroPFRequest.getEmail())
				.cpf(cadastroPFRequest.getCpf())
				.perfil(PerfilEnum.ROLE_USUARIO)
				.senha(null)
				.build();
		
		
		
		//funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPFDto.getSenha()));
		
		cadastroPFRequest.getQtdeHorasAlmoco()
		.ifPresent(hrsAlmoco -> funcionario.setQtdeHorasAlmoco(Float.valueOf(hrsAlmoco)));
		
				
	
				
		/*
		 * cadastroPFDto.getQtdHorasTrabalhoDia() .ifPresent(qtdHorasTrabDia ->
		 * funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabDia)));
		 * cadastroPFDto.getValorHora().ifPresent(valorHora ->
		 * funcionario.setValorHora(new BigDecimal(valorHora)));
		 */

		return funcionario;
	}
	
	/**
	 * Popula o DTO de cadastro com os dados do funcionário e empresa.
	 * 
	 * @param funcionario
	 * @return CadastroPFDto
	 */
	private CadastroPFRequest converterParaRequest(Funcionario funcionario) {
		CadastroPFRequest cadastroPFRequest = CadastroPFRequest.builder()
		.id(funcionario.getId())
		.nome(funcionario.getNome())
		.email(funcionario.getEmail())
		.cpf(funcionario.getCpf())
		.cnpj(funcionario.getEmpresa().getCnpj())
		.build();
		
		
	/*	funcionario.getQtdHorasAlmocoOpt().ifPresent(qtdHorasAlmoco -> cadastroPFDto
				.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
		funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(
				qtdHorasTrabDia -> cadastroPFDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabDia))));
		funcionario.getValorHoraOpt()
				.ifPresent(valorHora -> cadastroPFDto.setValorHora(Optional.of(valorHora.toString())));*/

		return cadastroPFRequest;
	}
	
}
