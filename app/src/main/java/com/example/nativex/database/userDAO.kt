/*útil para manejar el registro y login de los usuarios utilizando la base de datos SQLite*/
package com.example.nativex.database
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.security.MessageDigest
import java.util.Base64

class UserDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Método para registrar un usuario
    fun registerUser(nombre: String, email: String, contraseña: String): Boolean {
        val db: SQLiteDatabase = dbHelper.writableDatabase

        // Encriptar la contraseña
        val encryptedPassword = encryptPassword(contraseña)

        val values = ContentValues().apply {
            put("nombre", nombre)
            put("email", email)
            put("contraseña", encryptedPassword)  // Guardamos la contraseña encriptada
            put("monedas", 1000)  // Valor inicial de monedas
        }

        // Realizar la inserción
        val result = db.insert("usuarios", null, values)
        db.close()

        // Si result == -1, significa que la inserción falló
        return result != -1L
    }

    // Método para verificar el login del usuario
    @SuppressLint("Range")
    fun loginUser(email: String, password: String): Boolean {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val query = "SELECT * FROM usuarios WHERE email = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(email))

        if (cursor.moveToFirst()) {
            val storedPassword = cursor.getString(cursor.getColumnIndex("contraseña"))
            // Comparar la contraseña encriptada
            if (storedPassword == encryptPassword(password)) {
                cursor.close()
                db.close()
                return true  // Login exitoso
            }
        }

        cursor.close()
        db.close()
        return false  // Login fallido
    }

    // Método para encriptar las contraseñas
    private fun encryptPassword(password: String): String {
        try {
            val md = MessageDigest.getInstance("SHA-256")
            val hashBytes = md.digest(password.toByteArray())
            return Base64.getEncoder().encodeToString(hashBytes) // Encriptamos la contraseña en SHA-256 y la convertimos a Base64
        } catch (e: Exception) {
            Log.e("UserDAO", "Error al encriptar la contraseña", e)
            throw RuntimeException("Error al encriptar la contraseña", e)
        }
    }
}
