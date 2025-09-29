package br.greeeen.baronesa_petshop_backend.model

import br.greeeen.baronesa_petshop_backend.enum.StatusPedido
import com.google.cloud.firestore.annotation.DocumentId
import com.google.cloud.firestore.annotation.ServerTimestamp
import java.math.BigDecimal
import java.util.Date

data class Pedido(
    @DocumentId
    var id: String? = null,
    var idUsuario: String = "",
    var status: StatusPedido = StatusPedido.AGUARDANDO_PAGAMENTO,
    var itens: List<ItemDoPedido> = emptyList(),
    var valorItens: BigDecimal = BigDecimal.ZERO,
    var valorFrete: BigDecimal = BigDecimal.ZERO,
    var valorDesconto: BigDecimal = BigDecimal.ZERO,
    var valorTotal: BigDecimal = BigDecimal.ZERO,
    var enderecoEntrega: Endereco? = null,
    var codigoRastreio: String? = null,
    var metodoPagamento: String = "",
    var idTransacaoGateway: String? = null,

    @ServerTimestamp
    var dataCriacao: Date? = null,

    @ServerTimestamp
    var dataAtualizacao: Date? = null
)