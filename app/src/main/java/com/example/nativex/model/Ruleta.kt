package com.example.nativex.model

data class Ruleta(
    val idRuleta: Int,
    val numeroGanador: Int,
    val valorSegmento: Int,
    val tipoSegmento: String,  // 'positivo' o 'negativo'
    val usuarioId: Int,
    val fecha: String // o Timestamp, según lo que necesites
)