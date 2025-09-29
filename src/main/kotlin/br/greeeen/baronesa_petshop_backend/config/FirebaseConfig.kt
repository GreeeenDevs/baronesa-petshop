package br.greeeen.baronesa_petshop_backend.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.google.cloud.firestore.Firestore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.IOException
import jakarta.annotation.PostConstruct

@Configuration
class ConfiguracaoFirebase {

    companion object {
        private const val CAMINHOCREDENCIAIS = "firebase/baronesa-petshop-firebase-adminsdk.json"
    }

    @PostConstruct
    @Throws(IOException::class)
    fun inicializarFirebase() {
        val credenciaisStream = try {
            ClassPathResource(CAMINHOCREDENCIAIS).inputStream
        } catch (e: IOException) {
            println("Aviso: Arquivo de credenciais do Firebase não encontrado no caminho padrão. Tentando credenciais de ambiente.")
            null
        }

        val construtorOpcoes = FirebaseOptions.builder()
            .setCredentials(
                credenciaisStream?.let { GoogleCredentials.fromStream(it) } ?: GoogleCredentials.getApplicationDefault()
            )

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(construtorOpcoes.build())
            println("Firebase Admin SDK inicializado com sucesso.")
        }
    }

    @Bean
    fun clienteFirestore(): Firestore {
        return FirestoreClient.getFirestore()
    }
}