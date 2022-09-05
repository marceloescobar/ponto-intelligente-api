package com.mescobar.pontointeligente.api.controller;

import java.math.BigDecimal;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mescobar.pontointeligente.api.controller.response.Response;
import com.mescobar.pontointeligente.api.model.Funcionario;
import com.mescobar.pontointeligente.api.request.FuncionarioRequest;
import com.mescobar.pontointeligente.api.service.EmpresaService;
import com.mescobar.pontointeligente.api.service.FuncionarioService;
import com.mescobar.pontointeligente.api.utils.PasswordUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

	private final FuncionarioService funcionarioService;
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<FuncionarioRequest>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody FuncionarioRequest funcionarioRequest, BindingResult result) {
		log.info("atualizando funcionario: {}", funcionarioRequest.toString());

		Response<FuncionarioRequest> response = new Response<FuncionarioRequest>();

		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(id);

		if (!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "funcionario não encontrado"));
		}

		this.atualizarDadosFuncionario(funcionario.get(), funcionarioRequest, result);

		if (result.hasErrors()) {
			log.error(null);
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));

			return ResponseEntity.badRequest().body(response);
		}

		this.funcionarioService.persistir(funcionario.get());
		response.setData(this.converterFuncionario(funcionario.get()));

		return ResponseEntity.ok(response);
	}
	
	/**
	 * @param funcionario
	 * @param funcionarioRequest
	 * @param result
	 */
	private void atualizarDadosFuncionario(Funcionario funcionario, FuncionarioRequest funcionarioRequest, BindingResult result)
			 {
		funcionario.setNome(funcionarioRequest.getNome());

		if (!funcionario.getEmail().equals(funcionarioRequest.getEmail())) {
			this.funcionarioService.buscarPorEmail(funcionarioRequest.getEmail())
					.ifPresent(func -> result.addError(new ObjectError("email", "Email já existente.")));
			funcionario.setEmail(funcionarioRequest.getEmail());
		}

		funcionario.setQtdeHorasAlmoco(null);
		funcionarioRequest.getQtdeHorasAlmoco()
				.ifPresent(qtdHorasAlmoco -> funcionario.setQtdeHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));

		funcionario.setQtdeHorasTrabalhoDia(null);
		funcionarioRequest.getQtdeHorasTrabalhoDia()
				.ifPresent(qtdHorasTrabDia -> funcionario.setQtdeHorasTrabalhoDia(Float.valueOf(qtdHorasTrabDia)));

		funcionario.setValorHora(null);
		funcionarioRequest.getValorHora().ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));

		if (funcionarioRequest.getSenha().isPresent()) {
			funcionario.setSenha(PasswordUtils.gerarBCrypt(funcionarioRequest.getSenha().get()));
		}
	}
	
	/**
	 * @param funcionario
	 * @return
	 */
	private FuncionarioRequest converterFuncionario(Funcionario funcionario) {
		FuncionarioRequest funcionarioRequest = new FuncionarioRequest();
		funcionarioRequest.setId(funcionario.getId());
		funcionarioRequest.setEmail(funcionario.getEmail());
		funcionarioRequest.setNome(funcionario.getNome());
		
		funcionario.getQtdHorasAlmocoOpt().ifPresent(
				qtdHorasAlmoco -> funcionarioRequest.setQtdeHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
		
		funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(
				qtdHorasTrabDia -> funcionarioRequest.setQtdeHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabDia))));
		funcionario.getValorHoraOpt()
				.ifPresent(valorHora -> funcionarioRequest.setValorHora(Optional.of(valorHora.toString())));

		return funcionarioRequest;
	}
}
