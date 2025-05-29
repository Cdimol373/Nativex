package com.example.nativexcristina.model


import java.util.Date

data class Partida(
    val id: Int = 0,
    val usuarioId: Int,
    val resultado: String,
    val fecha: Date = Date()
)