package br.greeeen.baronesa_petshop_backend.dto

import br.greeeen.baronesa_petshop_backend.model.CarrinhoDeCompras
import br.greeeen.baronesa_petshop_backend.model.ItemDoCarrinho
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class ItemCarrinhoDTO(
    val idProduto: String,
    val nomeProduto: String,
    val precoUnitario: Double,
    val urlFoto: String?,
    val quantidade: Int,
    val subtotal: BigDecimal
) {
    companion object {
        fun deModelo(item: ItemDoCarrinho): ItemCarrinhoDTO {
            return ItemCarrinhoDTO(
                idProduto = item.idProduto,
                nomeProduto = item.nomeProduto,
                precoUnitario = item.precoUnitario,
                urlFoto = item.urlFoto,
                quantidade = item.quantidade,
                subtotal = item.calcularSubtotal()
            )
        }
    }
}

data class CarrinhoRespostaDTO(
    val id: String,
    val itens: List<ItemCarrinhoDTO>,
    val valorTotal: BigDecimal
) {
    companion object {
        fun deModelo(carrinho: CarrinhoDeCompras): CarrinhoRespostaDTO {
            return CarrinhoRespostaDTO(
                id = carrinho.id ?: "",
                itens = carrinho.itens.map { ItemCarrinhoDTO.deModelo(it) },
                valorTotal = carrinho.calcularValorTotalItens()
            )
        }
    }
}

data class AdicionarItemRequestDTO(
    @field:NotBlank(message = "O ID do produto é obrigatório.")
    val idProduto: String,

    @field:Min(value = 1, message = "A quantidade deve ser de no mínimo 1.")
    val quantidade: Int = 1
)

data class AtualizarItemRequestDTO(
    @field:Min(value = 1, message = "A quantidade deve ser de no mínimo 1.")
    val quantidade: Int
)