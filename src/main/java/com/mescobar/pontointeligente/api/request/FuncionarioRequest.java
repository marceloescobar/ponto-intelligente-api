package com.mescobar.pontointeligente.api.request;

import java.util.Optional;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

public class FuncionarioRequest {
	private Long id;

	@NotEmpty(message = "nome não pode ser vazio")
	@Length(min = 3, max = 200, message = "nome deve conter entre 3 e 200 caracteres")
	private String nome;

	@NotEmpty(message = "email não pode ser vazio")
	@Length(min = 5, max = 200, message = "email deve conter entre 5 e 200 caracteres")
	private String email;

	private Optional<String> senha = Optional.empty();

	private Optional<String> valorHora = Optional.empty();

	private Optional<String> qtdeHorasTrabalhoDia = Optional.empty();

	private Optional<String> qtdeHorasAlmoco = Optional.empty();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Optional<String> getSenha() {
		return senha;
	}

	public void setSenha(Optional<String> senha) {
		this.senha = senha;
	}

	public Optional<String> getValorHora() {
		return valorHora;
	}

	public void setValorHora(Optional<String> valorHora) {
		this.valorHora = valorHora;
	}

	public Optional<String> getQtdeHorasTrabalhoDia() {
		return qtdeHorasTrabalhoDia;
	}

	public void setQtdeHorasTrabalhoDia(Optional<String> qtdeHorasTrabalhoDia) {
		this.qtdeHorasTrabalhoDia = qtdeHorasTrabalhoDia;
	}

	public Optional<String> getQtdeHorasAlmoco() {
		return qtdeHorasAlmoco;
	}

	public void setQtdeHorasAlmoco(Optional<String> qtdeHorasAlmoco) {
		this.qtdeHorasAlmoco = qtdeHorasAlmoco;
	}

}
