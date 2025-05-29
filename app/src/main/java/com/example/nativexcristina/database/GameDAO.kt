/*Esta clase sirve para gestionar las partidas y jugadas
Sirve para:
Insertar una nueva partida.
Registrar las jugadas de esa partida (por ejemplo: qué segmento cayó en la ruleta).*/
package com.example.nativexcristina.database

import android.content.ContentValues
import android.content.Context
import com.example.nativexcristina.model.Partida

class GameDAO(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Método para insertar una nueva partida en la base de datos
    fun insertarPartida(partida: Partida): Long {
        val db = dbHelper.writableDatabase

        //Validacion de seguridad
        if (partida.resultado.isEmpty()) throw IllegalArgumentException("Resultado de la partida no puede estar vacío")

        val values = ContentValues().apply {
            put("usuario_id", partida.usuarioId)
            put("resultado", partida.resultado)
            put("fecha", partida.fecha.time) // timestamp en milisegundos
        }

        val partidaId = db.insert("partida", null, values)

        // Incrementar contador de partidas del usuario
        db.execSQL("UPDATE usuario SET partidas = partidas + 1 WHERE id = ?", arrayOf(partida.usuarioId))

        db.close()
        return partidaId
    }

    fun registrarJugada(partidaId: Long, segmentoId: Int, numeroGanador: Int): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("partida_id", partidaId)
            put("segmento_id", segmentoId)
            put("numero_ganador", numeroGanador)
        }
        val result = db.insert("ruleta", null, values)
        db.close()
        return result != -1L
    }

    // Método para actualizar las monedas del usuario
    fun actualizarMonedas(usuarioId: Int, monedas: Int): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("monedas", monedas)
        }
        val result = db.update("usuario", values, "id = ?", arrayOf(usuarioId.toString()))
        db.close()
        return result > 0
    }
}