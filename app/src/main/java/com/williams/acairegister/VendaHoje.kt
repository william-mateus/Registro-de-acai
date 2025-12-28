package com.williams.acairegister

data class VendaHoje(
    val produto: String,
    val precoProduto: Double,
    val complemento: String?,
    val precoComplemento: Double,
    val hora: String
) {
    val total: Double
        get() = precoProduto + precoComplemento
}

