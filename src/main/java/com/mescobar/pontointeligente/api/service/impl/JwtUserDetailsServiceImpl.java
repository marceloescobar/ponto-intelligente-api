package com.mescobar.pontointeligente.api.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mescobar.pontointeligente.api.model.Usuario​;
import com.mescobar.pontointeligente.api.security.utils.JwtUserFactory;
import com.mescobar.pontointeligente.api.service.UsuarioService​;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UsuarioService​ usuarioService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Usuario​> funcionario = usuarioService.buscarPorEmail(username);

		if (funcionario.isPresent()) {
			return JwtUserFactory.create(funcionario.get());
		}
		throw new UsernameNotFoundException("Email não encontrado.");
	
	}

}
