package br.greeeen.baronesa_petshop_backend.repository

import br.greeeen.baronesa_petshop_backend.model.Usuario
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import org.springframework.stereotype.Component

@Component
class UsuarioFirestoreRepository(
    private val firestore: Firestore
) : UsuarioRepository {

    private val NOMECOLECAO = "usuarios"

    override fun buscarPorId(id: String): Usuario? {
        val documento = firestore.collection(NOMECOLECAO).document(id).get().get()
        return documento.toObject(Usuario::class.java)
    }

    override fun buscarPorEmail(email: String): Usuario? {
        val resultado = firestore.collection(NOMECOLECAO)
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .get()

        if (resultado.isEmpty) {
            return null
        }
        return resultado.documents.first().toObject(Usuario::class.java)
    }

    override fun salvar(usuario: Usuario): Usuario {
        return if (usuario.id.isNullOrBlank()) {
            val referencia = firestore.collection(NOMECOLECAO).add(usuario).get()

            usuario.copy(id = referencia.id)
        } else {
            firestore.collection(NOMECOLECAO).document(usuario.id!!).set(usuario).get()
            usuario
        }
    }

    override fun deletar(id: String) {
        firestore.collection(NOMECOLECAO).document(id).delete().get()
    }
}