package com.example.nativexcristina.model

data class Usuario(
    val id: Int = 0,  // El id predeterminado es 0, ya que es autoincremental
    val nombre: String,
    val email: String,
    val contrasena: String,
    val monedas: Int = 1000,  // Valor predeterminado para las monedas
    val partidas: Int = 0    // Valor predeterminado para las partidas
)
