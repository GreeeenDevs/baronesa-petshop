package br.greeeen.baronesa_petshop_backend.service

import br.greeeen.baronesa_petshop_backend.exception.NegocioException
import br.greeeen.baronesa_petshop_backend.exception.RecursoNaoEncontradoException
import br.greeeen.baronesa_petshop_backend.model.CarrinhoDeCompras
import br.greeeen.baronesa_petshop_backend.model.ItemDoCarrinho
import br.greeeen.baronesa_petshop_backend.model.Produto
import br.greeeen.baronesa_petshop_backend.repository.CarrinhoRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
class CarrinhoServiceImplementationTest {

    @MockK
    lateinit var carrinhoRepository: CarrinhoRepository

    @MockK
    lateinit var produtoService: ProdutoService

    @InjectMockKs
    lateinit var carrinhoService: CarrinhoServiceImplementation

    private val ID_USUARIO = "user-123"
    private val ID_PRODUTO_A = "prod-A"

    private lateinit var produtoA: Produto
    private lateinit var carrinhoVazio: CarrinhoDeCompras

    @BeforeEach
    fun setup() {
        produtoA = Produto(
            id = ID_PRODUTO_A,
            nome = "Ração Premium",
            preco = 50.0,
            estoque = 10
        )
        carrinhoVazio = CarrinhoDeCompras(id = ID_USUARIO, itens = emptyList())

        // Configuração padrão dos mocks
        every { produtoService.buscarProdutoPorId(ID_PRODUTO_A) } returns produtoA
        every { carrinhoRepository.salvar(any()) } answers { it.invocation.args[0] as CarrinhoDeCompras }
    }

    @Test
    fun `deve adicionar item a um carrinho vazio com sucesso`() {
        // Arrange
        val quantidadeDesejada = 3
        every { carrinhoRepository.buscarPorId(ID_USUARIO) } returns null

        // Act
        val carrinhoAtualizado = carrinhoService.adicionarItem(ID_USUARIO, ID_PRODUTO_A, quantidadeDesejada)

        // Assert
        verify(exactly = 1) { carrinhoRepository.salvar(any()) }
        assertEquals(1, carrinhoAtualizado.itens.size)
        assertEquals(quantidadeDesejada, carrinhoAtualizado.itens.first().quantidade)

        // CORREÇÃO: Usamos compareTo() == 0 para que 150.0 (retornado pelo código) seja igual a 150.00 (esperado)
        assertTrue(
            BigDecimal("150.00").compareTo(carrinhoAtualizado.calcularValorTotalItens()) == 0,
            "O valor total deve ser 150.00"
        )
    }

    @Test
    fun `deve atualizar a quantidade de um item existente no carrinho`() {
        // Arrange
        val itemExistente = ItemDoCarrinho(
            idProduto = ID_PRODUTO_A,
            nomeProduto = produtoA.nome,
            precoUnitario = produtoA.preco,
            quantidade = 2
        )
        val carrinhoExistente = CarrinhoDeCompras(id = ID_USUARIO, itens = listOf(itemExistente))
        val quantidadeAdicionar = 4 // Total esperado: 6

        every { carrinhoRepository.buscarPorId(ID_USUARIO) } returns carrinhoExistente

        // Act
        val carrinhoAtualizado = carrinhoService.adicionarItem(ID_USUARIO, ID_PRODUTO_A, quantidadeAdicionar)

        // Assert
        assertEquals(1, carrinhoAtualizado.itens.size)
        assertEquals(6, carrinhoAtualizado.itens.first().quantidade)

        // CORREÇÃO: Usamos compareTo() == 0
        assertTrue(
            BigDecimal("300.00").compareTo(carrinhoAtualizado.calcularValorTotalItens()) == 0,
            "O valor total deve ser 300.00"
        )
    }

    @Test
    fun `deve lancar NegocioException se quantidade solicitada exceder o estoque (RN01)`() {
        // Arrange
        val quantidadeDesejada = 11 // Excede o estoque de 10
        every { carrinhoRepository.buscarPorId(ID_USUARIO) } returns carrinhoVazio

        // Act & Assert
        val excecao = assertThrows(NegocioException::class.java) {
            carrinhoService.adicionarItem(ID_USUARIO, ID_PRODUTO_A, quantidadeDesejada)
        }

        assertTrue(excecao.message!!.contains("Estoque insuficiente"))
        verify(exactly = 0) { carrinhoRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar NegocioException se o produto não for encontrado`() {
        val idProdutoInexistente = "prod-nao-existe"
        every { produtoService.buscarProdutoPorId(idProdutoInexistente) } throws RecursoNaoEncontradoException()

        val excecao = assertThrows(NegocioException::class.java) {
            carrinhoService.adicionarItem(ID_USUARIO, idProdutoInexistente, 1)
        }

        assertTrue(excecao.message!!.contains("não existe ou não está disponível"))
        verify(exactly = 0) { carrinhoRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar NegocioException se a quantidade for zero ou negativa`() {
        val quantidadeNegativa = -5
        val excecaoNegativa = assertThrows(NegocioException::class.java) {
            carrinhoService.adicionarItem(ID_USUARIO, ID_PRODUTO_A, quantidadeNegativa)
        }
        assertTrue(excecaoNegativa.message!!.contains("maior que zero"))

        val quantidadeZero = 0
        val excecaoZero = assertThrows(NegocioException::class.java) {
            carrinhoService.adicionarItem(ID_USUARIO, ID_PRODUTO_A, quantidadeZero)
        }
        assertTrue(excecaoZero.message!!.contains("maior que zero"))
        verify(exactly = 0) { carrinhoRepository.salvar(any()) }
    }
}