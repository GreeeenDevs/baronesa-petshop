package br.greeeen.baronesa_petshop_backend.api.pagamento

import br.greeeen.baronesa_petshop_backend.exception.NegocioException
import br.greeeen.baronesa_petshop_backend.api.pagamento.dto.TransacaoRequisicaoDTO
import br.greeeen.baronesa_petshop_backend.api.pagamento.dto.TransacaoRespostaDTO
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.UUID

//@Qualifier("webClientPagamento") private val webClientPagamento: WebClient

@Component
class ClientePagamento() {

    fun processar(requisicao: TransacaoRequisicaoDTO): TransacaoRespostaDTO {
        println("Aviso: A chamada à API de Pagamento está desabilitada. Usando valor mockado.")
        return TransacaoRespostaDTO(
            idTransacaoGateway = "MOCK-TRANSACTION-${UUID.randomUUID()}",
            statusTransacao = "APPROVED",
            mensagem = "Pagamento mockado com sucesso."
        )
    }
}