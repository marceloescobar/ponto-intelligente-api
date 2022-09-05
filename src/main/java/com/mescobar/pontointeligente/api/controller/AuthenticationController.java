package com.mescobar.pontointeligente.api.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.mescobar.pontointeligente.api.controller.response.Response;
import com.mescobar.pontointeligente.api.dto.JwtAuthenticationDTO;
import com.mescobar.pontointeligente.api.dto.TokenDTO;
import com.mescobar.pontointeligente.api.security.utils.JwtTokenUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
public class AuthenticationController {

	private static final String TOKEN_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private final AuthenticationManager authenticationManager;

	private final JwtTokenUtil jwtTokenUtil;

	private final UserDetailsService userDetailsService;

	@PostMapping
	public ResponseEntity<Response<TokenDTO>> gerarTokenJwt(@Valid @RequestBody JwtAuthenticationDTO authenticationDto,
			BindingResult result) throws AuthenticationException {
		Response<TokenDTO> response = new Response<TokenDTO>();

		if (result.hasErrors()) {
			log.error("Erro validando lançamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		log.info("Gerando token para o email {}.", authenticationDto.getEmail());

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authenticationDto.getEmail(), authenticationDto.getSenha()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationDto.getEmail());
		String token = jwtTokenUtil.obterToken(userDetails);
		response.setData(new TokenDTO(token));

		return ResponseEntity.ok(response);
	}

	/**
	 * Gera um novo token com uma nova data de expiração.
	 * 
	 * @param request
	 * @return ResponseEntity<Response<TokenDto>>
	 */
	@PostMapping(value = "/refresh")
	public ResponseEntity<Response<TokenDTO>> gerarRefreshTokenJwt(HttpServletRequest request) {
		log.info("Gerando refresh token JWT.");
		Response<TokenDTO> response = new Response<TokenDTO>();
		Optional<String> token = Optional.ofNullable(request.getHeader(TOKEN_HEADER));

		if (token.isPresent() && token.get().startsWith(BEARER_PREFIX)) {
			token = Optional.of(token.get().substring(7));
		}

		if (!token.isPresent()) {
			response.getErrors().add("Token não informado.");
		} else if (!jwtTokenUtil.tokenValido(token.get())) {
			response.getErrors().add("Token inválido ou expirado.");
		}

		if (!response.getErrors().isEmpty()) {
			return ResponseEntity.badRequest().body(response);
		}

		String refreshedToken = jwtTokenUtil.refreshToken(token.get());
		response.setData(new TokenDTO(refreshedToken));
		return ResponseEntity.ok(response);
	}
}
