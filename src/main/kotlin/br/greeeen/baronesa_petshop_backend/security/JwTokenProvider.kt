package br.greeeen.baronesa_petshop_backend.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException // Import explícito para clareza
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User // Do Spring Security
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider { // Removida injeção do FirebaseAuth do construtor por enquanto

    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    @Value("\${app.jwt.secret}")
    private lateinit var jwtSecretString: String

    @Value("\${app.jwt.expiration-ms}")
    private lateinit var jwtExpirationMsString: String

    private val jwtSecretKey: SecretKey by lazy {
        var keyBytes = jwtSecretString.toByteArray(Charsets.UTF_8) // Especificar Charset
        if (keyBytes.size < 32) {
            logger.warn(
                "JWT Secret Key ('app.jwt.secret') é menor que 32 bytes (256 bits). " +
                        "Isso é INSEGURO para HS256. A chave será preenchida, mas " +
                        "POR FAVOR, USE UMA CHAVE SECRETA FORTE E LONGA EM PRODUÇÃO (ex: gerada por um gerador de senhas, com pelo menos 32 caracteres aleatórios)."
            )
            // Preencher para 32 bytes não torna a chave original mais forte.
            // É crucial usar uma chave secreta forte desde o início.
            val newKeyBytes = ByteArray(32)
            System.arraycopy(keyBytes, 0, newKeyBytes, 0, minOf(keyBytes.size, newKeyBytes.size))
            keyBytes = newKeyBytes
        }
        Keys.hmacShaKeyFor(keyBytes)
    }

    private val jwtExpirationMs: Long by lazy {
        jwtExpirationMsString.toLong()
    }

    /**
     * Gera um token JWT para um usuário autenticado.
     * @param uid O ID único do usuário (ex: UID do Firebase).
     * @param email O email do usuário.
     * @param nome O nome do usuário.
     * @param authorities As permissões/roles do usuário.
     * @return O token JWT gerado.
     */
    fun gerarTokenInterno(uid: String, email: String, nome: String, authorities: Collection<GrantedAuthority>): String {
        val agora =Date()
        val dataExpiracao = Date(agora.time + jwtExpirationMs)

        val claims: Claims = Jwts.claims().setSubject(uid) // 'sub' (subject) é o UID
        claims["email"] = email
        claims["nome"] = nome
        // Armazena as roles como uma lista de strings para facilitar a recuperação
        claims["roles"] = authorities.map { it.authority }.toList()

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(agora)
            .setExpiration(dataExpiracao)
            .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    /**
     * Obtém o ID do usuário (UID) a partir do token JWT.
     */
    fun getUidFromJWT(token: String): String? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .body
            claims.subject
        } catch (ex: Exception) {
            logger.error("Não foi possível obter o UID do JWT: {}", ex.message)
            null
        }
    }

    /**
     * Valida o token JWT.
     */
    fun validarTokenInterno(authToken: String): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(authToken)
            return true
        } catch (ex: SignatureException) {
            logger.error("Assinatura JWT inválida: {}", ex.message)
        } catch (ex: MalformedJwtException) {
            logger.error("Token JWT inválido (malformado): {}", ex.message)
        } catch (ex: ExpiredJwtException) {
            logger.error("Token JWT expirado: {}", ex.message)
        } catch (ex: UnsupportedJwtException) {
            logger.error("Token JWT não suportado: {}", ex.message)
        } catch (ex: IllegalArgumentException) {
            logger.error("Argumento JWT (claims string) inválido ou vazio: {}", ex.message)
        }
        returnfalse
    }

    /**
     * Extrai o token do cabeçalho Authorization "Bearer <token>" de uma requisição HTTP.
     */
    fun extrairTokenDaRequisicao(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ", ignoreCase = true)) {
            return bearerToken.substring(7)
        }
        return null
    }

    /**
     * Cria um objeto Authentication para o Spring Security a partir de um token JWT válido.
     * Este método será usado pelo JwtAuthenticationFilter.
     */
    fun getAuthenticationFromJWT(token: String): Authentication? {
        if (!validarTokenInterno(token)) {
            return null
        }

        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .body

            val uid = claims.subject
            // val email = claims["email"] as String? ?: "" // Pode ser útil ter o email
            // val nome = claims["nome"] as String? ?: "" // E o nome

            @Suppress("UNCHECKED_CAST")
            val rolesString = claims["roles"] as? List<String> ?: emptyList()
            val authorities = rolesString.map { SimpleGrantedAuthority(it) }.toMutableList()

            // Se nenhuma role for encontrada no token, podemos adicionar uma role padrão.
            // Isso depende da sua lógica de autorização.
            if (authorities.isEmpty()) {
                authorities.add(SimpleGrantedAuthority("ROLE_USUARIO")) // Role padrão
            }

            // O User do Spring Security precisa de um username, password (pode ser vazio/dummy para JWT) e authorities.
            // Usaremos o UID como username para o objeto Principal do Spring Security.
            val userDetails = User(uid, "", authorities)

            UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        } catch (e: Exception) {
            logger.error("Erro ao obter autenticação do JWT: {}", e.message)
            null
        }
    }
}