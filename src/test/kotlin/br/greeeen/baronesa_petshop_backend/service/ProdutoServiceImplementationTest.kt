package br.greeeen.baronesa_petshop_backend.service

import br.greeeen.baronesa_petshop_backend.model.Produto
import br.greeeen.baronesa_petshop_backend.repository.ProdutoRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ProdutoServiceImplementationTest {

    // MockK: Mock para simular o acesso a dados, isolando o Service
    @MockK
    lateinit var produtoRepository: ProdutoRepository

    // Injeção da classe a ser testada, utilizando o Mock acima
    @InjectMockKs
    lateinit var produtoService: ProdutoServiceImpl

    // Dados de teste para simular o banco de dados
    private val produtoDisponivel = Produto(id = "1", nome = "Ração", preco = 50.0, estoque = 5, categorias = listOf("Alimento"))
    private val produtoIndisponivel = Produto(id = "2", nome = "Brinquedo", preco = 10.0, estoque = 0, categorias = listOf("Brinquedo"))
    private val produtoOutro = Produto(id = "3", nome = "Coleira", preco = 25.0, estoque = 1, categorias = listOf("Acessório"))

    // Lista de todos os produtos no "banco" (Mock do repositório)
    private val todosProdutos = listOf(produtoDisponivel, produtoIndisponivel, produtoOutro)

    // TESTES DE LISTAGEM E FILTRAGEM (RN03)

    @Test
    fun `deve listar apenas produtos com estoque maior que zero (RN03)`() {
        // Arrange
        // Stub: Simula o repositório retornando todos os produtos
        every { produtoRepository.buscarTodos() } returns todosProdutos

        // Act
        val produtosListados = produtoService.listarProdutos()

        // Assert
        assertEquals(2, produtosListados.size) // Espera Produto 1 e Produto 3
        assertEquals(produtoDisponivel.id, produtosListados[0].id)
        assertEquals(produtoOutro.id, produtosListados[1].id)
    }

    @Test
    fun `deve filtrar por termo e ignorar produtos sem estoque (RN03 e Fluxo Alternativo)`() {
        // Arrange
        val termoBusca = "Brinquedo" // Este produto está indisponível (estoque 0)

        // Stub: Simula o repositório buscando por termo (a busca no Firestore é implementada no Repo)
        every { produtoRepository.buscarPorTermo(termoBusca) } returns listOf(produtoIndisponivel)
        every { produtoRepository.buscarTodos() } returns todosProdutos // Fallback, embora o termo deva ser prioritário

        // Act
        val produtosFiltrados = produtoService.buscarEFiltrarProdutos(termoBusca, emptyList())

        // Assert
        assertEquals(0, produtosFiltrados.size) // Embora o termo case, o estoque é zero e deve ser ignorado.
    }
}