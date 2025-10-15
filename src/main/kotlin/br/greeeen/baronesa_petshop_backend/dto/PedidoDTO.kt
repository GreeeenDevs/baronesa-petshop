package br.greeeen.baronesa_petshop_backend.dto

import br.greeeen.baronesa_petshop_backend.model.Endereco
import br.greeeen.baronesa_petshop_backend.model.ItemDoPedido
import br.greeeen.baronesa_petshop_backend.model.Pedido
import br.greeeen.baronesa_petshop_backend.enum.StatusPedido
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.Date

data class ItemDoPedidoDTO(
    val idProduto: String,
    val nomeProduto: String,
    val precoUnitario: Double,
    val quantidade: Int,
    val subtotal: BigDecimal
) {
    companion object {
        fun deModelo(item: ItemDoPedido): ItemDoPedidoDTO {
            return ItemDoPedidoDTO(
                idProduto = item.idProduto,
                nomeProduto = item.nomeProduto,
                precoUnitario = item.precoUnitario,
                quantidade = item.quantidade,
                subtotal = item.calcularSubtotal()
            )
        }
    }
}

data class PedidoRespostaDTO(
    val id: String,
    val status: StatusPedido,
    val itens: List<ItemDoPedidoDTO>,
    val valorItens: BigDecimal,
    val valorFrete: BigDecimal,
    val valorDesconto: BigDecimal,
    val valorTotal: BigDecimal,
    val enderecoEntrega: Endereco,
    val metodoPagamento: String,
    val codigoRastreio: String?,
    val dataCriacao: Date?
) {
    companion object {
        fun deModelo(pedido: Pedido): PedidoRespostaDTO {
            return PedidoRespostaDTO(
                id = pedido.id ?: throw IllegalStateException("ID do pedido não pode ser nulo."),
                status = pedido.status,
                itens = pedido.itens.map { ItemDoPedidoDTO.deModelo(it) },
                valorItens = pedido.valorItens,
                valorFrete = pedido.valorFrete,
                valorDesconto = pedido.valorDesconto,
                valorTotal = pedido.valorTotal,
                enderecoEntrega = pedido.enderecoEntrega ?: throw IllegalStateException("Endereço de entrega não pode ser nulo."),
                metodoPagamento = pedido.metodoPagamento,
                codigoRastreio = pedido.codigoRastreio,
                dataCriacao = pedido.dataCriacao
            )
        }
    }
}

data class PreviaCheckoutDTO(
    @field:NotBlank(message = "O CEP de destino é obrigatório.")
    val cepDestino: String,
    val cupom: String? = null
)

data class FinalizarCheckoutDTO(
    @field:NotBlank(message = "O ID do endereço de entrega é obrigatório.")
    val idEndereco: String,

    @field:NotBlank(message = "O método de pagamento é obrigatório.")
    val metodoPagamento: String, // Ex: "Cartão de Crédito", "Pix"

    @field:NotNull(message = "Os dados específicos do pagamento são obrigatórios.")
    val dadosPagamento: Map<String, Any>, // Ex: { "tokenCartao": "..." }

    val cupom: String? = null
)

data class AtualizacaoStatusDTO(
    @field:NotBlank(message = "O novo status é obrigatório.")
    val novoStatus: String
)