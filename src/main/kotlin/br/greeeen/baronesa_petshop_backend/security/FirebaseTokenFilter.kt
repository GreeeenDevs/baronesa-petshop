package br.greeeen.baronesa_petshop_backend.security

import com.google.firebase.auth.FirebaseAuth
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class FirebaseTokenFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response)
            return
        }

        val header = request.getHeader("Authorization")

        if (header.isNullOrBlank() || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val idToken = header.substring(7)

            val tokenDecodificado = FirebaseAuth.getInstance().verifyIdToken(idToken)

            val autenticacao = UserPrinciple(tokenDecodificado)

            SecurityContextHolder.getContext().authentication = autenticacao

        } catch (e: Exception) {
            println("Falha na autenticação do token Firebase: ${e.message}")
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }
}