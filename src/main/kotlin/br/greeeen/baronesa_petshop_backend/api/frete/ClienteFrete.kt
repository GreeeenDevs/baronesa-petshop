package br.greeeen.baronesa_petshop_backend.api.frete

import br.greeeen.baronesa_petshop_backend.exception.NegocioException
import br.greeeen.baronesa_petshop_backend.api.frete.dto.CalculoFreteRespostaDTO
import br.greeeen.baronesa_petshop_backend.api.frete.dto.FreteRequisicaoDTO
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class ClienteFrete(
    @Qualifier("webClientFrete") private val webClientFrete: WebClient
) {

    fun calcularFrete(requisicao: FreteRequisicaoDTO): CalculoFreteRespostaDTO {

        return webClientFrete.post()
            .uri("/calcular") // Endpoint específico da API de frete (Ex: /cotacao, /servicos)
            .bodyValue(requisicao)
            .retrieve()
            .onStatus({ status -> status.isError }) { response ->
                response.bodyToMono(String::class.java).flatMap { erroCorpo ->
                    Mono.error(NegocioException("Erro na API de Frete (Status ${response.statusCode()}): $erroCorpo"))
                }
            }
            .bodyToMono(CalculoFreteRespostaDTO::class.java)
            .block()
            ?: throw NegocioException("Resposta inválida ou nula da API de Frete.")
    }
}