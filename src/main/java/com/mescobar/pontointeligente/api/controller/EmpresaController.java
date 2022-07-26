package com.mescobar.pontointeligente.api.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mescobar.pontointeligente.api.controller.response.Response;
import com.mescobar.pontointeligente.api.model.Empresa;
import com.mescobar.pontointeligente.api.request.EmpresaRequest;
import com.mescobar.pontointeligente.api.service.EmpresaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

	private final EmpresaService empresaService;

	@GetMapping(value = "/cnpj/{cnpj}")
	public ResponseEntity<Response<EmpresaRequest>> buscarPorCnpj(@PathVariable("cnpj") String cnpj) {
		log.info("buscando empresa por CNPJ: {}", cnpj);
		Response<EmpresaRequest> response = new Response<EmpresaRequest>();
		Optional<Empresa> empresa = empresaService.buscarPorCnpj(cnpj);

		if (!empresa.isPresent()) {
			log.info("empresa não encontrada para o CNPJ {}", cnpj);
			response.getErrors().add("empresa nao localizada para o CNPJ " + cnpj);
			return ResponseEntity.badRequest().body(response);
		}

		response.setData(this.converterEmpresa(empresa.get()));
		return ResponseEntity.ok(response);
	}

	private EmpresaRequest converterEmpresa(Empresa empresa) {
		return EmpresaRequest.builder().id(empresa.getId()).cnpj(empresa.getCnpj())
				.razaoSocial(empresa.getRazaoSocial()).build();

	}
}
