package com.biolab.stockservice.services;

import com.biolab.stockservice.models.Produto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProdutoService {

    // Armazenamento simples em memória (sem banco de dados)
    private final List<Produto> produtos = new CopyOnWriteArrayList<>();
    private final AtomicLong proximoId = new AtomicLong(1);

    public ProdutoService() {
        // Cadastro inicial obrigatório dos 5 produtos
        cadastrarInicial("Notebook", new BigDecimal("2500.00"), 12);
        cadastrarInicial("Mouse", new BigDecimal("89.90"), 30);
        cadastrarInicial("Teclado", new BigDecimal("149.90"), 20);
        cadastrarInicial("Monitor", new BigDecimal("899.90"), 8);
        cadastrarInicial("Headset", new BigDecimal("199.90"), 15);
    }

    private void cadastrarInicial(String nome, BigDecimal preco, Integer estoque) {
        produtos.add(new Produto(proximoId.getAndIncrement(), nome, preco, estoque));
    }

    public List<Produto> listarTodos() {
        return List.copyOf(produtos);
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtos.stream()
                .filter(produto -> produto.getId().equals(id))
                .findFirst();
    }

    public Produto cadastrar(Produto produto) {
        BigDecimal precoNormalizado = produto.getPreco().setScale(2, RoundingMode.HALF_UP);
        Produto novoProduto = new Produto(
                proximoId.getAndIncrement(),
                produto.getNome().trim(),
                precoNormalizado,
                produto.getEstoque()
        );
        produtos.add(novoProduto);
        return novoProduto;
    }
}
