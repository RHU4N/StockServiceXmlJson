package com.biolab.stockservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProdutoControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    void deveListarProdutosEmJson() throws Exception {
        mockMvc.perform(get("/produtos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().stringValues(HttpHeaders.VARY, hasItem(containsString("Accept"))))
                .andExpect(jsonPath("$.produtos", hasSize(5)))
                .andExpect(jsonPath("$.produtos[0].id").value(1))
                .andExpect(jsonPath("$.produtos[0].nome").value("Notebook"))
                .andExpect(jsonPath("$.produtos[0].preco").value(2500.00))
                .andExpect(jsonPath("$.produtos[0].estoque").value(12));
    }

    @Test
    void deveListarProdutosEmXml() throws Exception {
        mockMvc.perform(get("/produtos").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(header().stringValues(HttpHeaders.VARY, hasItem(containsString("Accept"))))
                .andExpect(xpath("/produtos/produto").nodeCount(5))
                .andExpect(xpath("/produtos/produto[1]/id").string("1"))
                .andExpect(xpath("/produtos/produto[1]/nome").string("Notebook"))
                .andExpect(xpath("/produtos/produto[1]/preco").string("2500.00"))
                .andExpect(xpath("/produtos/produto[1]/estoque").string("12"));
    }

    @Test
    void deveRetornarJsonPorPadraoSemHeaderAccept() throws Exception {
        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().stringValues(HttpHeaders.VARY, hasItem(containsString("Accept"))))
                .andExpect(jsonPath("$.produtos", hasSize(5)));
    }

    @Test
    void deveBuscarProdutoPorIdEmJson() throws Exception {
        mockMvc.perform(get("/produtos/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().stringValues(HttpHeaders.VARY, hasItem(containsString("Accept"))))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Notebook"))
                .andExpect(jsonPath("$.preco").value(2500.00))
                .andExpect(jsonPath("$.estoque").value(12));
    }

    @Test
    void deveBuscarProdutoPorIdEmXml() throws Exception {
        mockMvc.perform(get("/produtos/1").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(header().stringValues(HttpHeaders.VARY, hasItem(containsString("Accept"))))
                .andExpect(xpath("/produto/id").string("1"))
                .andExpect(xpath("/produto/nome").string("Notebook"))
                .andExpect(xpath("/produto/preco").string("2500.00"))
                .andExpect(xpath("/produto/estoque").string("12"));
    }

    @Test
    void deveRetornar404QuandoProdutoNaoExistir() throws Exception {
        mockMvc.perform(get("/produtos/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar406ParaAcceptNaoSuportado() throws Exception {
        mockMvc.perform(get("/produtos").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void deveCadastrarProdutoViaJsonERetornar201() throws Exception {
        String payload = """
                {
                    "nome": "Webcam HD",
                    "preco": 249.90,
                    "estoque": 10
                }
                """;

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().stringValues(HttpHeaders.VARY, hasItem(containsString("Accept"))))
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.nome").value("Webcam HD"))
                .andExpect(jsonPath("$.preco").value(249.90))
                .andExpect(jsonPath("$.estoque").value(10));

        mockMvc.perform(get("/produtos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.produtos", hasSize(6)));
    }

    @Test
    void deveCadastrarProdutoViaXmlERetornar201() throws Exception {
        String payload = """
                <produto>
                    <nome>Mousepad Gamer</nome>
                    <preco>79.90</preco>
                    <estoque>25</estoque>
                </produto>
                """;

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(header().stringValues(HttpHeaders.VARY, hasItem(containsString("Accept"))))
                .andExpect(xpath("/produto/id").string("6"))
                .andExpect(xpath("/produto/nome").string("Mousepad Gamer"))
                .andExpect(xpath("/produto/preco").string("79.90"))
                .andExpect(xpath("/produto/estoque").string("25"));
    }

    @Test
    void deveRetornar415ParaContentTypeNaoSuportado() throws Exception {
        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("nome=Teste"))
                .andExpect(status().isUnsupportedMediaType());
    }
}
