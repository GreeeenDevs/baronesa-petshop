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
                    .requestMatchers(HttpMethod.POST, "/api/v1/produtos/filtrar").permitAll()
                    .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                    ).permitAll()

                    // Endpoints de Administrador
                    .requestMatchers(HttpMethod.POST, "/api/v1/produtos/**").permitAll() //.hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/produtos/**").permitAll() //.hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/produtos/**").permitAll() //.hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/produtos/**").permitAll() //.hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/v1/clientes").permitAll() //.hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/clientes/**").permitAll() //.hasRole("ADMIN")
                    .requestMatchers("/api/v1/pedidos/admin/**").permitAll() //.hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/pedidos/**").permitAll() //.hasRole("ADMIN")

                    // Endpoints de Cliente e Administrador
                    .requestMatchers("/api/v1/clientes/**").permitAll() //.hasAnyRole("CLIENTE", "ADMIN")
                    .requestMatchers("/api/v1/carrinho/**").permitAll() //.hasAnyRole("CLIENTE", "ADMIN")
                    .requestMatchers("/api/v1/pedidos/**").permitAll() //.hasAnyRole("CLIENTE", "ADMIN")

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




