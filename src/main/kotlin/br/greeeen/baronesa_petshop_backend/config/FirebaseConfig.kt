package br.greeeen.baronesa_petshop_backend.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.cloud.FirestoreClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException
import java.io.InputStream

@Configuration
class FirebaseConfig {
    private val CAMINHOCHAVE = "firebase/baronesa-petshop-firebase-adminsdk.json"

    @Bean
    fun inicializarFirebaseApp(): FirebaseApp {
        val inputStream: InputStream = FirebaseConfig::class.java.classLoader.getResourceAsStream(CAMINHOCHAVE)
            ?: throw IOException("Arquivo da chave de conta de serviço do Firebase não encontrado em 'src/main/resources/$CAMINHOCHAVE'. " +
                    "Faça o download no Console do Firebase > Configurações do Projeto > Contas de Serviço e coloque-o na pasta resources.")

        val opcoes: FirebaseOptions = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(inputStream))
            // Se você configurou o Database URL no Firebase console, pode adicioná-lo aqui,
            // mas para Firestore e Firebase Auth geralmente não é necessário se as credenciais estiverem corretas.
            // .setDatabaseUrl("https://<SEU_ID_DE_PROJETO>.firebaseio.com")
            .build()

        // Evita reinicialização se já houver uma instância (útil em testes ou hot reload)
        return if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(opcoes)
        } else {
            FirebaseApp.getInstance()
        }
    }

    @Bean
    fun obterFirestore(firebaseApp: FirebaseApp): Firestore {
        return FirestoreClient.getFirestore(firebaseApp)
    }

    @Bean
    fun obterFirebaseAuth(firebaseApp: FirebaseApp): FirebaseAuth {
        return FirebaseAuth.getInstance(firebaseApp)
    }
}