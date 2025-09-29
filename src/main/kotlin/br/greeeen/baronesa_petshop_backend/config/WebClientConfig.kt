package br.greeeen.baronesa_petshop_backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * Configuração para o WebClient, cliente HTTP não-bloqueante e reativo.
 * Usa as propriedades injetadas para configurar URLs e tokens de integração.
 */
@Configuration
class WebClientConfig {

    @Value("\${integracao.frete.base-url}")
    private lateinit var urlFrete: String

    @Value("\${integracao.frete.token}")
    private lateinit var tokenFrete: String

    @Value("\${integracao.pagamento.base-url}")
    private lateinit var urlPagamento: String

    @Value("\${integracao.pagamento.access-token}")
    private lateinit var tokenPagamento: String

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder.codecs { it.defaultCodecs().maxInMemorySize(16 * 1024 * 1024) }.build()
    }

    /**
     * WebClient para integração com serviços externos de Frete (ex: Correios/Kangu).
     */
    @Bean(name = ["webClientFrete"])
    fun webClientFrete(builder: WebClient.Builder): WebClient {
        return builder.baseUrl(urlFrete)
            .defaultHeader("Accept", "application/json")
            .defaultHeader("Authorization", "Bearer $tokenFrete")
            .build()
    }

    /**
     * WebClient para integração com o Gateway de Pagamento (ex: Mercado Pago).
     */
    @Bean(name = ["webClientPagamento"])
    fun webClientPagamento(builder: WebClient.Builder): WebClient {
        return builder.baseUrl(urlPagamento)
            .defaultHeader("Content-Type", "application/json")
            .defaultHeader("Authorization", "Bearer $tokenPagamento")
            .build()
    }
}