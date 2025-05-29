package com.example.nativexcristina.database

import android.content.Context
import com.example.nativexcristina.model.Segmento

class SegmentoInitializer(private val context: Context) {
    private val segmentoDao = SegmentoDAO(context)  // Usamos el SegmentoDao para manejar la base de datos

    // Método para inicializar los segmentos
    fun inicializarSegmentos() {
        // Verificamos si ya existen segmentos en la base de datos
        val segmentos = segmentoDao.obtenerTodos()

        // Si no existen segmentos en la base de datos, los insertamos
        if (segmentos.isEmpty()) {
            insertarSegmentos()
        }
    }

    // Método para insertar los segmentos predeterminados en la base de datos
    private fun insertarSegmentos() {
        val nuevosSegmentos = listOf(
            Segmento(valor = 50, tipo = "positivo"),
            Segmento(valor = -100, tipo = "negativo"),
            Segmento(valor = 180, tipo = "positivo"),
            Segmento(valor = -340, tipo = "negativo"),
            Segmento(valor = 0, tipo = "bonus"),
            Segmento(valor = 100, tipo = "positivo"),
            Segmento(valor = -165, tipo = "negativo"),
            Segmento(valor = 340, tipo = "positivo"),
            Segmento(valor = -50, tipo = "negativo"),
            Segmento(valor = 220, tipo = "positivo"),
            Segmento(valor = -260, tipo = "negativo"),
            Segmento(valor = 165, tipo = "positivo"),
            Segmento(valor = 0, tipo = "bonus"),
            Segmento(valor = -220, tipo = "negativo"),
            Segmento(valor = 260, tipo = "positivo"),
            Segmento(valor = -180, tipo = "negativo")
        )

        // Insertamos los nuevos segmentos en la base de datos
        nuevosSegmentos.forEach { segmentoDao.insertarSegmento(it) }
    }
}