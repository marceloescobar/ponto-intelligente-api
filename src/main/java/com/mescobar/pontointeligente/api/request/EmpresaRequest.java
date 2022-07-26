package com.mescobar.pontointeligente.api.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmpresaRequest {
	private Long id;
	private String razaoSocial;
	private String cnpj;
}
