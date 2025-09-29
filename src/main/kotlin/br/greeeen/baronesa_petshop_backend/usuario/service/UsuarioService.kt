package br.greeeen.baronesa_petshop_backend.usuario.service

import br.greeeen.baronesa_petshop_backend.usuario.dto.RequisicaoCadastroCliente
import br.greeeen.baronesa_petshop_backend.usuario.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserRecord
import com.google.cloud.firestore.Firestore
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder // Importaremos para o futuro, mas não usaremos diretamente aqui para o Firebase Auth
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    @Autowired private val firebaseAuth: FirebaseAuth,
    @Autowired private val firestore: Firestore
    // @Autowired private val passwordEncoder: PasswordEncoder // Para quando formos gerar tokens JWT e validar senhas localmente se necessário
) {

    private val logger = LoggerFactory.getLogger(UsuarioService::class.java)
    private val colecaoUsuarios = "usuarios" // Nome da coleção no Firestore

    /**
     * Registra um novo cliente.
     * 1. Cria o usuário no Firebase Authentication.
     * 2. Salva os dados adicionais do usuário no Firestore usando o UID do Firebase Auth como ID do documento.
     */
    fun registrarNovoCliente(requisicao: RequisicaoCadastroCliente): Usuario {
        try {
            // 1. Criar usuário no Firebase Authentication
            val createRequest = UserRecord.CreateRequest()
                .setEmail(requisicao.email)
                .setPassword(requisicao.senha)
                .setDisplayName(requisicao.nome)
                .setEmailVerified(false)

            val registroUsuarioFirebase: UserRecord = firebaseAuth.createUser(createRequest)
            logger.info("Usuário criado no Firebase Authentication com UID: ${registroUsuarioFirebase.uid}")

            // 2. Preparar e salvar dados no Firestore
            val novoUsuario = Usuario(
                id = registroUsuarioFirebase.uid,
                nome = requisicao.nome,
                email = requisicao.email
                // dataCriacao e dataAtualizacao serão preenchidos pelo @ServerTimestamp
            )

            firestore.collection(colecaoUsuarios)
                .document(registroUsuarioFirebase.uid)
                .set(novoUsuario)
                .get() // .get() aqui é para esperar a operação completar

            logger.info("Dados do usuário ${novoUsuario.email} salvos no Firestore.")
            return novoUsuario

        } catch (e: FirebaseAuthException) {
            logger.error("Erro ao criar usuário no Firebase Authentication: ${e.message}", e)
            if (e.errorCode.name == "ALREADY_EXISTS" || e.message?.contains("EMAIL_EXISTS") == true || e.message?.contains("email already in use") == true) {
                throw EmailJaCadastradoException("O email '${requisicao.email}' já está cadastrado.")
            }
            throw RuntimeException("Falha ao registrar usuário no Firebase: ${e.message}", e)
        } catch (e: Exception) {
            logger.error("Erro inesperadoao registrar novo cliente: ${e.message}", e)
            throw RuntimeException("Erro inesperado ao registrar cliente: ${e.message}", e)
        }
    }

    /**
     * Busca um usuário no Firestore pelo seu ID (UID do Firebase Auth).
     */
    fun buscarUsuarioPorId(uid: String): Usuario? {
        return try {
            val documentSnapshot = firestore.collection(colecaoUsuarios).document(uid).get().get()
            if (documentSnapshot.exists()) {
                documentSnapshot.toObject(Usuario::class.java)
            } else {
                logger.warn("Usuário com UID $uid não encontrado no Firestore.")
                null
            }
        } catch (e: Exception) {
            logger.error("Erro ao buscar usuário por UID $uid no Firestore: ${e.message}", e)
            null // Ou lançar uma exceção específica
        }
    }

    /**
     * Busca um usuário no Firestore pelo seu email.
     * Útil para verificar se um email já existe antes de tentar criar no Firebase Auth,
     * ou para obter dados do usuário após um login bem-sucedido.
     */
    fun buscarUsuarioPorEmail(email: String): Usuario? {
        return try {
            val querySnapshot = firestore.collection(colecaoUsuarios)
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .get()

            if (!querySnapshot.isEmpty) {
                querySnapshot.documents[0].toObject(Usuario::class.java)
            } else {
                logger.info("Usuário com email $email não encontrado no Firestore.")
                null
            }
        } catch (e: Exception) {
            logger.error("Erro ao buscar usuário por email $email no Firestore: ${e.message}", e)
            null
        }
    }

    // Futuramente:
    // fun atualizarDadosUsuario(uid: String, dadosAtualizados: /* DTO de atualização */): Usuario
    // fun adicionarEndereco(uid: String, novoEndereco: Endereco): Usuario
    // fun removerEndereco(uid: String, idEndereco: String): Usuario
}

// Exceção customizada
class EmailJaCadastradoException(message: String) : RuntimeException(message)