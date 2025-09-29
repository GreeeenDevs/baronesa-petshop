package br.greeeen.baronesa_petshop_backend.service

import br.greeeen.baronesa_petshop_backend.enum.StatusPedido
import br.greeeen.baronesa_petshop_backend.exception.NegocioException
import br.greeeen.baronesa_petshop_backend.exception.RecursoNaoEncontradoException
import br.greeeen.baronesa_petshop_backend.model.*
import br.greeeen.baronesa_petshop_backend.repository.PedidoRepository
import br.greeeen.baronesa_petshop_backend.servico.UsuarioService
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class PedidoServiceImpl(
    private val pedidoRepository: PedidoRepository,
    private val carrinhoService: CarrinhoService,
    private val produtoService: ProdutoService,
    private val servicoDeFrete: FreteService,
    private val servicoDePagamento: PagamentoService,
    private val servicoDeUsuario: UsuarioService
) : PedidoService {

    private val CUPOM_VALIDO = "BARONESA10"
    private val PORCENTAGEM_DESCONTO = BigDecimal("0.10") // 10%

    override fun calcularPreviaDoPedido(idUsuario: String, cepDestino: String, cupom: String?): Pedido {
        val carrinho = carrinhoService.buscarOuCriarCarrinho(idUsuario)
        if (carrinho.itens.isEmpty()) {
            throw NegocioException("Não é possível calcular a prévia: o carrinho está vazio.")
        }

        val valorItens = carrinho.calcularValorTotalItens()

        val valorFrete = servicoDeFrete.calcularFrete(cepDestino, carrinho)

        var valorDesconto = BigDecimal.ZERO

        if (cupom?.uppercase() == CUPOM_VALIDO) {
            valorDesconto = valorItens.multiply(PORCENTAGEM_DESCONTO)
            if (valorDesconto.compareTo(valorItens) > 0) {
                valorDesconto = valorItens
            }
        }

        val valorTotal = valorItens.add(valorFrete).subtract(valorDesconto)

        return Pedido(
            idUsuario = idUsuario,
            itens = carrinho.itens.map { item ->
                ItemDoPedido(
                    idProduto = item.idProduto,
                    nomeProduto = item.nomeProduto,
                    precoUnitario = item.precoUnitario,
                    urlFoto = item.urlFoto,
                    quantidade = item.quantidade
                )
            },
            valorItens = valorItens,
            valorFrete = valorFrete,
            valorDesconto = valorDesconto,
            valorTotal = valorTotal
        )
    }

    override fun finalizarCheckout(
        idUsuario: String,
        enderecoEntrega: Endereco,
        metodoPagamento: String,
        dadosPagamento: Map<String, Any>,
        cupom: String?
    ): Pedido {

        val previa = calcularPreviaDoPedido(idUsuario, enderecoEntrega.cep, cupom)
        val carrinho = carrinhoService.buscarOuCriarCarrinho(idUsuario)

        var novoPedido = previa.copy(
            enderecoEntrega = enderecoEntrega,
            metodoPagamento = metodoPagamento,
            status = StatusPedido.AGUARDANDO_PAGAMENTO
        )
        novoPedido = pedidoRepository.salvar(novoPedido)

        try {
            val idTransacao = servicoDePagamento.processarPagamento(novoPedido, dadosPagamento)
            novoPedido = novoPedido.copy(idTransacaoGateway = idTransacao)

            val statusPagamento = servicoDePagamento.verificarStatusPagamento(idTransacao)

            if (statusPagamento == StatusPedido.PAGAMENTO_APROVADO) {
                for (item in carrinho.itens) {
                    val produtoAtual = produtoService.buscarProdutoPorId(item.idProduto)
                    produtoService.atualizarEstoque(
                        item.idProduto,
                        produtoAtual.estoque - item.quantidade // Subtrai a quantidade do estoque
                    )
                }

                carrinhoService.limparCarrinho(idUsuario)
            }

            novoPedido = pedidoRepository.atualizarStatus(novoPedido.id!!, statusPagamento)

            return novoPedido

        } catch (e: NegocioException) {
            pedidoRepository.atualizarStatus(novoPedido.id!!, StatusPedido.PAGAMENTO_RECUSADO)
            throw NegocioException("Falha ao processar o pagamento: ${e.message}")
        }
    }

    override fun buscarPedidoPorId(id: String): Pedido {
        return pedidoRepository.buscarPorId(id) ?: throw RecursoNaoEncontradoException("Pedido com ID '$id' não encontrado.")
    }

    override fun listarPedidosPorUsuario(idUsuario: String): List<Pedido> {
        return pedidoRepository.buscarPorUsuario(idUsuario)
    }

    override fun atualizarStatusPedido(id: String, novoStatus: StatusPedido): Pedido {

        println("Aviso: Disparando notificação para o pedido $id. Novo status: $novoStatus")

        return pedidoRepository.atualizarStatus(id, novoStatus)
    }
}