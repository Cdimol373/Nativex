/*Esta clase gestiona los segmentos de la ruleta
Sirve para:
Insertar los segmentos (solo si quieres hacerlo dinámicamente desde código y no fijo).
Obtener todos los segmentos definidos (útil para pintar la ruleta en pantalla, por ejemplo).*/
package com.example.nativexcristina.database

import android.content.ContentValues
import android.content.Context
import com.example.nativexcristina.model.Segmento

class SegmentoDAO(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertarSegmento(segmento: Segmento): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("valor", segmento.valor)
            put("tipo", segmento.tipo)
        }
        val result = db.insert("segmento", null, values)
        db.close()
        return result != -1L
    }

    fun obtenerTodos(): List<Segmento> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM segmento", null)
        val segmentos = mutableListOf<Segmento>()

        if (cursor.moveToFirst()) {
            do {
                val segmento = Segmento(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    valor = cursor.getInt(cursor.getColumnIndexOrThrow("valor")),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
                )
                segmentos.add(segmento)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return segmentos
    }
}
