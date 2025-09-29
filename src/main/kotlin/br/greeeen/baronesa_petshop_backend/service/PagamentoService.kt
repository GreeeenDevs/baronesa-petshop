package br.greeeen.baronesa_petshop_backend.service

import br.greeeen.baronesa_petshop_backend.model.Pedido
import br.greeeen.baronesa_petshop_backend.enum.StatusPedido

interface PagamentoService {

    fun processarPagamento(pedido: Pedido, dadosPagamento: Map<String, Any>): String

    fun verificarStatusPagamento(idTransacao: String): StatusPedido
}