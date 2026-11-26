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

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<ProdutosResponse> listarProdutos() {
        ProdutosResponse resposta = new ProdutosResponse(produtoService.listarTodos());
        return ResponseEntity.ok()
                .varyBy(HttpHeaders.ACCEPT)
                .body(resposta);
    }

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
