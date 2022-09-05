package com.mescobar.pontointeligente.api.service;

import java.util.Optional;

import com.mescobar.pontointeligente.api.model.Usuario​;

public interface UsuarioService​ {

	Optional<Usuario​> buscarPorEmail(String email);
}
