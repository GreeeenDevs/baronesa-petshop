package br.greeeen.baronesa_petshop_backend.model

import com.google.cloud.firestore.annotation.DocumentId
import com.google.cloud.firestore.annotation.ServerTimestamp
import java.math.BigDecimal
import java.util.Date

data class CarrinhoDeCompras(
    @DocumentId
    var id: String? = null,
    var itens: List<ItemDoCarrinho> = emptyList(),

    @ServerTimestamp
    var dataAtualizacao: Date? = null
) {
    fun calcularValorTotalItens(): BigDecimal {
        return itens.sumOf { it.calcularSubtotal() }
    }
}