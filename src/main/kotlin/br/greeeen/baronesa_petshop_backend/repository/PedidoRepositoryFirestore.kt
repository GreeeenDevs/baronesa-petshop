package br.greeeen.baronesa_petshop_backend.repository

import br.greeeen.baronesa_petshop_backend.exception.RecursoNaoEncontradoException
import br.greeeen.baronesa_petshop_backend.model.Pedido
import br.greeeen.baronesa_petshop_backend.model.StatusPedido
import com.google.cloud.firestore.Firestore
import org.springframework.stereotype.Component
import java.util.Date

@Component
class PedidoRepositoryFirestore(
    private val firestore: Firestore
) : PedidoRepository {

    private val NOMECOLECAO = "pedidos"

    override fun salvar(pedido: Pedido): Pedido {
        return if (pedido.id.isNullOrBlank()) {
            val referencia = firestore.collection(NOMECOLECAO).add(pedido).get()
            pedido.copy(id = referencia.id)
        } else {
            firestore.collection(NOMECOLECAO).document(pedido.id!!).set(pedido).get()
            pedido
        }
    }

    override fun buscarPorId(id: String): Pedido? {
        val documento = firestore.collection(NOMECOLECAO).document(id).get().get()
        return documento.toObject(Pedido::class.java)
    }

    override fun buscarPorUsuario(idUsuario: String): List<Pedido> {
        val resultado = firestore.collection(NOMECOLECAO)
            .whereEqualTo("idUsuario", idUsuario)
            .orderBy("dataCriacao", com.google.cloud.firestore.Query.Direction.DESCENDING)
            .get()
            .get()

        return resultado.documents.mapNotNull { it.toObject(Pedido::class.java) }
    }

    override fun buscarTodos(): List<Pedido> {
        val resultado = firestore.collection(NOMECOLECAO)
            .orderBy("dataCriacao", com.google.cloud.firestore.Query.Direction.DESCENDING)
            .get()
            .get()

        return resultado.documents.mapNotNull { it.toObject(Pedido::class.java) }
    }

    override fun atualizarStatus(id: String, novoStatus: StatusPedido): Pedido {
        val pedidoExistente = buscarPorId(id)
            ?: throw RecursoNaoEncontradoException("Pedido com ID '$id' não encontrado para atualização de status.")

        val atualizacoes = mapOf(
            "status" to novoStatus,
            "dataAtualizacao" to Date()
        )

        firestore.collection(NOMECOLECAO).document(id).update(atualizacoes).get()

        return pedidoExistente.copy(status = novoStatus)
    }
}