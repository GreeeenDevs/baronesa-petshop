package br.greeeen.baronesa_petshop_backend.controller

import br.greeeen.baronesa_petshop_backend.dto.AdicionarItemRequestDTO
import br.greeeen.baronesa_petshop_backend.dto.AtualizarItemRequestDTO
import br.greeeen.baronesa_petshop_backend.dto.CarrinhoRespostaDTO
import br.greeeen.baronesa_petshop_backend.security.UserPrinciple
import br.greeeen.baronesa_petshop_backend.service.CarrinhoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/carrinho")
class CarrinhoController(
    private val carrinhoService: CarrinhoService
) {
    private fun obterIdUsuario(principio: UserPrinciple): String {
        return principio.name
    }

    @GetMapping
    fun buscarCarrinho(@AuthenticationPrincipal principio: UserPrinciple): ResponseEntity<CarrinhoRespostaDTO> {
        val idUsuario = obterIdUsuario(principio)
        val carrinho = carrinhoService.buscarOuCriarCarrinho(idUsuario)
        return ResponseEntity.ok(CarrinhoRespostaDTO.deModelo(carrinho))
    }

    @PostMapping("/itens")
    fun adicionarItem(
        @AuthenticationPrincipal principio: UserPrinciple,
        @Valid @RequestBody itemDTO: AdicionarItemRequestDTO
    ): ResponseEntity<CarrinhoRespostaDTO> {
        val idUsuario = obterIdUsuario(principio)
        val carrinhoAtualizado = carrinhoService.adicionarItem(idUsuario, itemDTO.idProduto, itemDTO.quantidade)
        return ResponseEntity.ok(CarrinhoRespostaDTO.deModelo(carrinhoAtualizado))
    }

    @PutMapping("/itens/{idProduto}")
    fun atualizarQuantidadeItem(
        @AuthenticationPrincipal principio: UserPrinciple,
        @PathVariable idProduto: String,
        @Valid @RequestBody itemDTO: AtualizarItemRequestDTO
    ): ResponseEntity<CarrinhoRespostaDTO> {
        val idUsuario = obterIdUsuario(principio)
        val carrinhoAtualizado = carrinhoService.atualizarQuantidade(idUsuario, idProduto, itemDTO.quantidade)
        return ResponseEntity.ok(CarrinhoRespostaDTO.deModelo(carrinhoAtualizado))
    }

    @DeleteMapping("/itens/{idProduto}")
    fun removerItem(
        @AuthenticationPrincipal principio: UserPrinciple,
        @PathVariable idProduto: String
    ): ResponseEntity<CarrinhoRespostaDTO> {
        val idUsuario = obterIdUsuario(principio)
        val carrinhoAtualizado = carrinhoService.removerItem(idUsuario, idProduto)
        return ResponseEntity.ok(CarrinhoRespostaDTO.deModelo(carrinhoAtualizado))
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun limparCarrinho(@AuthenticationPrincipal principio: UserPrinciple) {
        val idUsuario = obterIdUsuario(principio)
        carrinhoService.limparCarrinho(idUsuario)
    }
}