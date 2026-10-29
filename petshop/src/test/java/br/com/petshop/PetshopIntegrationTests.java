package br.com.petshop;

import br.com.petshop.model.Pet;
import br.com.petshop.model.Tutor;
import br.com.petshop.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:petshop-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class PetshopIntegrationTests {

    @Autowired MockMvc mvc;
    @Autowired AgendamentoRepository agendamentos;
    @Autowired PetRepository pets;
    @Autowired TutorRepository tutores;
    @Autowired ProdutoRepository produtos;
    @Autowired UsuarioRepository usuarios;

    @BeforeEach
    void limparBanco() {
        agendamentos.deleteAll();
        pets.deleteAll();
        tutores.deleteAll();
        produtos.deleteAll();
        usuarios.deleteAll();
    }

    private MockHttpSession sessaoValida() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", "teste");
        session.setAttribute("ultimo", System.currentTimeMillis());
        return session;
    }

    private Tutor tutor() {
        Tutor t = new Tutor();
        t.nome = "Maria Silva";
        t.telefone = "11987654321";
        t.email = "maria@example.com";
        t.cpf = "52998224725";
        t.endereco = "Rua A, 123";
        return tutores.save(t);
    }

    private Pet pet() {
        Pet p = new Pet();
        p.nome = "Rex";
        p.especie = "Cão";
        p.raca = "SRD";
        p.idade = 3;
        p.tutor = tutor();
        return pets.save(p);
    }

    @Test
    void formulariosNovosDevemRenderizar() throws Exception {
        MockHttpSession session = sessaoValida();
        mvc.perform(get("/tutores/novo").session(session)).andExpect(status().isOk());
        mvc.perform(get("/pets/novo").session(session)).andExpect(status().isOk());
        mvc.perform(get("/produtos/novo").session(session)).andExpect(status().isOk());
        mvc.perform(get("/agendamentos/novo").session(session)).andExpect(status().isOk());
    }

    @Test
    void tutorComCpfInvalidoDeveExibirErroSemFalha500() throws Exception {
        mvc.perform(post("/tutores/salvar").session(sessaoValida())
                        .param("nome", "Maria Silva")
                        .param("telefone", "(11) 98765-4321")
                        .param("email", "maria@example.com")
                        .param("cpf", "111.111.111-11")
                        .param("endereco", "Rua A, 123"))
                .andExpect(status().isOk())
                .andExpect(view().name("tutores/form"))
                .andExpect(content().string(containsString("CPF inválido")));
    }

    @Test
    void petComIdadeInvalidaDeveExibirErroSemFalha500() throws Exception {
        Tutor t = tutor();
        mvc.perform(post("/pets/salvar").session(sessaoValida())
                        .param("nome", "Rex")
                        .param("especie", "Cão")
                        .param("raca", "SRD")
                        .param("idade", "abc")
                        .param("tutorId", t.id.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/form"))
                .andExpect(content().string(containsString("Informe a idade em anos")));
    }

    @Test
    void produtoComPrecoInvalidoDeveExibirErroSemFalha500() throws Exception {
        mvc.perform(post("/produtos/salvar").session(sessaoValida())
                        .param("nome", "Ração")
                        .param("preco", "abc")
                        .param("estoque", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("produtos/form"))
                .andExpect(content().string(containsString("Preço inválido")));
    }

    @Test
    void agendamentoNoPassadoDeveExibirErroSemFalha500() throws Exception {
        Pet p = pet();
        mvc.perform(post("/agendamentos/salvar").session(sessaoValida())
                        .param("petId", p.id.toString())
                        .param("servico", "BANHO")
                        .param("dataHora", "2020-01-01T10:00")
                        .param("status", "AGENDADO"))
                .andExpect(status().isOk())
                .andExpect(view().name("agendamentos/form"))
                .andExpect(content().string(containsString("Não é possível agendar no passado")));
    }

    @Test
    void sessaoInativaDeveSerInvalidadaPeloServidor() throws Exception {
        MockHttpSession session = sessaoValida();
        session.setAttribute("ultimo", System.currentTimeMillis() - 31_000);
        mvc.perform(get("/").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?expirada"));
    }
}
