package br.greeeen.baronesa_petshop_backend.servico

import br.greeeen.baronesa_petshop_backend.model.Endereco
import br.greeeen.baronesa_petshop_backend.model.Usuario

interface UsuarioService {

    fun registrarNovoUsuario(usuario: Usuario): Usuario

    fun buscarUsuarioPorId(id: String): Usuario

    fun atualizarDadosCadastrais(id: String, usuarioAtualizado: Usuario): Usuario

    fun adicionarEndereco(idUsuario: String, novoEndereco: Endereco): Usuario

    fun removerEndereco(idUsuario: String, idEndereco: String): Usuario

    fun definirEnderecoPrincipal(idUsuario: String, idEndereco: String): Usuario

    fun listarTodosUsuarios(): List<Usuario>

    fun deletarUsuario(id: String)
}