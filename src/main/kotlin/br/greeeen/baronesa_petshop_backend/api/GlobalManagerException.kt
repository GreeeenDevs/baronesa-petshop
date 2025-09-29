package br.greeeen.baronesa_petshop_backend.api

import br.greeeen.baronesa_petshop_backend.exception.NegocioException
import br.greeeen.baronesa_petshop_backend.exception.RecursoNaoEncontradoException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
class GlobalManagerException {
    data class RespostaDeErro(
        val timestamp: LocalDateTime = LocalDateTime.now(),
        val status: Int,
        val erro: String,
        val mensagem: String,
        val detalhes: Map<String, String?>? = null
    )

    @ExceptionHandler(RecursoNaoEncontradoException::class)
    fun manipularRecursoNaoEncontrado(ex: RecursoNaoEncontradoException): ResponseEntity<RespostaDeErro> {
        val status = HttpStatus.NOT_FOUND
        val erro = RespostaDeErro(
            status = status.value(),
            erro = status.reasonPhrase,
            mensagem = ex.message ?: "Recurso não encontrado."
        )
        return ResponseEntity(erro, status)
    }

    @ExceptionHandler(NegocioException::class)
    fun manipularExcecaoDeNegocio(ex: NegocioException): ResponseEntity<RespostaDeErro> {
        val status = HttpStatus.BAD_REQUEST
        val erro = RespostaDeErro(
            status = status.value(),
            erro = "Regra de Negócio Violada",
            mensagem = ex.message ?: "Falha na validação de negócio."
        )
        return ResponseEntity(erro, status)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun manipularValidacaoDeArgumento(ex: MethodArgumentNotValidException): ResponseEntity<RespostaDeErro> {
        val status = HttpStatus.BAD_REQUEST

        // Mapeia todos os erros de campo para um mapa de detalhes
        val detalhes = ex.bindingResult.fieldErrors.associate {
            it.field to it.defaultMessage
        }

        val erro = RespostaDeErro(
            status = status.value(),
            erro = "Dados de Entrada Inválidos",
            mensagem = "A requisição falhou devido a erros de validação.",
            detalhes = detalhes
        )
        return ResponseEntity(erro, status)
    }
}