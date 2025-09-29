package br.greeeen.baronesa_petshop_backend.repository

import br.greeeen.baronesa_petshop_backend.model.Pedido
import br.greeeen.baronesa_petshop_backend.model.StatusPedido
import org.springframework.stereotype.Repository

@Repository
interface PedidoRepository {

    fun salvar(pedido: Pedido): Pedido

    fun buscarPorId(id: String): Pedido?

    fun buscarPorUsuario(idUsuario: String): List<Pedido>

    fun buscarTodos(): List<Pedido>

    fun atualizarStatus(id: String, novoStatus: StatusPedido): Pedido
}