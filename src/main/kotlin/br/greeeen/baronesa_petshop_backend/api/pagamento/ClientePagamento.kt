package br.greeeen.baronesa_petshop_backend.api.pagamento

import br.greeeen.baronesa_petshop_backend.exception.NegocioException
import br.greeeen.baronesa_petshop_backend.api.pagamento.dto.TransacaoRequisicaoDTO
import br.greeeen.baronesa_petshop_backend.api.pagamento.dto.TransacaoRespostaDTO
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class ClientePagamento(
    @Qualifier("webClientPagamento") private val webClientPagamento: WebClient
) {

    fun processar(requisicao: TransacaoRequisicaoDTO): TransacaoRespostaDTO {
        return webClientPagamento.post()
            .uri("/payments") // Endpoint real do Mercado Pago ou outro gateway
            .bodyValue(requisicao)
            .retrieve()
            .onStatus({ status -> status.isError }) { response ->
                response.bodyToMono(String::class.java).flatMap { erroCorpo ->
                    Mono.error(NegocioException("Recusa de Pagamento (Status ${response.statusCode()}): ${erroCorpo}"))
                }
            }
            .bodyToMono(TransacaoRespostaDTO::class.java)
            .block() ?: throw NegocioException("Resposta inválida ou nula da API de Pagamento.")
    }
}