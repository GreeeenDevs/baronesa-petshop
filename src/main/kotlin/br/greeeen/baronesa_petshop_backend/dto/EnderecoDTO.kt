package br.greeeen.baronesa_petshop_backend.dto

import br.greeeen.baronesa_petshop_backend.model.Endereco
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class EnderecoDTO(
    val id: String? = null,
    val apelido: String? = null,
    @field:NotBlank(message = "O CEP é obrigatório.")
    @field:Size(min = 8, max = 9, message = "O CEP deve ter 8 ou 9 dígitos.")
    val cep: String,
    val logradouro: String = "",
    val numero: String = "",
    val complemento: String? = null,
    val bairro: String = "",
    val cidade: String = "",
    val estado: String = "",
    val principal: Boolean = false
) {
    fun paraModelo(): Endereco {
        return Endereco(
            id = this.id,
            apelido = this.apelido,
            cep = this.cep,
            logradouro = this.logradouro,
            numero = this.numero,
            complemento = this.complemento,
            bairro = this.bairro,
            cidade = this.cidade,
            estado = this.estado,
            principal = this.principal
        )
    }

    companion object {
        fun deModelo(endereco: Endereco): EnderecoDTO {
            return EnderecoDTO(
                id = endereco.id,
                apelido = endereco.apelido,
                cep = endereco.cep,
                logradouro = endereco.logradouro,
                numero = endereco.numero,
                complemento = endereco.complemento,
                bairro = endereco.bairro,
                cidade = endereco.cidade,
                estado = endereco.estado,
                principal = endereco.principal
            )
        }
    }
}
