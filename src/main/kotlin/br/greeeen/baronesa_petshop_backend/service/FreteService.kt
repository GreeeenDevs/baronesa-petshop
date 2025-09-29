package br.greeeen.baronesa_petshop_backend.service

import br.greeeen.baronesa_petshop_backend.model.CarrinhoDeCompras
import java.math.BigDecimal

interface FreteService {

    fun calcularFrete(cepDestino: String, carrinho: CarrinhoDeCompras): BigDecimal
}