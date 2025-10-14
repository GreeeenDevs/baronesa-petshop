package br.greeeen.baronesa_petshop_backend.controller

import br.greeeen.baronesa_petshop_backend.dto.AtualizacaoStatusDTO
import br.greeeen.baronesa_petshop_backend.dto.FinalizarCheckoutDTO
import br.greeeen.baronesa_petshop_backend.dto.PedidoRespostaDTO
import br.greeeen.baronesa_petshop_backend.dto.PreviaCheckoutDTO
import br.greeeen.baronesa_petshop_backend.enum.StatusPedido
import br.greeeen.baronesa_petshop_backend.security.UserPrinciple
import br.greeeen.baronesa_petshop_backend.service.PedidoService
import br.greeeen.baronesa_petshop_backend.servico.UsuarioService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/pedidos")
class PedidoController(
    private val pedidoService: PedidoService,
    private val servicoDeUsuario: UsuarioService
) {

    private fun obterIdUsuario(principio: UserPrinciple): String {
        return principio.name
    }

    @PostMapping("/checkout/previa")
    fun calcularPreviaCheckout(
        @AuthenticationPrincipal principio: UserPrinciple,
        @Valid @RequestBody previaDTO: PreviaCheckoutDTO
    ): ResponseEntity<PedidoRespostaDTO> {
        val idUsuario = obterIdUsuario(principio)

        val previaPedido = pedidoService.calcularPreviaDoPedido(
            idUsuario,
            previaDTO.cepDestino,
            previaDTO.cupom
        )

        return ResponseEntity.ok(PedidoRespostaDTO.deModelo(previaPedido))
    }

    @PostMapping("/checkout/finalizar")
    fun finalizarCheckout(
        @AuthenticationPrincipal principio: UserPrinciple,
        @Valid @RequestBody finalizarDTO: FinalizarCheckoutDTO
    ): ResponseEntity<PedidoRespostaDTO> {
        val idUsuario = obterIdUsuario(principio)

        val usuario = servicoDeUsuario.buscarUsuarioPorId(idUsuario)
        val enderecoEntrega = usuario.enderecos.find { it.id == finalizarDTO.idEndereco }
            ?: throw IllegalStateException("Endereço de entrega selecionado não encontrado para o usuário.")

        val pedidoCriado = pedidoService.finalizarCheckout(
            idUsuario = idUsuario,
            enderecoEntrega = enderecoEntrega,
            metodoPagamento = finalizarDTO.metodoPagamento,
            dadosPagamento = finalizarDTO.dadosPagamento,
            cupom = finalizarDTO.cupom
        )

        return ResponseEntity
            .created(URI.create("/api/v1/pedidos/${pedidoCriado.id}"))
            .body(PedidoRespostaDTO.deModelo(pedidoCriado))
    }


    @GetMapping
    fun listarMeusPedidos(@AuthenticationPrincipal principio: UserPrinciple): ResponseEntity<List<PedidoRespostaDTO>> {
        val idUsuario = obterIdUsuario(principio)
        val pedidos = pedidoService.listarPedidosPorUsuario(idUsuario)
        val respostaDTO = pedidos.map { PedidoRespostaDTO.deModelo(it) }

        return ResponseEntity.ok(respostaDTO)
    }

    @GetMapping("/admin/todos")
    fun listarTodosPedidos(): ResponseEntity<List<PedidoRespostaDTO>> {
        val pedidos = pedidoService.listarTodosPedidos()
        val respostaDTO = pedidos.map { PedidoRespostaDTO.deModelo(it) }
        return ResponseEntity.ok(respostaDTO)
    }

    @GetMapping("/{id}")
    fun buscarDetalheDoPedido(@PathVariable id: String): ResponseEntity<PedidoRespostaDTO> {
        val pedido = pedidoService.buscarPedidoPorId(id)
        val respostaDTO = PedidoRespostaDTO.deModelo(pedido)

        return ResponseEntity.ok(respostaDTO)
    }

    @PatchMapping("/{id}/status")
    fun atualizarStatusDoPedido(
        @PathVariable id: String,
        @Valid @RequestBody statusDTO: AtualizacaoStatusDTO
    ): ResponseEntity<PedidoRespostaDTO> {
        val novoStatus = try {
            StatusPedido.valueOf(statusDTO.novoStatus.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Status de pedido inválido. Valores válidos: ${StatusPedido.entries.map { it.name }}")
        }

        val pedidoAtualizado = pedidoService.atualizarStatusPedido(id, novoStatus)
        val respostaDTO = PedidoRespostaDTO.deModelo(pedidoAtualizado)

        return ResponseEntity.ok(respostaDTO)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletarPedido(@PathVariable id: String) {
        pedidoService.deletarPedido(id)
    }
}