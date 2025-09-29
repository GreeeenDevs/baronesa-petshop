package br.greeeen.baronesa_petshop_backend.api.pagamento.dto

import java.math.BigDecimal

data class TransacaoRequisicaoDTO(
    val idExterno: String,
    val valor: BigDecimal,
    val metodoPagamento: String,
    val dadosCliente: DadosClienteDTO,
    val dadosEspecíficos: Map<String, Any>
)

data class DadosClienteDTO(
    val nome: String,
    val email: String,
    val telefone: String?
)

data class TransacaoRespostaDTO(
    val idTransacaoGateway: String,
    val statusTransacao: String, // Ex: "approved", "pending", "rejected"
    val mensagem: String? = null
)