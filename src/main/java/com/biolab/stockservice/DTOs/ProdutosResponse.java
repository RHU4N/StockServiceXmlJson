package com.biolab.stockservice.DTOs;

import com.biolab.stockservice.models.Produto;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

// DTO wrapper para garantir um único elemento raiz <produtos> no XML mantendo o mesmo objeto Java em JSON e XML
// APRESENTACAO 05 — Colecao: lista de objetos em JSON e elementos <produto> em uma raiz XML.
// As anotacoes evitam um nivel extra de agrupamento no XML, sem criar outro modelo Java.
@JacksonXmlRootElement(localName = "produtos")
public class ProdutosResponse {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "produto")
    private List<Produto> produtos;

    public ProdutosResponse() {
    }

    public ProdutosResponse(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }
}
