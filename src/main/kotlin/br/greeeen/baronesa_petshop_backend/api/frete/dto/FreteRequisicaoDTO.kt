package br.greeeen.baronesa_petshop_backend.api.frete.dto

import java.math.BigDecimal

data class FreteRequisicaoDTO(
    val cepOrigem: String,
    val cepDestino: String,
    val pesoTotalKg: BigDecimal,
    val valorTotalItens: BigDecimal
)

data class FreteRespostaDTO(
    val nomeServico: String, // Ex: "SEDEX", "PAC", "Baronesa Express"
    val valorFrete: BigDecimal,
    val prazoDias: Int
)

data class CalculoFreteRespostaDTO(
    val servicosDisponiveis: List<FreteRespostaDTO>
)