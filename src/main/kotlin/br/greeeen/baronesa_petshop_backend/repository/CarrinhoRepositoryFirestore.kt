package br.greeeen.baronesa_petshop_backend.repository

import br.greeeen.baronesa_petshop_backend.model.CarrinhoDeCompras
import com.google.cloud.firestore.Firestore
import org.springframework.stereotype.Component

@Component
class CarrinhoRepositoryFirestore(
    private val firestore: Firestore
) : CarrinhoRepository {

    private val NOMECOLECAO = "carrinhos"

    override fun buscarPorId(idUsuario: String): CarrinhoDeCompras? {
        val documento = firestore.collection(NOMECOLECAO).document(idUsuario).get().get()
        return documento.toObject(CarrinhoDeCompras::class.java)
    }

    override fun salvar(carrinho: CarrinhoDeCompras): CarrinhoDeCompras {
        val idUsuario = carrinho.id ?: throw IllegalArgumentException("O ID do usuário (ID do carrinho) é obrigatório.")

        firestore.collection(NOMECOLECAO).document(idUsuario).set(carrinho).get()
        return carrinho
    }

    override fun deletar(idUsuario: String) {
        firestore.collection(NOMECOLECAO).document(idUsuario).delete().get()
    }
}