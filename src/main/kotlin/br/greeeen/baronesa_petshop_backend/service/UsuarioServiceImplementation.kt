package br.greeeen.baronesa_petshop_backend.service

import br.greeeen.baronesa_petshop_backend.exception.NegocioException
import br.greeeen.baronesa_petshop_backend.exception.RecursoNaoEncontradoException
import br.greeeen.baronesa_petshop_backend.model.Endereco
import br.greeeen.baronesa_petshop_backend.model.Usuario
import br.greeeen.baronesa_petshop_backend.repository.UsuarioRepository
import br.greeeen.baronesa_petshop_backend.servico.UsuarioService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UsuarioServiceImplementation(
    private  val repositorio: UsuarioRepository
) : UsuarioService {

    override fun registrarNovoUsuario(usuario: Usuario): Usuario {

        if (repositorio.buscarPorEmail(usuario.email) != null) {
            throw NegocioException("O e-mail '${usuario.email}' já está cadastrado no sistema.")
        }

        val enderecosComId = usuario.enderecos.mapIndexed { index, endereco ->
            endereco.copy(
                id = endereco.id ?: UUID.randomUUID().toString(),
                principal = (index == 0)
            )
        }
        val novoUsuario = usuario.copy(enderecos = enderecosComId)

        return repositorio.salvar(novoUsuario)
    }

    override fun buscarUsuarioPorId(id: String): Usuario {
        return repositorio.buscarPorId(id) ?: throw RecursoNaoEncontradoException("Usuário com ID '$id' não encontrado.")
    }

    override fun atualizarDadosCadastrais(id: String, usuarioAtualizado: Usuario): Usuario {
        val usuarioExistente = buscarUsuarioPorId(id)

        if (usuarioAtualizado.email != usuarioExistente.email) {
            if (repositorio.buscarPorEmail(usuarioAtualizado.email) != null) {
                throw NegocioException("Não é possível alterar o e-mail. O e-mail '${usuarioAtualizado.email}' já está em uso por outro usuário.")
            }
        }

        val usuarioParaSalvar = usuarioExistente.copy(
            nome = usuarioAtualizado.nome,
            email = usuarioAtualizado.email,
            telefone = usuarioAtualizado.telefone,
            enderecos = usuarioExistente.enderecos,
            dataCriacao = usuarioExistente.dataCriacao,
            id = usuarioExistente.id
        )

        return repositorio.salvar(usuarioParaSalvar)
    }

    override fun adicionarEndereco(idUsuario: String, novoEndereco: Endereco): Usuario {

        val usuario = buscarUsuarioPorId(idUsuario)

        val idNovoEndereco = novoEndereco.id ?: UUID.randomUUID().toString()
        val enderecoComId = novoEndereco.copy(
            id = idNovoEndereco,
            principal = usuario.enderecos.isEmpty()
        )

        val novaListaDeEnderecos = usuario.enderecos + enderecoComId
        val usuarioAtualizado = usuario.copy(enderecos = novaListaDeEnderecos)

        return repositorio.salvar(usuarioAtualizado)
    }

    override fun removerEndereco(idUsuario: String, idEndereco: String): Usuario {
        val usuario = buscarUsuarioPorId(idUsuario)

        val enderecoParaRemover = usuario.enderecos.find { it.id == idEndereco }
            ?: throw RecursoNaoEncontradoException("Endereço com ID '$idEndereco' não encontrado para o usuário.")

        val novaListaDeEnderecos = usuario.enderecos.filter { it.id != idEndereco }

        var listaFinalDeEnderecos = novaListaDeEnderecos
        if (enderecoParaRemover.principal && listaFinalDeEnderecos.isNotEmpty()) {
            listaFinalDeEnderecos = listaFinalDeEnderecos.toMutableList().apply {
                this[0] = this[0].copy(principal = true)
            }
        }

        val usuarioAtualizado = usuario.copy(enderecos = listaFinalDeEnderecos)
        return repositorio.salvar(usuarioAtualizado)
    }

    override fun definirEnderecoPrincipal(idUsuario: String, idEndereco: String): Usuario {
        val usuario = buscarUsuarioPorId(idUsuario)
        var encontrado = false
        val novaListaDeEnderecos = usuario.enderecos.map { endereco ->
            when (endereco.id) {
                idEndereco -> {
                    encontrado = true
                    endereco.copy(principal = true)
                }
                else -> {
                    endereco.copy(principal = false)
                }
            }
        }

        if (!encontrado) {
            throw RecursoNaoEncontradoException("Endereço com ID '$idEndereco' não encontrado para o usuário.")
        }

        val usuarioAtualizado = usuario.copy(enderecos = novaListaDeEnderecos)
        return repositorio.salvar(usuarioAtualizado)
    }
}