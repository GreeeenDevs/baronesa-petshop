package br.greeeen.baronesa_petshop_backend.controller

import br.greeeen.baronesa_petshop_backend.dto.*
import br.greeeen.baronesa_petshop_backend.model.Usuario
import br.greeeen.baronesa_petshop_backend.servico.UsuarioService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/clientes")
class UsuarioController(
    private  val servico: UsuarioService
) {
    @PostMapping("/registro")
    fun registrarNovoUsuario(@Valid @RequestBody registroDTO: RegistroUsuarioDTO): ResponseEntity<RespostaUsuarioDTO> {
        val usuario = registroDTO.paraModelo()
        val novoUsuario = servico.registrarNovoUsuario(usuario)

        val respostaDTO = RespostaUsuarioDTO.deModelo(novoUsuario)

        return ResponseEntity
            .created(URI.create("/api/v1/clientes/${novoUsuario.id}"))
            .body(respostaDTO)
    }

    @GetMapping
    fun listarTodosUsuarios(): ResponseEntity<List<RespostaUsuarioDTO>> {
        val usuarios = servico.listarTodosUsuarios()
        val respostaDTO = usuarios.map { RespostaUsuarioDTO.deModelo(it) }
        return ResponseEntity.ok(respostaDTO)
    }

    @GetMapping("/{id}")
    fun buscarUsuarioPorId(@PathVariable id: String): ResponseEntity<RespostaUsuarioDTO> {
        val usuario = servico.buscarUsuarioPorId(id)
        val respostaDTO = RespostaUsuarioDTO.deModelo(usuario)
        return ResponseEntity.ok(respostaDTO)
    }

    @PutMapping("/{id}")
    fun atualizarDadosCadastrais(@PathVariable id: String, @Valid @RequestBody atualizacaoDTO: AtualizacaoUsuarioDTO): ResponseEntity<RespostaUsuarioDTO> {
        val usuarioParaAtualizar = Usuario(
            nome = atualizacaoDTO.nome,
            email = atualizacaoDTO.email,
            telefone = atualizacaoDTO.telefone
        )

        val usuarioAtualizado = servico.atualizarDadosCadastrais(id, usuarioParaAtualizar)
        val respostaDTO = RespostaUsuarioDTO.deModelo(usuarioAtualizado)
        return ResponseEntity.ok(respostaDTO)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletarUsuario(@PathVariable id: String) {
        servico.deletarUsuario(id)
    }
}