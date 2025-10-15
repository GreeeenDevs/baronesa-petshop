package br.greeeen.baronesa_petshop_backend.model

import java.math.BigDecimal

//Snapshot dos itens do carrinho no momento do checkout

data class ItemDoPedido(
    var idProduto: String = "",
    var nomeProduto: String = "",
    var precoUnitario: Double = 0.0,
    var urlFoto: String? = null,
    var quantidade: Int = 1
) {
    fun calcularSubtotal(): BigDecimal {
        return precoUnitario.toBigDecimal().multiply(quantidade.toBigDecimal())
    }
}