package br.greeeen.baronesa_petshop_backend.repository

import br.greeeen.baronesa_petshop_backend.model.Produto
import org.springframework.stereotype.Repository

@Repository
interface ProdutoRepository {
    fun salvar(produto: Produto): Produto

    fun buscarPorId(id: String): Produto?

    fun buscarTodos(): List<Produto>

    fun buscarPorTermo(termo: String): List<Produto>

    fun deletar(id: String)
}