package br.greeeen.baronesa_petshop_backend.service

import br.greeeen.baronesa_petshop_backend.api.frete.ClienteFrete
import br.greeeen.baronesa_petshop_backend.api.frete.dto.FreteRequisicaoDTO
import br.greeeen.baronesa_petshop_backend.model.CarrinhoDeCompras
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ServicoDeFreteImplementation(
    private val clienteFrete: ClienteFrete // Injeção do cliente HTTP
) : FreteService {

    @Value("\${integracao.frete.cep-origem}")
    private lateinit var cepOrigemCD: String

    private val LIMITE_FRETE_GRATIS = BigDecimal("250.00")

    override fun calcularFrete(cepDestino: String, carrinho: CarrinhoDeCompras): BigDecimal {
        if (carrinho.itens.isEmpty()) {
            return BigDecimal.ZERO
        }

        val totalItens = carrinho.calcularValorTotalItens()

        if (totalItens.compareTo(LIMITE_FRETE_GRATIS) >= 0) {
            println("Regra de Negócio: Frete Grátis aplicado. Total: $totalItens")
            return BigDecimal.ZERO
        }


        val pesoTotalKg = carrinho.itens.sumOf { it.quantidade.toBigDecimal().multiply(BigDecimal("0.5")) }

        val requisicao = FreteRequisicaoDTO(
            cepOrigem = cepOrigemCD,
            cepDestino = cepDestino,
            pesoTotalKg = pesoTotalKg,
            valorTotalItens = totalItens
        )

        val resposta = clienteFrete.calcularFrete(requisicao)

        val freteMaisBarato = resposta.servicosDisponiveis
            .minByOrNull { it.valorFrete }
            ?: throw IllegalStateException("Nenhum serviço de frete disponível para o CEP: $cepDestino")

        return freteMaisBarato.valorFrete
    }
}