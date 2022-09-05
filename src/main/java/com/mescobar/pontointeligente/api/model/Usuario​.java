package com.mescobar.pontointeligente.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.mescobar.pontointeligente.api.model.enums.PerfilEnum;

import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario​ {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "email",nullable = false)
	private String email;
	
	@Column(name = "senha",  nullable = false)
	private String senha;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "perfil", nullable = false)
	private PerfilEnum perfil;

}
