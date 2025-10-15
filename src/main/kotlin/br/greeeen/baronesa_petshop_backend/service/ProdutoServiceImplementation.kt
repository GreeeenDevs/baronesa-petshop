package br.greeeen.baronesa_petshop_backend.service

import br.greeeen.baronesa_petshop_backend.exception.NegocioException
import br.greeeen.baronesa_petshop_backend.exception.RecursoNaoEncontradoException
import br.greeeen.baronesa_petshop_backend.model.Produto
import br.greeeen.baronesa_petshop_backend.repository.ProdutoRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ProdutoServiceImpl(
    private val repository: ProdutoRepository
) : ProdutoService {

    override fun salvarProduto(produto: Produto): Produto {

        if (produto.nome.isBlank()) {
            throw NegocioException("O nome do produto é obrigatório.")
        }
        if (produto.preco < 0.0) {
            throw NegocioException("O preço do produto não pode ser negativo.")
        }
        if (produto.estoque < 0) {
            throw NegocioException("O estoque inicial não pode ser negativo.")
        }

        return repository.salvar(produto)
    }

    override fun deletarProduto(id: String) {
        buscarProdutoPorId(id)
        repository.deletar(id)
    }

    override fun atualizarEstoque(id: String, novaQuantidade: Int): Produto {
        if (novaQuantidade < 0) {
            throw NegocioException("A quantidade em estoque não pode ser negativa.")
        }

        val produtoExistente = buscarProdutoPorId(id)

        // Cria uma cópia com o estoque atualizado
        val produtoAtualizado = produtoExistente.copy(estoque = novaQuantidade)

        return repository.salvar(produtoAtualizado)
    }

    override fun buscarProdutoPorId(id: String): Produto {
        return repository.buscarPorId(id) ?: throw RecursoNaoEncontradoException("Produto com ID '$id' não encontrado.")
    }

    override fun listarProdutos(): List<Produto> {
        return repository.buscarTodos().filter { it.estoque > 0 }
    }

    override fun buscarEFiltrarProdutos(termo: String?, categorias: List<String>): List<Produto> {
        val produtos = if (termo.isNullOrBlank()) {
            repository.buscarTodos()
        } else {
            repository.buscarPorTermo(termo)
        }

        var resultado = produtos.filter { it.estoque > 0 }

        if (categorias.isNotEmpty()) {
            resultado = resultado.filter { produto ->
                produto.categorias.any { categoriaProduto -> categorias.contains(categoriaProduto) }
            }
        }

        return resultado
    }
}