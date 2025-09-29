package br.greeeen.baronesa_petshop_backend.exception

class RecursoNaoEncontradoException (
    mensagem: String = "O recurso solicitado não foi encontrado."
) : RuntimeException(mensagem)
