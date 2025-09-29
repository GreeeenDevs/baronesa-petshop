package br.greeeen.baronesa_petshop_backend.security

import com.google.firebase.auth.FirebaseToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class UserPrinciple(
    private val token: FirebaseToken,
    private val estaAutenticado: Boolean = true
) : Authentication {

    override fun getName(): String {
        return token.uid
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val claims = token.claims
        val authorities = mutableListOf<GrantedAuthority>()

        if (claims["admin"] == true) {
            authorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))
        }

        authorities.add(SimpleGrantedAuthority("ROLE_CLIENTE"))

        return authorities
    }

    override fun getCredentials(): Any {
        return token.name
    }

    override fun getDetails(): Any {
        return token
    }

    override fun getPrincipal(): Any {
        return token.uid
    }

    override fun isAuthenticated(): Boolean {
        return estaAutenticado
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        throw IllegalArgumentException("Não é permitido modificar o status de autenticação. Use um novo PrincipioDeUsuario.")
    }
}