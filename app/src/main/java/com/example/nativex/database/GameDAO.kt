/*Esta clase maneja la lógica de guardar las jugadas del juego,
incluyendo la actualización de las monedas del usuario y el registro del resultado de cada jugada en la base de datos.*/
package com.example.nativex.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.nativex.model.Usuario

class GameDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun saveGame(usuarioId: Int, monedasApostadas: Int): Int {
        val db: SQLiteDatabase = dbHelper.writableDatabase

        val segmentos = arrayOf(
            100, 200, -50, 150, -100, 250, -150, 300,
            50, -25, 100, -75, 200, 150, -50, 300
        )

        val numeroGanador = (0 until 16).random()
        val valorSegmento = segmentos[numeroGanador]
        val tipoSegmento = if (valorSegmento >= 0) "positivo" else "negativo"


        val values = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("numero_ganador", numeroGanador + 1)
            put("valor_segmento", valorSegmento)
            put("tipo_segmento", tipoSegmento)
        }
        db.insert("ruleta", null, values)

        val usuario = obtenerUsuarioPorId(usuarioId)
        val nuevasMonedas = usuario.monedas + valorSegmento

        val updateValues = ContentValues().apply {
            put("monedas", nuevasMonedas)
        }
        db.update("usuarios", updateValues, "id = ?", arrayOf(usuarioId.toString()))
        db.close()

        return valorSegmento
    }

    fun obtenerUsuarioPorId(usuarioId: Int): Usuario {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val query = "SELECT * FROM usuarios WHERE id = ?"
        val cursor = db.rawQuery(query, arrayOf(usuarioId.toString()))

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            val monedas = cursor.getInt(cursor.getColumnIndexOrThrow("monedas"))
            val partidas = cursor.getInt(cursor.getColumnIndexOrThrow("partidas")) // 🛠️ Agregado

            val usuario = Usuario(
                usuarioId, nombre, email, monedas.toString(), partidas,

                partidas = TODO()
            )
            cursor.close()
            return usuario
        }

        cursor.close()
        throw Exception("Usuario no encontrado")
    }
}