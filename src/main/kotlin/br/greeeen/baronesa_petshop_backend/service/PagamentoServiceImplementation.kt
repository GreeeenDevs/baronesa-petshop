package br.greeeen.baronesa_petshop_backend.service

import br.greeeen.baronesa_petshop_backend.exception.NegocioException
import br.greeeen.baronesa_petshop_backend.model.Pedido
import br.greeeen.baronesa_petshop_backend.enum.StatusPedido
import br.greeeen.baronesa_petshop_backend.api.pagamento.ClientePagamento
import br.greeeen.baronesa_petshop_backend.api.pagamento.dto.DadosClienteDTO
import br.greeeen.baronesa_petshop_backend.api.pagamento.dto.TransacaoRequisicaoDTO
import br.greeeen.baronesa_petshop_backend.servico.UsuarioService
import org.springframework.stereotype.Service


@Service
class ServicoDePagamentoImplementation(
    private val clientePagamento: ClientePagamento,
    private val servicoDeUsuario: UsuarioService,
) : PagamentoService {

    override fun processarPagamento(pedido: Pedido, dadosPagamento: Map<String, Any>): String {
        val usuario = servicoDeUsuario.buscarUsuarioPorId(pedido.idUsuario)

        val dadosCliente = DadosClienteDTO(
            nome = usuario.nome,
            email = usuario.email,
            telefone = usuario.telefone
        )

        val requisicao = TransacaoRequisicaoDTO(
            idExterno = pedido.id!!,
            valor = pedido.valorTotal,
            metodoPagamento = pedido.metodoPagamento,
            dadosCliente = dadosCliente,
            dadosEspecíficos = dadosPagamento
        )

        val resposta = clientePagamento.processar(requisicao)

        if (resposta.statusTransacao.uppercase() == "REJECTED") {
            throw NegocioException("Pagamento recusado pelo gateway: ${resposta.mensagem}")
        }

        return resposta.idTransacaoGateway
    }

    override fun verificarStatusPagamento(idTransacao: String): StatusPedido {

        val statusGateway = "approved"

        return when (statusGateway.uppercase()) {
            "APPROVED", "SETTLED" -> StatusPedido.PAGAMENTO_APROVADO
            "PENDING" -> StatusPedido.AGUARDANDO_PAGAMENTO
            "REJECTED", "CANCELLED" -> StatusPedido.PAGAMENTO_RECUSADO
            else -> StatusPedido.AGUARDANDO_PAGAMENTO
        }
    }
}