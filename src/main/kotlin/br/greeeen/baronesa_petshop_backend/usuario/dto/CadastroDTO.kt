package br.greeeen.baronesa_petshop_backend.usuario.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// Cadastro
data class RequisicaoCadastroCliente(
    @field:NotBlank(message = "Nome não pode estar em branco")
    val nome: String,

    @field:NotBlank(message = "Email não pode estar em branco")
    @field:Email(message = "Formato de email inválido")
    val email: String,

    @field:NotBlank(message = "Senha não pode estar em branco")
    @field:Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    val senha: String
)