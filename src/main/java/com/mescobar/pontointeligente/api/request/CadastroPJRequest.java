package com.mescobar.pontointeligente.api.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CadastroPJRequest {

	private Long id;
	
	@NotEmpty(message = "nome não pode ser vazio")
	@Length(min = 3, max=200, message = "Nome deve conter entre 3 e 200 caracteres")
	private String nome;
	
	@NotEmpty(message = "Email não pode ser vazio")
	@Length(min=5, max=200, message = "email deve conter entre 5 e 200 caracteres")
	@Email(message = "Email inválido")
	private String email;
	
	@NotEmpty(message = "senha não pode ser vazia")
	private String senha;
	
	@NotEmpty(message = "CPF não pode ser vazio")
	@CPF(message = "CPF inválido")
	private String cpf;
	
	private String razaoSocial;
	
	@CNPJ
	private String cnpj;
	
}
