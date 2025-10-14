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
                    // Endpoints Públicos
                    .requestMatchers(HttpMethod.POST, "/api/v1/clientes/registro").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/produtos/**").permitAll()

                    // Endpoints de Administrador
                    .requestMatchers(HttpMethod.POST, "/api/v1/produtos/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/produtos/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/produtos/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/produtos/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/v1/clientes").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/clientes/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/pedidos/admin/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/pedidos/**").hasRole("ADMIN")


                    // Endpoints de Cliente e Administrador
                    .requestMatchers("/api/v1/clientes/**").hasAnyRole("CLIENTE", "ADMIN")
                    .requestMatchers("/api/v1/carrinho/**").hasAnyRole("CLIENTE", "ADMIN")
                    .requestMatchers("/api/v1/pedidos/**").hasAnyRole("CLIENTE", "ADMIN")


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