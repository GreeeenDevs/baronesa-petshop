package br.greeeen.baronesa_petshop_backend.dto

import br.greeeen.baronesa_petshop_backend.model.Usuario
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.util.Date

data class RegistroUsuarioDTO(
    @field:NotBlank(message = "O nome é obrigatório.")
    val nome: String,

    @field:NotBlank(message = "O e-mail é obrigatório.")
    @field:Email(message = "O e-mail deve ser válido.")
    val email: String,

    val telefone: String? = null,
    val enderecos: List<EnderecoDTO> = emptyList()
) {
    fun paraModelo(): Usuario {
        return Usuario(
            nome = this.nome,
            email = this.email,
            telefone = this.telefone,
            enderecos = this.enderecos.map { it.paraModelo() }
        )
    }
}

data class RespostaUsuarioDTO(
    val id: String,
    val nome: String,
    val email: String,
    val telefone: String? = null,
    val enderecos: List<EnderecoDTO> = emptyList(),
    val dataCriacao: Date? = null
) {
    companion object {
        fun deModelo(usuario: Usuario): RespostaUsuarioDTO {
            return RespostaUsuarioDTO(
                id = usuario.id ?: throw IllegalStateException("ID do usuário não pode ser nulo na resposta."),
                nome = usuario.nome,
                email = usuario.email,
                telefone = usuario.telefone,
                enderecos = usuario.enderecos.map { EnderecoDTO.deModelo(it) },
                dataCriacao = usuario.dataCriacao
            )
        }
    }
}

data class AtualizacaoUsuarioDTO(
    @field:NotBlank(message = "O nome é obrigatório.")
    val nome: String,

    @field:NotBlank(message = "O e-mail é obrigatório.")
    @field:Email(message = "O e-mail deve ser válido.")
    val email: String,

    val telefone: String? = null
)
