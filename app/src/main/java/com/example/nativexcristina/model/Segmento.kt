package com.example.nativexcristina.model

data class Segmento(
    val id: Int = 0,
    val valor: Int,
    val tipo: String // 'positivo', 'negativo', 'bonus'
)