package br.greeeen.baronesa_petshop_backend.model

import com.google.cloud.firestore.annotation.DocumentId
import com.google.cloud.firestore.annotation.ServerTimestamp
import java.math.BigDecimal
import java.util.Date

data class Produto(
    @DocumentId
    var id: String? = null,

    var nome: String = "",
    var descricao: String = "",
    var preco: Double  = 0.0,
    var estoque: Int = 0,
    var categorias: List<String> = emptyList(),
    var fotos: List<String> = emptyList(),
    var especificacoes: Map<String, String> = emptyMap(), // Ex: "Peso": "2kg", "Sabor": "Frango"
    var marca: String = "",

    @ServerTimestamp
    var dataCriacao: Date? = null,

    @ServerTimestamp
    var dataAtualizacao: Date? = null
)