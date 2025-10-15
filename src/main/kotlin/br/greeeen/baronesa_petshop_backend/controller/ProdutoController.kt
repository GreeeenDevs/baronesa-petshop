package br.greeeen.baronesa_petshop_backend.controller

import br.greeeen.baronesa_petshop_backend.dto.FiltroProdutoDTO
import br.greeeen.baronesa_petshop_backend.dto.AtualizacaoEstoqueDTO
import br.greeeen.baronesa_petshop_backend.dto.ProdutoCadastroDTO
import br.greeeen.baronesa_petshop_backend.dto.ProdutoRespostaDTO
import br.greeeen.baronesa_petshop_backend.service.ProdutoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/produtos")
class ProdutoController(
    private val service: ProdutoService
) {

    @GetMapping
    fun listarTodosProdutos(): ResponseEntity<List<ProdutoRespostaDTO>> {
        val produtos = service.listarProdutos()
        val respostaDTO = produtos.map { ProdutoRespostaDTO.deModelo(it) }
        return ResponseEntity.ok(respostaDTO)
    }

    @PostMapping("/filtrar")
    fun buscarProdutosPorFiltro(@RequestBody filtroDTO: FiltroProdutoDTO): ResponseEntity<List<ProdutoRespostaDTO>> {
        val produtos = service.buscarEFiltrarProdutos(filtroDTO.termo, filtroDTO.categorias)
        val respostaDTO = produtos.map { ProdutoRespostaDTO.deModelo(it) }
        return ResponseEntity.ok(respostaDTO)
    }

    @GetMapping("/{id}")
    fun buscarProdutoPorId(@PathVariable id: String): ResponseEntity<ProdutoRespostaDTO> {
        val produto = service.buscarProdutoPorId(id)
        val respostaDTO = ProdutoRespostaDTO.deModelo(produto)

        if (produto.estoque <= 0) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(respostaDTO)
    }

    @PostMapping
    fun criarProduto(@Valid @RequestBody cadastroDTO: ProdutoCadastroDTO): ResponseEntity<ProdutoRespostaDTO> {
        val produtoParaSalvar = cadastroDTO.paraModelo()
        val novoProduto = service.salvarProduto(produtoParaSalvar)
        val respostaDTO = ProdutoRespostaDTO.deModelo(novoProduto)

        return ResponseEntity
            .created(URI.create("/api/v1/produtos/${novoProduto.id}"))
            .body(respostaDTO)
    }

    @PutMapping("/{id}")
    fun atualizarProduto(@PathVariable id: String, @Valid @RequestBody cadastroDTO: ProdutoCadastroDTO): ResponseEntity<ProdutoRespostaDTO> {
        val produtoParaAtualizar = cadastroDTO.paraModelo(id)
        val produtoAtualizado = service.salvarProduto(produtoParaAtualizar)
        val respostaDTO = ProdutoRespostaDTO.deModelo(produtoAtualizado)

        return ResponseEntity.ok(respostaDTO)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletarProduto(@PathVariable id: String) {
        service.deletarProduto(id)
    }

    @PatchMapping("/{id}/estoque")
    fun atualizarEstoque(@PathVariable id: String, @Valid @RequestBody estoqueDTO: AtualizacaoEstoqueDTO): ResponseEntity<ProdutoRespostaDTO> {
        val produtoAtualizado = service.atualizarEstoque(id, estoqueDTO.novaQuantidade)
        val respostaDTO = ProdutoRespostaDTO.deModelo(produtoAtualizado)

        return ResponseEntity.ok(respostaDTO)
    }
}