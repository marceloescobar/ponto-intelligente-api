package com.mescobar.pontointeligente.api.controller;

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
import com.mescobar.pontointeligente.api.request.CadastroPJRequest;
import com.mescobar.pontointeligente.api.service.EmpresaService;
import com.mescobar.pontointeligente.api.service.FuncionarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin(origins = "*")
public class CadastroPJController {

	private final FuncionarioService funcionarioService;

	private final EmpresaService empresaService;

	@PostMapping
	public ResponseEntity<Response<CadastroPJRequest>> cadastrar(@RequestBody @Valid CadastroPJRequest cadastroPJ,
			BindingResult result) {
		log.info("cadastrando PJ: {}", cadastroPJ.toString());

		Response<CadastroPJRequest> response = new Response<CadastroPJRequest>();
		validarDadosExistentes(cadastroPJ, result);

		Empresa empresa = this.converterDTOparaEmpresa(cadastroPJ);
		Funcionario funcionario = this.converterDTOparaFuncionario(cadastroPJ);

		if (result.hasErrors()) {
			log.error("Erro validando dados do cadastro PJ: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));

			return ResponseEntity.badRequest().body(response);
		}

		this.empresaService.persistir(empresa);
		funcionario.setEmpresa(empresa);
		this.funcionarioService.persistir(funcionario);

		response.setData(this.converterCadastroPJDto(funcionario));

		return ResponseEntity.ok(response);

	}

	private void validarDadosExistentes(CadastroPJRequest cadastroPJ, BindingResult result) {
		this.empresaService.buscarPorCnpj(cadastroPJ.getCnpj())
				.ifPresent(emp -> result.addError(new ObjectError("empresa", "Empresa já existente.")));

		this.funcionarioService.buscarPorCpf(cadastroPJ.getCpf())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente.")));

		this.funcionarioService.buscarPorEmail(cadastroPJ.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente.")));
	}

	private Empresa converterDTOparaEmpresa(CadastroPJRequest cadastroPJ) {
		Empresa emp = Empresa.builder().cnpj(cadastroPJ.getCnpj()).razaoSocial(cadastroPJ.getRazaoSocial()).build();

		return emp;
	}

	private Funcionario converterDTOparaFuncionario(CadastroPJRequest dto) {
		return Funcionario.builder().cpf(dto.getCpf()).nome(dto.getNome()).email(dto.getEmail()).senha(dto.getSenha())
				.build();
	}

	/**
	 * Popula o DTO de cadastro com os dados do funcionário e empresa.
	 * 
	 * @param funcionario
	 * @return CadastroPJDto
	 */
	private CadastroPJRequest converterCadastroPJDto(Funcionario funcionario) {
		return CadastroPJRequest.builder().id(funcionario.getId()).nome(funcionario.getNome()).email(funcionario.getEmail())
				.cpf(funcionario.getCpf()).razaoSocial(funcionario.getEmpresa().getRazaoSocial())
				.cnpj(funcionario.getEmpresa().getCnpj()).build();
	}
}
