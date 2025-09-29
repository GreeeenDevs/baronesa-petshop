package br.greeeen.baronesa_petshop_backend.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthenticationFilter : OncePerRequestFilter() { // Herda de OncePerRequestFilter

    @Autowired
    private lateinit var tokenProvider: JwtTokenProvider // Dependência do nosso provedor de token

    // Não vamos injetar UserDetailsService aqui diretamente por enquanto,
    // pois a autenticação virá das claims do token JWT gerado internamente.
    // Se precisássemos carregar UserDetails do banco a cada requisição (o que é menos comum com JWT puro),
    // aí sim injetaríamos um UserDetailsService.

    private val customLogger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java) // Renomeado para evitar conflito com logger herdado

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = tokenProvider.extrairTokenDaRequisicao(request)

            if (jwt != null && tokenProvider.validarTokenInterno(jwt)) {
                // Se o token for válido, configuramos a autenticação no contexto de segurança do Spring
                val authentication = tokenProvider.getAuthenticationFromJWT(jwt)

                if (authentication != null) {
                    // Define os detalhes da autenticação (como endereço IP, etc.)
                    // Isso é opcional, mas pode ser útil para auditoria ou lógica específica.
                    // No nosso caso, como o UserDetails é construído a partir do token,
                    // não temos um UserDetailsService tradicional para carregar o usuário.
                    // O UsernamePasswordAuthenticationToken já contém o UserDetails.
                    // A linha abaixo é mais relevante quando se usa um UserDetailsService.
                    // (authentication as UsernamePasswordAuthenticationToken).details = WebAuthenticationDetailsSource().buildDetails(request)

                    SecurityContextHolder.getContext().authentication = authentication
                    customLogger.debug("Autenticação JWT bem-sucedida para o usuário: {}", authentication.name)
                } else {
                    customLogger.warn("Falha ao obter objeto de autenticação do token JWT, mesmo o token sendo válido.")
                }
            }
        } catch (ex: Exception) {
            customLogger.error("Não foi possível definir a autenticação do usuário no contexto de segurança: {}", ex.message, ex)
            // Não relançar a exceção aqui para permitir que o request continue na cadeia de filtros,
            // onde o JwtAuthenticationEntryPoint tratará a falha de autenticação se necessário.
        }

        filterChain.doFilter(request, response) // Continua para o próximo filtro na cadeia
    }
}