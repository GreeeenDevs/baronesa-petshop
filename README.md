# Baronesa Petshop E-commerce Backend

Este documento serve como a documentação técnica e de referência para o backend da plataforma de e-commerce Baronesa Petshop, desenvolvido em **Kotlin** com **Spring Boot** e utilizando **Google Firestore (Firebase Admin SDK)** como banco de dados NoSQL.

## 1. Arquitetura e Estrutura do Projeto

O projeto segue uma arquitetura em camadas (Model, Repository, Service, Controller), organizada em pacotes com nomenclatura em **Português**, conforme convenções estabelecidas.

### Estrutura de Pacotes

| Pacote | Responsabilidade | Convenção de Nomenclatura |
| :--- | :--- | :--- |
| `model` | Classes de domínio e entidades do Firestore. | `Usuario.kt`, `Produto.kt` |
| `repository` | Contratos e implementações de acesso a dados (Firestore). | `UsuarioRepository`, `ProdutoRepositoryFirestore` |
| `service` | Lógica de Negócio e validações (RFs). | `UsuarioService`, `ProdutoServiceImpl` |
| `controller` | Ponto de entrada da API REST (endpoints HTTP). | `UsuarioController`, `ProdutoController` |
| `dto` | Objetos de Transferência de Dados (Input/Output da API). | `RegistroUsuarioDTO`, `ProdutoRespostaDTO` |
| `exception` | Exceções customizadas (`404`, `400`). | `ExcecaoRecursoNaoEncontrado` |
| `config` | Configurações de terceiros (Ex: Firebase Admin SDK). | `ConfiguracaoFirebase` |

### Tecnologias Principais

* **Linguagem:** Kotlin
* **Framework:** Spring Boot 3
* **Banco de Dados:** Google Firestore (via Firebase Admin SDK)
* **Segurança:** Spring Security (será implementado na próxima fase)

## 2. Configuração Inicial

Para rodar o projeto, o Firebase Admin SDK precisa ser inicializado. A configuração espera o arquivo de credenciais em:

`src/main/resources/firebase/baronesa-petshop-firebase-adminsdk.json`

## 3. Módulos Desenvolvidos

### 3.1. Módulo de Gestão de Usuários (Clientes)

**Responsabilidade:** Cadastro, visualização, edição de dados e gerenciamento de endereços (RF001, RF003, RF004).

| Método HTTP | Endpoint | Descrição | RFs |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/clientes/registro` | **Registro de Novo Cliente** (Público). | RF001 |
| `GET` | `/api/v1/clientes/{id}` | Busca os dados cadastrais do cliente. (Privado) | RF003 |
| `PUT` | `/api/v1/clientes/{id}` | Atualiza nome, e-mail e telefone do cliente. (Privado) | RF003 |
| `POST` | `/api/v1/clientes/{id}/enderecos` | Adiciona um novo endereço à conta do cliente. (Privado) | RF004 |
| `DELETE` | `/api/v1/clientes/{id}/enderecos/{idEndereco}` | Remove um endereço específico. (Privado) | RF004 |
| `PATCH` | `/api/v1/clientes/{id}/enderecos/{idEndereco}/principal` | Define um endereço como principal de entrega. (Privado) | RF004 |

### 3.2. Módulo de Catálogo e Produtos

**Responsabilidade:** Exibição do catálogo público e gerenciamento de produtos e estoque pelo Admin (RF005, RF006, RF007, RF008, RF019, RF020).

| Método HTTP | Endpoint | Descrição | Restrição | RFs |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/produtos` | Lista/Filtra produtos (termo de busca, categorias). | Público | RF006, RF007 |
| `GET` | `/api/v1/produtos/{id}` | Busca os detalhes de um produto específico. | Público | RF008 |
| `POST` | `/api/v1/produtos` | Adiciona um novo produto ao catálogo. | **ADMIN** | RF019 |
| `PUT` | `/api/v1/produtos/{id}` | Atualiza todos os dados de um produto existente. | **ADMIN** | RF019 |
| `DELETE` | `/api/v1/produtos/{id}` | Remove um produto do catálogo. | **ADMIN** | RF019 |
| `PATCH` | `/api/v1/produtos/{id}/estoque` | Atualiza a quantidade em estoque. | **ADMIN** | RF020 |

---

## 4. Próximos Passos (Próxima Fase)

A próxima fase de desenvolvimento será dedicada ao módulo de Segurança, abordando:

1.  **Spring Security:** Configuração da segurança base.
2.  **Firebase Authentication:** Implementação do login de clientes (RF002).
3.  **Autorização:** Proteção dos endpoints administrativos (ex: `POST /api/v1/produtos`) com base na função do usuário.