package com.mescobar.pontointeligente.api.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mescobar.pontointeligente.api.model.Funcionario;
import com.mescobar.pontointeligente.api.model.Lancamento;
import com.mescobar.pontointeligente.api.model.enums.TipoEnum;
import com.mescobar.pontointeligente.api.request.LancamentoRequest;
import com.mescobar.pontointeligente.api.service.FuncionarioService;
import com.mescobar.pontointeligente.api.service.LancamentoService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LancamentoControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private LancamentoService lancamentoService;
	
	@MockBean
	private FuncionarioService funcionarioService;
	
	private static final String URL_BASE = "/api/lancamentos/";
	
	private static final Long ID_FUNCIONARIO = 1L;
	
	private static final Long ID_LANCAMENTO = 1L;
	
	private static final String TIPO = TipoEnum.INICIO_TRABALHO.name();
	
	private static final Date DATA = new Date();
	
	private final SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Test
	public void testCadastrarLancamento() throws Exception{
		Lancamento lancamento = obterDadosLancamento();
		BDDMockito.given(funcionarioService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(Funcionario.builder().build()));
		BDDMockito.given(lancamentoService.persistir(Mockito.any(Lancamento.class))).willReturn(lancamento);
				
		mvc.perform(MockMvcRequestBuilders.post(URL_BASE)
				.content(this.obterJsonRequisicaoPost())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(ID_LANCAMENTO))
				.andExpect(jsonPath("$.data.tipo").value(TIPO))
				.andExpect(jsonPath("$.data.data").value(this.simpleFormat.format(DATA)))
				
				.andExpect(jsonPath("$.data.funcionarioId").value(ID_FUNCIONARIO))
				.andExpect(jsonPath("$.errors").isEmpty());
				
	}
	
	@Test
	public void testCadastrarLancamentoFuncionarioIdInvalido() throws Exception {
		BDDMockito.given(this.funcionarioService.buscarPorId(Mockito.anyLong())).willReturn(Optional.empty());

		mvc.perform(MockMvcRequestBuilders.post(URL_BASE)
				.content(this.obterJsonRequisicaoPost())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").value("Funcionário não encontrado. ID inexistente."))
				.andExpect(jsonPath("$.data").isEmpty());
	}
	
	@Test
	@WithMockUser(username="admin@admin.com", roles = {"ADMIN"})
	public void testRemoverLancamento() throws Exception {
		BDDMockito.given(this.lancamentoService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(new Lancamento()));

		mvc.perform(MockMvcRequestBuilders.delete(URL_BASE + ID_LANCAMENTO)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser
	public void testRemoverLancamentoAcessoNegado() throws Exception{
		BDDMockito.given(this.lancamentoService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(new Lancamento()));

		mvc.perform(MockMvcRequestBuilders.delete(URL_BASE + ID_LANCAMENTO)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}
	
	
	private String obterJsonRequisicaoPost() throws JsonProcessingException {
		LancamentoRequest request =  LancamentoRequest.builder()
				.id(null)
				.data(this.simpleFormat.format(DATA))
				.tipo(TIPO)
				.funcionarioId(ID_FUNCIONARIO)
				.build();
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(request);
	}
	
	
	/**
	 * @return
	 */
	private Lancamento obterDadosLancamento() {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(ID_LANCAMENTO);
		lancamento.setData(DATA);
		lancamento.setTipo(TipoEnum.valueOf(TIPO));
		lancamento.setFuncionario(Funcionario.builder().id(ID_FUNCIONARIO).build());
		
		return lancamento;
	}
}
