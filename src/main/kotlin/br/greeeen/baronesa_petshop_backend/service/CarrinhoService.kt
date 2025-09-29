package br.greeeen.baronesa_petshop_backend.service

import br.greeeen.baronesa_petshop_backend.model.CarrinhoDeCompras

interface CarrinhoService {

    fun buscarOuCriarCarrinho(idUsuario: String): CarrinhoDeCompras

    fun adicionarItem(idUsuario: String, idProduto: String, quantidade: Int): CarrinhoDeCompras

    fun removerItem(idUsuario: String, idProduto: String): CarrinhoDeCompras

    fun atualizarQuantidade(idUsuario: String, idProduto: String, novaQuantidade: Int): CarrinhoDeCompras

    fun limparCarrinho(idUsuario: String)
}