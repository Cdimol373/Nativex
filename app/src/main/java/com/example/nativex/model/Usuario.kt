package com.example.nativex.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val contrasena: String,
    val monedas: Int,
    val partidas: Int
)