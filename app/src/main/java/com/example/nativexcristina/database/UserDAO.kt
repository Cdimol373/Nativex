/*Esta clse sirve para la gestión de los usuarios (registro, login y  monedas
* Sirve para:
Insertar nuevos usuarios (registro).
Buscar usuarios por email (para login o recuperación).
Actualizar las monedas de un usuario (cuando gana o pierde en el juego).*/
package com.example.nativexcristina.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.nativexcristina.model.Usuario

class UserDAO(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Método para insertar un nuevo usuario
    fun insertarUsuario(usuario: Usuario): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", usuario.nombre)
            put("email", usuario.email)
            put("contrasena", usuario.contrasena)
            put("monedas", usuario.monedas)
            put("partidas", usuario.partidas)
        }
        return db.insert("usuario", null, values).also { db.close() } != -1L
    }

    fun existeUsuario(email: String): Boolean {
        return buscarUsuarioPorEmail(email) != null
    }


    // Método para buscar un usuario por su email
    fun buscarUsuarioPorEmail(email: String): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM usuario WHERE email = ?", arrayOf(email))
        var usuario: Usuario? = null

        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                contrasena = cursor.getString(cursor.getColumnIndexOrThrow("contrasena")),
                monedas = cursor.getInt(cursor.getColumnIndexOrThrow("monedas")),
                partidas = cursor.getInt(cursor.getColumnIndexOrThrow("partidas"))
            )
        }
        cursor.close()
        db.close()
        return usuario
    }

    // Método para actualizar las monedas de un usuario
    fun actualizarMonedas(usuarioId: Int, nuevasMonedas: Int): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("monedas", nuevasMonedas)
        }
        return db.update("usuario", values, "id = ?", arrayOf(usuarioId.toString())).also { db.close() } > 0

    }

    // Método para realizar el login del usuario
    fun loginUser(email: String, password: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM usuario WHERE email = ? AND contrasena = ?", arrayOf(email, password))

        // Si hay al menos un registro, es un login exitoso
        val isLoginSuccess = cursor.count > 0
        cursor.close()
        db.close()
        return isLoginSuccess
    }
}
