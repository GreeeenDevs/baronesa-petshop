package br.greeeen.baronesa_petshop_backend.usuario.model

data class Endereco(
    var id: String? = null, // Pode ser útil ter um ID para cada endereço se o usuário tiver muitos.
    var apelido: String? = null, // Ex: "Casa", "Trabalho"
    var cep: String = "",
    var logradouro: String = "",
    var numero: String = "",
    var complemento: String? = null,
    var bairro: String = "",
    var cidade: String = "",
    var estado: String = "", // Sigla do estado, ex: "SP"var principal: Boolean = false // Indica se é o endereço principal de entrega
)