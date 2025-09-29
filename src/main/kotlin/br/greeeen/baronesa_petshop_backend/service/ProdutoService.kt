package br.greeeen.baronesa_petshop_backend.service

import br.greeeen.baronesa_petshop_backend.model.Produto

interface ProdutoService {

    fun salvarProduto(produto: Produto): Produto

    fun deletarProduto(id: String)

    fun atualizarEstoque(id: String, novaQuantidade: Int): Produto

    fun buscarProdutoPorId(id: String): Produto

    fun listarProdutos(): List<Produto>

    fun buscarEFiltrarProdutos(termo: String?, categorias: List<String>): List<Produto>
}