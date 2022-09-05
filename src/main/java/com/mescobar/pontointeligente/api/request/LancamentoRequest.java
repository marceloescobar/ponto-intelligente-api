package com.mescobar.pontointeligente.api.request;

import java.util.Optional;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LancamentoRequest {

	private Optional<Long> id;
	private String data;
	private String tipo;
	private String descricao;
	private String localizacao;
	private Long funcionarioId;
}
