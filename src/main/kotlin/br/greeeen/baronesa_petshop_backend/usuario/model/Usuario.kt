package br.greeeen.baronesa_petshop_backend.usuario.model

import br.greeeen.baronesa_petshop_backend.usuario.model.Endereco
import com.google.cloud.firestore.annotation.DocumentId
import com.google.cloud.firestore.annotation.ServerTimestamp
import java.util.Date

data class Usuario(
    @DocumentId
    var id: String? = null,
    var nome: String = "",
    var email: String = "",
    var telefone: String? = null,
    var enderecos: List<Endereco> = emptyList(),

    @ServerTimestamp
    var dataCriacao: Date? = null,

    @ServerTimestamp
    var dataAtualizacao: Date? = null
)

