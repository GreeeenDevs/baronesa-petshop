package br.greeeen.baronesa_petshop_backend.service

import br.greeeen.baronesa_petshop_backend.exception.NegocioException
import br.greeeen.baronesa_petshop_backend.exception.RecursoNaoEncontradoException
import br.greeeen.baronesa_petshop_backend.model.CarrinhoDeCompras
import br.greeeen.baronesa_petshop_backend.model.ItemDoCarrinho
import br.greeeen.baronesa_petshop_backend.repository.CarrinhoRepository
import org.springframework.stereotype.Service

@Service
class CarrinhoServiceImplementation(
    private val carrinhoRepository: CarrinhoRepository,
    private val produtoService: ProdutoService
) : CarrinhoService {

    override fun buscarOuCriarCarrinho(idUsuario: String): CarrinhoDeCompras {
        return carrinhoRepository.buscarPorId(idUsuario) ?: CarrinhoDeCompras(id = idUsuario)
    }

    override fun adicionarItem(idUsuario: String, idProduto: String, quantidade: Int): CarrinhoDeCompras {
        if (quantidade <= 0) {
            throw NegocioException("A quantidade a adicionar deve ser maior que zero.")
        }

        val produto = try {
            produtoService.buscarProdutoPorId(idProduto)
        } catch (e: RecursoNaoEncontradoException) {
            throw NegocioException("O produto com ID '$idProduto' não existe ou não está disponível.")
        }

        val carrinho = buscarOuCriarCarrinho(idUsuario)
        val itemExistente = carrinho.itens.find { it.idProduto == idProduto }
        val novaQuantidadeTotal = (itemExistente?.quantidade ?: 0) + quantidade

        if (novaQuantidadeTotal > produto.estoque) {
            throw NegocioException("Estoque insuficiente. O carrinho excede o máximo disponível: ${produto.estoque}")
        }

        val novosItens = carrinho.itens.toMutableList()
        val index = novosItens.indexOfFirst { it.idProduto == idProduto }

        if (index != -1) {
            val itemAtualizado = novosItens[index].copy(
                quantidade = novaQuantidadeTotal
            )
            novosItens[index] = itemAtualizado
        } else {
            val novoItem = ItemDoCarrinho(
                idProduto = produto.id!!,
                nomeProduto = produto.nome,
                precoUnitario = produto.preco,
                urlFoto = produto.fotos.firstOrNull(), // Usa a primeira foto como miniatura
                quantidade = quantidade
            )
            novosItens.add(novoItem)
        }

        val carrinhoAtualizado = carrinho.copy(itens = novosItens)
        return carrinhoRepository.salvar(carrinhoAtualizado)
    }

    override fun removerItem(idUsuario: String, idProduto: String): CarrinhoDeCompras {
        val carrinho = buscarOuCriarCarrinho(idUsuario)

        val itemExistente = carrinho.itens.find { it.idProduto == idProduto }
        if (itemExistente == null) {
            return carrinho
        }

        val novosItens = carrinho.itens.filter { it.idProduto != idProduto }

        val carrinhoAtualizado = carrinho.copy(itens = novosItens)
        return carrinhoRepository.salvar(carrinhoAtualizado)
    }

    override fun atualizarQuantidade(idUsuario: String, idProduto: String, novaQuantidade: Int): CarrinhoDeCompras {

        if (novaQuantidade <= 0) {
            return removerItem(idUsuario, idProduto)
        }

        val produto = try {
            produtoService.buscarProdutoPorId(idProduto)
        } catch (e: RecursoNaoEncontradoException) {
            throw NegocioException("O produto com ID '$idProduto' não existe ou não está disponível.")
        }

        val carrinho = buscarOuCriarCarrinho(idUsuario)
        val index = carrinho.itens.indexOfFirst { it.idProduto == idProduto }

        if (index == -1) {
            throw RecursoNaoEncontradoException("O item com ID '$idProduto' não está no carrinho.")
        }

        if (novaQuantidade > produto.estoque) {
            throw NegocioException("Estoque insuficiente para esta quantidade. Máximo disponível: ${produto.estoque}")
        }

        val novosItens = carrinho.itens.toMutableList()
        val itemAtualizado = novosItens[index].copy(quantidade = novaQuantidade)
        novosItens[index] = itemAtualizado

        val carrinhoAtualizado = carrinho.copy(itens = novosItens)
        return carrinhoRepository.salvar(carrinhoAtualizado)
    }

    override fun limparCarrinho(idUsuario: String) {
        carrinhoRepository.deletar(idUsuario)
    }
}