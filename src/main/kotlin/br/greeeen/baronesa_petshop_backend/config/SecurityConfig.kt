package br.greeeen.baronesa_petshop_backend.config

import br.greeeen.baronesa_petshop_backend.security.JwtAuthenticationEntryPoint
import br.greeeen.baronesa_petshop_backend.security.JwtAuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita anotações como @PreAuthorize
class SecurityConfig(
    // As injeções de JwtAuthenticationEntryPoint e JwtAuthenticationFilter
    // podem causar um problema de dependência cíclica se eles também dependerem
    // de beans criados aqui. Vamos adiar a injeção deles ou usar @Lazy.
    // Por enquanto, vou remover a injeção direta no construtor para simplificar
    // e focaremos em fazer o Spring Security carregar.
    // @Autowired private val jwtEntryPoint: JwtAuthenticationEntryPoint,
    // @Autowired private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    // Injetaremos os componentes JWT aqui para serem usados no securityFilterChain
    @Autowired
    private lateinit var jwtEntryPoint: JwtAuthenticationEntryPoint

    @Autowired
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOriginPattern("*") // ATENÇÃO: Em produção, restrinja para o seu domínio do frontend!
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() }
            .cors { cors -> cors.filter(corsFilter()) } // Aplicar o filtro CORS explicitamente
            .exceptionHandling { exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(jwtEntryPoint)
            }
            .sessionManagement { sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { authorizeRequests ->
                authorizeRequests
                    .requestMatchers(
                        "/api/auth/registrar",
                        "/api/auth/login",
                        "/api/auth/esqueci-senha",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                    ).permitAll()
                    .anyRequest().authenticated()
            }

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}