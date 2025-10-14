package br.greeeen.baronesa_petshop_backend.api.frete

import br.greeeen.baronesa_petshop_backend.exception.NegocioException
import br.greeeen.baronesa_petshop_backend.api.frete.dto.CalculoFreteRespostaDTO
import br.greeeen.baronesa_petshop_backend.api.frete.dto.FreteRequisicaoDTO
import br.greeeen.baronesa_petshop_backend.api.frete.dto.FreteRespostaDTO
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.math.BigDecimal

//@Qualifier("webClientFrete") private val webClientFrete: WebClient

@Component
class ClienteFrete() {

    fun calcularFrete(requisicao: FreteRequisicaoDTO): CalculoFreteRespostaDTO {
        println("Aviso: A chamada à API de Frete está desabilitada. Usando valor mockado.")
        val servicoMock = FreteRespostaDTO(
            nomeServico = "SEDEX Mock",
            valorFrete = BigDecimal("25.50"),
            prazoDias = 3
        )
        return CalculoFreteRespostaDTO(servicosDisponiveis = listOf(servicoMock))
    }
}