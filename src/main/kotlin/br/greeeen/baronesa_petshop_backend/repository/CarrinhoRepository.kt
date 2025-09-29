package br.greeeen.baronesa_petshop_backend.repository

import br.greeeen.baronesa_petshop_backend.model.CarrinhoDeCompras
import org.springframework.stereotype.Repository

@Repository
interface CarrinhoRepository {

    fun buscarPorId(idUsuario: String): CarrinhoDeCompras?

    fun salvar(carrinho: CarrinhoDeCompras): CarrinhoDeCompras

    fun deletar(idUsuario: String)
}