package br.greeeen.baronesa_petshop_backend.security

import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val filtroDeTokenFirebase: FirebaseTokenFilter
) {

    @Bean
    fun filtroDeCadeiaDeSeguranca(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(HttpMethod.POST, "/api/v1/clientes/registro").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/produtos/**").permitAll()
                    .requestMatchers("/api/v1/produtos/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/clientes/**").hasAnyRole("CLIENTE", "ADMIN")
                    .anyRequest().authenticated()
            }

            .addFilterBefore(
                filtroDeTokenFirebase,
                UsernamePasswordAuthenticationFilter::class.java
            )

            .exceptionHandling {
                it.accessDeniedHandler { request, response, accessDeniedException ->
                    response.status = HttpServletResponse.SC_FORBIDDEN
                    response.writer.write("Acesso negado. Você não tem permissão para acessar este recurso.")
                }
            }

        return http.build()
    }
}