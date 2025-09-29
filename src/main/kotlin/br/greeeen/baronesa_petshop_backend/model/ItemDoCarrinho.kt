package br.greeeen.baronesa_petshop_backend.model

import java.math.BigDecimal

data class ItemDoCarrinho(
    var idProduto: String = "",

    var nomeProduto: String = "",
    var precoUnitario: BigDecimal = BigDecimal.ZERO,
    var urlFoto: String? = null,
    var quantidade: Int = 1
) {
    fun calcularSubtotal(): BigDecimal {
        return precoUnitario.multiply(quantidade.toBigDecimal())
    }
}