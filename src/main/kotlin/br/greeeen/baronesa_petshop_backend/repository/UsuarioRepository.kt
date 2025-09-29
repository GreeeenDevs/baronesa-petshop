package br.greeeen.baronesa_petshop_backend.repository

import br.greeeen.baronesa_petshop_backend.model.Usuario
import org.springframework.stereotype.Repository

@Repository
interface UsuarioRepository {
    fun buscarPorId(id: String): Usuario?

    fun buscarPorEmail(email: String): Usuario?

    fun salvar(usuario: Usuario): Usuario

    fun deletar(id: String)
}