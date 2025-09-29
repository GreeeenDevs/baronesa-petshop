package br.greeeen.baronesa_petshop_backend.service

import br.greeeen.baronesa_petshop_backend.model.Pedido
import br.greeeen.baronesa_petshop_backend.enum.StatusPedido
import br.greeeen.baronesa_petshop_backend.model.Endereco

interface PedidoService {

    fun calcularPreviaDoPedido(idUsuario: String, cepDestino: String, cupom: String?): Pedido

    fun finalizarCheckout(
        idUsuario: String,
        enderecoEntrega: Endereco,
        metodoPagamento: String,
        dadosPagamento: Map<String, Any>,
        cupom: String?
    ): Pedido

    fun buscarPedidoPorId(id: String): Pedido

    fun listarPedidosPorUsuario(idUsuario: String): List<Pedido>

    fun atualizarStatusPedido(id: String, novoStatus: StatusPedido): Pedido
}