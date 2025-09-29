package br.greeeen.baronesa_petshop_backend.controller

import br.greeeen.baronesa_petshop_backend.dto.EnderecoDTO
import br.greeeen.baronesa_petshop_backend.dto.RespostaUsuarioDTO
import br.greeeen.baronesa_petshop_backend.servico.UsuarioService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/clientes")
class EnderecoController(
    private  val servico: UsuarioService
) {

    @PostMapping("/{idUsuario}/enderecos")
    fun adicionarEndereco(@PathVariable idUsuario: String, @Valid @RequestBody enderecoDTO: EnderecoDTO): ResponseEntity<RespostaUsuarioDTO> {
        val novoEndereco = enderecoDTO.paraModelo()
        val usuarioAtualizado = servico.adicionarEndereco(idUsuario, novoEndereco)
        val respostaDTO = RespostaUsuarioDTO.deModelo(usuarioAtualizado)

        // Retorna o usuário completo para que o front-end veja o novo endereço
        return ResponseEntity.status(HttpStatus.CREATED).body(respostaDTO)
    }

    @DeleteMapping("/{idUsuario}/enderecos/{idEndereco}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Retorno 204 No Content para remoção bem-sucedida
    fun removerEndereco(@PathVariable idUsuario: String, @PathVariable idEndereco: String) {
        servico.removerEndereco(idUsuario, idEndereco)
    }

    @PatchMapping("/{idUsuario}/enderecos/{idEndereco}/principal")
    fun definirEnderecoPrincipal(@PathVariable idUsuario: String, @PathVariable idEndereco: String): ResponseEntity<RespostaUsuarioDTO> {
        val usuarioAtualizado = servico.definirEnderecoPrincipal(idUsuario, idEndereco)
        val respostaDTO = RespostaUsuarioDTO.deModelo(usuarioAtualizado)
        return ResponseEntity.ok(respostaDTO)
    }
}
