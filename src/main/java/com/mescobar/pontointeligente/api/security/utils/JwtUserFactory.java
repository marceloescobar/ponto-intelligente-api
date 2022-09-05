package com.mescobar.pontointeligente.api.security.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.mescobar.pontointeligente.api.model.JwtUser;
import com.mescobar.pontointeligente.api.model.Usuario​;
import com.mescobar.pontointeligente.api.model.enums.PerfilEnum;

public class JwtUserFactory {

	private JwtUserFactory() {
	}

	public static JwtUser create(Usuario​ usuario) {
		return new JwtUser(usuario.getId(), usuario.getEmail(), usuario.getSenha(),
				mapToGrantedAuthorities(usuario.getPerfil()));
	}

	/**
	 * Converte o perfil do usuário para o formato utilizado pelo Spring Security.
	 *
	 * @param perfilEnum
	 * @return List<GrantedAuthority>
	 */
	private static List<GrantedAuthority> mapToGrantedAuthorities(PerfilEnum perfilEnum) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(perfilEnum.toString()));
		return authorities;
	}
}
