package com.example.nativexcristina.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.nativexcristina.model.Historial

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        try {
            Log.d("DatabaseHelper", "Creando la base de datos...")

            // Habilito las claves foráneas
            db.execSQL("PRAGMA foreign_keys=ON;")

            // Creación de las tablas
            val createUsuariosTable = """
                CREATE TABLE IF NOT EXISTS usuario (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    contrasena TEXT NOT NULL,
                    monedas INTEGER DEFAULT 1000,
                    partidas INTEGER DEFAULT 0
                );
            """.trimIndent()

            val createPartidasTable = """
                CREATE TABLE IF NOT EXISTS partida (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    usuario_id INTEGER NOT NULL,
                    resultado TEXT NOT NULL,
                    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY(usuario_id) REFERENCES usuario(id)
                );
            """.trimIndent()


            val createRuletaTable = """
                CREATE TABLE IF NOT EXISTS ruleta (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    numero_ganador INTEGER NOT NULL,
                    partida_id INTEGER NOT NULL,
                    segmento INTEGER NOT NULL,
                    FOREIGN KEY(partida_id) REFERENCES partida(id)
                );
            """.trimIndent()

            val createHistorialTable = """
                CREATE TABLE IF NOT EXISTS historial (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    partidas TEXT,
                    monedas INTEGER,
                    partida TEXT -- Nueva columna para almacenar información sobre la partida
                );
            """.trimIndent()

            val createUbicacionesTable = """
                CREATE TABLE IF NOT EXISTS ubicaciones (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    latitud REAL NOT NULL,
                    longitud REAL NOT NULL,
                    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
            """.trimIndent()


            // Ejecutar las consultas de creación de tablas
            db.execSQL(createUsuariosTable)
            db.execSQL(createPartidasTable)
            db.execSQL(createRuletaTable)
            db.execSQL(createHistorialTable)
            db.execSQL(createUbicacionesTable)

            Log.d("DatabaseHelper", "Tablas creadas correctamente")
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al crear la base de datos: ${e.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            Log.d("DatabaseHelper", "Actualizando la base de datos...")

            // Actualizar tabla historial sin eliminar los datos existentes
            if (oldVersion < 3) {
                db.execSQL("ALTER TABLE historial ADD COLUMN partida TEXT")
            }

            // Las demás tablas no se modifican
            Log.d("DatabaseHelper", "Actualización realizada correctamente")
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al actualizar la base de datos: ${e.message}")
        }
    }

    // Función para guardar un historial de partida completo
    fun guardarPartida(historial: Historial) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("partidas", historial.partidasList)
            put("monedas", historial.monedas)
            put("partida", historial.partida)

        }
        db.insert("historial", null, values)
        db.close()
    }

    // Función para obtener el historial completo
    fun obtenerHistorial(): List<Historial> {
        val lista = mutableListOf<Historial>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM historial", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val partidas = cursor.getString(cursor.getColumnIndexOrThrow("partidas"))
                val monedas = cursor.getInt(cursor.getColumnIndexOrThrow("monedas"))
                val partida = cursor.getString(cursor.getColumnIndexOrThrow("partida")) ?: ""
                lista.add(Historial(id, partidas, monedas, partida))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }


    // Función para obtener la última puntuación
    fun obtenerUltimaPuntuacion(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT monedas FROM historial ORDER BY id DESC LIMIT 1", null)

        val puntuacion = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow("monedas"))
        } else 0

        cursor.close()
        db.close()
        return puntuacion
    }

    fun guardarUbicacion(latitud: Double, longitud: Double) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("latitud", latitud)
            put("longitud", longitud)
        }
        db.insert("ubicaciones", null, values)
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "nativexCristina.db"
        private const val DATABASE_VERSION = 3
    }

}