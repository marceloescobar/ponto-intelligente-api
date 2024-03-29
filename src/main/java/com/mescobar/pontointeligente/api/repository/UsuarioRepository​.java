package com.mescobar.pontointeligente.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mescobar.pontointeligente.api.model.Usuario​;

@Repository
public interface UsuarioRepository​ extends JpaRepository<Usuario​, Long> {

	Usuario​ findByEmail(String email);
}
