package com.mescobar.pontointeligente.api.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mescobar.pontointeligente.api.model.Usuario​;
import com.mescobar.pontointeligente.api.repository.UsuarioRepository​;
import com.mescobar.pontointeligente.api.service.UsuarioService​;

import lombok.Data;

@Data
@Service
public class UsuarioServiceImpl​ implements UsuarioService​{

	private final UsuarioRepository​ usuarioRepository;
	
	public Optional<Usuario​> buscarPorEmail(String email){
		return Optional.ofNullable(this.usuarioRepository.findByEmail(email));
	}
}
