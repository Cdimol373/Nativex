package com.example.nativex.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nativex.R
import com.example.nativex.database.UserDAO

class AuthActivity : AppCompatActivity() {

    private var isLogin = true // Variable para alternar entre Login y Registro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonAction = findViewById<Button>(R.id.buttonAction)
        val textToggle = findViewById<TextView>(R.id.textToggle)

        buttonAction.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val userDAO = UserDAO(this)

                if (isLogin) {
                    if (userDAO.loginUser(email, password)) {
                        Toast.makeText(this, "Login exitoso ✅", Toast.LENGTH_SHORT).show()
                        // Aquí puedes redirigir al juego o a la siguiente actividad
                    } else {
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (userDAO.registerUser(nombre, email, password)) {
                        Toast.makeText(this, "Registro exitoso ✅", Toast.LENGTH_SHORT).show()
                        // Aquí puedes redirigir al login o directamente al juego
                    } else {
                        Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, completa los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


