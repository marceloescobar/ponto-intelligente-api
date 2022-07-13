package com.mescobar.pontointeligente.api.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.mescobar.pontointeligente.api.model.enums.PerfilEnum;

import lombok.Data;

@Data
@Entity
@Table(name = "funcionario")
public class Funcionario implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String nome;
	private String email;
	private String senha;
	private String cpf;
	private BigDecimal valorHora;
	private Float qtdeHorasTrabalhoDia;
	
	@Enumerated(EnumType.STRING)
	private PerfilEnum perfil;
	private Date dataCriacao;
	private Date dataAtualizacao;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Empresa empresa;
	
	@OneToMany(mappedBy = "funcionario", fetch= FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Lancamento> lancamentos;
	

	@PreUpdate
	public void preUpdate() {
		this.dataAtualizacao= new Date();
	}
	
	@PrePersist
	public void prePersist() {
		final Date atual = new Date();
		this.dataCriacao = atual;
		this.dataAtualizacao = atual;
	}
}
