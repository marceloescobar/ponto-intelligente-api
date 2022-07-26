package com.mescobar.pontointeligente.api.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.mescobar.pontointeligente.api.model.Empresa;
import com.mescobar.pontointeligente.api.model.Funcionario;
import com.mescobar.pontointeligente.api.model.enums.PerfilEnum;

@SpringBootTest
@ActiveProfiles("test")
public class FuncionarioRepositoryTest {

	@Autowired
	private FuncionarioRepository funcionarioRepository;

	@Autowired
	private EmpresaRepository empresaRepository;

	private static final String EMAIL = "email|@email.com";
	private static final String CPF = "21212332322";

	@BeforeAll
	public void setUp() {
		Empresa empresa = this.empresaRepository.save(obterDadosEmpresa());
		this.funcionarioRepository.save(obterDadosFuncionario(empresa));
	}

	@AfterAll
	public final void tearDown() {
		this.empresaRepository.deleteAll();
	}

	@Test
	public void testBuscarFuncionarioPoEmail() {
		Funcionario funcionario = this.funcionarioRepository.findByEmail(EMAIL);

		Assertions.assertEquals(EMAIL, funcionario.getEmail());
	}

	@Test
	public void testBuscarFuncionarioPorCpf() {
		Funcionario funcionario = this.funcionarioRepository.findByCpf(CPF);

		Assertions.assertEquals(CPF, funcionario.getCpf());
	}

	@Test
	public void testBuscarFuncionarioPorEmailECpf() {
		Funcionario funcionario = this.funcionarioRepository.findByCpfOrEmail(CPF, EMAIL);

		Assertions.assertNotNull(funcionario);
	}

	@Test
	public void testBuscarFuncionarioPorEmailOuCpfParaEmailInvalido() {
		Funcionario funcionario = this.funcionarioRepository.findByCpfOrEmail(CPF, "email@invalido.com");

		Assertions.assertNotNull(funcionario);
	}

	@Test
	public void testBuscarFuncionarioPorEmailECpfParaCpfInvalido() {
		Funcionario funcionario = this.funcionarioRepository.findByCpfOrEmail("12345678901", EMAIL);

		Assertions.assertNotNull(funcionario);
	}

	private Funcionario obterDadosFuncionario(Empresa empresa) {
		return Funcionario.builder().nome("Jose da silva").perfil(PerfilEnum.ROLE_USUARIO).senha("").cpf(CPF)
				.email(EMAIL).build();
	}

	private Empresa obterDadosEmpresa() {
		return Empresa.builder().razaoSocial("Empresa de exemplo").cnpj("51463645000100").build();
	}
}
