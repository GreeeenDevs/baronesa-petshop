# Baronesa Petshop E-commerce Backend

Este documento serve como a documentação técnica e de referência para o backend da plataforma de e-commerce Baronesa Petshop.
O projeto segue uma arquitetura em camadas (Model, Repository, Service, Controller), utilizando **Kotlin** com **Spring Boot** e **Google Firestore (Firebase Admin SDK)** para persistência.

## 1. Arquitetura e Estrutura do Projeto

A estrutura de pacotes segue o padrão Spring, com nomenclatura em **Português**, garantindo clareza e manutenção.

| Pacote | Responsabilidade |
| :--- | :--- |
| `model` | Classes de domínio e entidades do Firestore. |
| `repository` | Contratos e implementações de acesso a dados (Firestore). |
| `service` | Lógica de Negócio e validações. |
| `controller` | Ponto de entrada da API REST (endpoints HTTP). |
| `dto` | Objetos de Transferência de Dados (Input/Output da API). |
| `exception` | Exceções customizadas (`404`, `400`). |
| `config` | Configurações de terceiros (Firebase Admin SDK, WebClient). |
| `security` | Configuração de Autenticação/Autorização (Spring Security + Firebase). |
| `api` | Clientes HTTP e DTOs para APIs externas (Frete, Pagamento). |

### Tecnologias Principais

* **Linguagem:** Kotlin
* **Framework:** Spring Boot 3
* **Banco de Dados:** Google Firestore (via Firebase Admin SDK)
* **Comunicação Externa:** Spring WebFlux WebClient (Não-bloqueante)
* **Segurança:** Spring Security com Firebase ID Token.

## 2. Módulos Implementados e Endpoints

Os módulos centrais da plataforma estão prontos, com operações de CRUD (Create, Read, Update, Delete) completas.

### 2.1. Módulo de Usuários (Clientes)

**Gestão completa de contas de clientes e seus endereços.**

| Método HTTP | Endpoint | Descrição | Restrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/clientes/registro` | **Registro de Novo Cliente.** | Público |
| `GET` | `/api/v1/clientes` | Lista todos os clientes. | `ROLE_ADMIN` |
| `GET` | `/api/v1/clientes/{id}` | Busca os dados de um cliente específico. | Autenticado |
| `PUT` | `/api/v1/clientes/{id}` | Atualiza nome, e-mail e telefone do cliente. | Autenticado |
| `DELETE` | `/api/v1/clientes/{id}` | Deleta um cliente. | `ROLE_ADMIN` |
| `POST` | `/api/v1/clientes/{id}/enderecos` | Adiciona um novo endereço. | Autenticado |
| `DELETE` | `/api/v1/clientes/{id}/enderecos/{idEndereco}` | Remove um endereço. | Autenticado |
| `PATCH` | `/api/v1/clientes/{id}/enderecos/{idEndereco}/principal` | Define um endereço como principal. | Autenticado |

### 2.2. Módulo de Catálogo e Produtos

**Gestão completa de produtos, estoque e catálogo.**

| Método HTTP | Endpoint | Descrição | Restrição |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/produtos` | Lista/Filtra produtos (termo de busca, categorias). | Público |
| `GET` | `/api/v1/produtos/{id}` | Busca os detalhes de um produto. | Público |
| `POST` | `/api/v1/produtos` | Adiciona um novo produto ao catálogo. | `ROLE_ADMIN` |
| `PUT` | `/api/v1/produtos/{id}` | Atualiza um produto existente. | `ROLE_ADMIN` |
| `DELETE` | `/api/v1/produtos/{id}` | Remove um produto do catálogo. | `ROLE_ADMIN` |
| `PATCH`| `/api/v1/produtos/{id}/estoque`| Atualiza a quantidade em estoque. | `ROLE_ADMIN` |

### 2.3. Módulo de Carrinho de Compras

**Gerenciamento do carrinho de compras do usuário logado.**

| Método HTTP | Endpoint | Descrição | Restrição |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/carrinho` | Busca o carrinho do usuário logado. | Autenticado |
| `POST`| `/api/v1/carrinho/itens`| Adiciona um item ao carrinho. | Autenticado |
| `PUT` | `/api/v1/carrinho/itens/{idProduto}` | Altera a quantidade de um item. | Autenticado |
| `DELETE`| `/api/v1/carrinho/itens/{idProduto}` | Remove um item do carrinho. | Autenticado |
| `DELETE`| `/api/v1/carrinho` | Esvazia o carrinho do usuário. | Autenticado |

### 2.4. Módulo de Pedidos e Checkout

**Fluxo de finalização de compra e histórico de pedidos.**

| Método HTTP | Endpoint | Descrição | Restrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/pedidos/checkout/previa` | **Calcula Frete e Cupom** para visualização. | Autenticado |
| `POST` | `/api/v1/pedidos/checkout/finalizar` | Cria o pedido, processa o pagamento e atualiza o estoque. | Autenticado |
| `GET` | `/api/v1/pedidos` | Lista o histórico de pedidos do usuário logado. | Autenticado |
| `GET` | `/api/v1/pedidos/admin/todos` | Lista todos os pedidos do sistema. | `ROLE_ADMIN` |
| `GET` | `/api/v1/pedidos/{id}` | Busca os detalhes de um pedido. | Autenticado |
| `PATCH`| `/api/v1/pedidos/{id}/status` | Atualiza o status do pedido (Ex: ENVIADO). | `ROLE_ADMIN` |
| `DELETE`| `/api/v1/pedidos/{id}` | Deleta um pedido do sistema. | `ROLE_ADMIN` |

## 3. Próximos Passos (Requisitos Faltantes)

Os seguintes requisitos precisam ser desenvolvidos para a conclusão total do sistema (conforme Auditoria):

1.  **Módulo de Gestão de Cupons (RF022):** Implementação de Model, Repo, Service e Controller dedicados para criar e gerenciar cupons dinâmicos.
2.  **Painel Administrativo - Dashboard (RF023):** Criação de relatórios de vendas e acesso.
3.  **Assinaturas (RF018):** O módulo de compras recorrentes ainda precisa ser implementado.
4.  **Notificações (RF017):** Implementação do serviço de envio de e-mails transacionais (pedido confirmado, enviado, etc.).

O desenvolvimento continuará com foco na construção do **Módulo de Gestão de Cupons** e do **Painel Administrativo**.