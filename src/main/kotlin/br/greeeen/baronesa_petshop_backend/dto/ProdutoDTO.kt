package br.greeeen.baronesa_petshop_backend.dto

import br.greeeen.baronesa_petshop_backend.model.Produto
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.util.Date

data class ProdutoCadastroDTO(
    @field:NotBlank(message = "O nome do produto é obrigatório.")
    val nome: String,

    @field:NotBlank(message = "A descrição é obrigatória.")
    val descricao: String,

    @field:NotNull(message = "O preço é obrigatório.")
    @field:DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    val preco: BigDecimal,

    @field:Min(value = 0, message = "O estoque não pode ser negativo.")
    val estoque: Int,

    @field:NotEmpty(message = "O produto deve ter pelo menos uma categoria.")
    val categorias: List<String>,

    val fotos: List<String> = emptyList(), // URLs/Caminhos das imagens (RF008)
    val especificacoes: Map<String, String> = emptyMap(),

    @field:NotBlank(message = "A marca é obrigatória.")
    val marca: String
) {
    fun paraModelo(id: String? = null): Produto {
        return Produto(
            id = id,
            nome = this.nome,
            descricao = this.descricao,
            preco = this.preco,
            estoque = this.estoque,
            categorias = this.categorias,
            fotos = this.fotos,
            especificacoes = this.especificacoes,
            marca = this.marca
        )
    }
}

data class ProdutoRespostaDTO(
    val id: String,
    val nome: String,
    val descricao: String,
    val preco: BigDecimal,
    val estoque: Int,
    val categorias: List<String>,
    val fotos: List<String>,
    val especificacoes: Map<String, String>,
    val marca: String,
    val dataCriacao: Date?
) {
    companion object {
        fun deModelo(produto: Produto): ProdutoRespostaDTO {
            return ProdutoRespostaDTO(
                id = produto.id ?: throw IllegalStateException("ID do produto não pode ser nulo."),
                nome = produto.nome,
                descricao = produto.descricao,
                preco = produto.preco,
                estoque = produto.estoque,
                categorias = produto.categorias,
                fotos = produto.fotos,
                especificacoes = produto.especificacoes,
                marca = produto.marca,
                dataCriacao = produto.dataCriacao
            )
        }
    }
}

data class AtualizacaoEstoqueDTO(
    @field:Min(value = 0, message = "A nova quantidade em estoque não pode ser negativa.")
    val novaQuantidade: Int
)

data class FiltroProdutoDTO(
    val termo: String? = null,
    val categorias: List<String> = emptyList()
)