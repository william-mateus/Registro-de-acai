package com.williams.acairegister

data class TipoVenda(
    val nome: String,
    val preco: Double
) {
    override fun toString(): String {
        return nome
    }
}
