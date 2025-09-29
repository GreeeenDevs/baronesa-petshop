package br.greeeen.baronesa_petshop_backend.usuario.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

// Login
data class RequisicaoLoginCliente(
    @field:NotBlank(message = "Email não pode estar em branco")
    @field:Email(message = "Formato de email inválido")
    val email: String,

    @field:NotBlank(message = "Senha não pode estar em branco")
    val senha: String
)

// Resposta para login/cadastro bem-sucedido
data class RespostaAutenticacao(
    val token: String, // JWT
    val idUsuario: String,
    val email: String,
    val nome: String
)

// Resposta genérica para mensagens
data class RespostaMensagem(
    val mensagem: String
)

// "Esqueci a senha"
data class RequisicaoEsqueciSenha(
    @field:NotBlank(message = "Email não pode estar em branco")
    @field:Email(message = "Formato de email inválido")
    val email: String
)