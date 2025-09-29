package br.greeeen.baronesa_petshop_backend.repository

import br.greeeen.baronesa_petshop_backend.model.Produto
import com.google.cloud.firestore.Firestore
import org.springframework.stereotype.Component

@Component
class ProdutoRepositoryFirestore(
    private val firestore: Firestore
) : ProdutoRepository {

    private val NOMECOLECAO = "produtos"

    override fun salvar(produto: Produto): Produto {
        return if (produto.id.isNullOrBlank()) {
            val referencia = firestore.collection(NOMECOLECAO).add(produto).get()
            produto.copy(id = referencia.id)
        } else {
            firestore.collection(NOMECOLECAO).document(produto.id!!).set(produto).get()
            produto
        }
    }

    override fun buscarPorId(id: String): Produto? {
        val documento = firestore.collection(NOMECOLECAO).document(id).get().get()
        return documento.toObject(Produto::class.java)
    }

    override fun buscarTodos(): List<Produto> {
        val resultado = firestore.collection(NOMECOLECAO).get().get()
        return resultado.documents.mapNotNull { it.toObject(Produto::class.java) }
    }

    override fun buscarPorTermo(termo: String): List<Produto> {
        val termoMinusculo = termo.lowercase()
        val resultado = firestore.collection(NOMECOLECAO)
            .whereGreaterThanOrEqualTo("nome", termoMinusculo)
            .whereLessThanOrEqualTo("nome", termoMinusculo + '\uf8ff')
            .get()
            .get()

        return resultado.documents.mapNotNull { it.toObject(Produto::class.java) }
    }

    override fun deletar(id: String) {
        firestore.collection(NOMECOLECAO).document(id).delete().get()
    }
}