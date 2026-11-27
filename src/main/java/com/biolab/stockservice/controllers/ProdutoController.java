package com.biolab.stockservice.controllers;

import com.biolab.stockservice.DTOs.ProdutosResponse;
import com.biolab.stockservice.models.Produto;
import com.biolab.stockservice.services.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // APRESENTACAO 01 — GET /produtos: produces anuncia JSON e XML; Accept seleciona o formato.
    // O Spring/Jackson serializa o mesmo objeto Java e Vary: Accept identifica a variacao da resposta.
    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<ProdutosResponse> listarProdutos() {
        ProdutosResponse resposta = new ProdutosResponse(produtoService.listarTodos());
        return ResponseEntity.ok()
                .varyBy(HttpHeaders.ACCEPT)
                .body(resposta);
    }

    // APRESENTACAO 02 — GET /produtos/{id}: identificacao do recurso pela URL; retorna 200 ou 404.
    @GetMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(produto -> ResponseEntity.ok()
                        .varyBy(HttpHeaders.ACCEPT)
                        .body(produto))
                .orElseGet(() -> ResponseEntity.notFound()
                        .varyBy(HttpHeaders.ACCEPT)
                        .build());
    }

    // APRESENTACAO 03 — POST: consumes define os formatos recebidos; @RequestBody desserializa.
    // @Valid valida o objeto Java; produces/Accept definem a resposta, com HTTP 201.
    @PostMapping(
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public ResponseEntity<Produto> cadastrarProduto(@Valid @RequestBody Produto produto) {
        Produto criado = produtoService.cadastrar(produto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .varyBy(HttpHeaders.ACCEPT)
                .body(criado);
    }
}
