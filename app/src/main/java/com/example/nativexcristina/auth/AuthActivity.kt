package com.example.nativexcristina.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nativexcristina.R
import com.example.nativexcristina.database.UserDAO
import com.example.nativexcristina.model.Usuario
import com.example.nativexcristina.ui.BienvenidaActivity
import android.content.Intent
import android.view.View

class AuthActivity : AppCompatActivity() {

    private var isLogin = true // Variable para alternar entre Login y Registro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val userDAO = UserDAO(this)

        // Usuario de prueba
        val usuarioPrueba = Usuario(nombre = "Cristina", email = "cristina@email.com", contrasena = "1234")
        if (!userDAO.existeUsuario(usuarioPrueba.email)) {
            userDAO.insertarUsuario(usuarioPrueba)
        }

        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val editTextName = findViewById<EditText>(R.id.editTextName) // Añadimos un EditText para el nombre
        val buttonAction = findViewById<Button>(R.id.buttonAction)
        val textToggle = findViewById<TextView>(R.id.textToggle)
        val btnLoginCristina = findViewById<Button>(R.id.btnLoginCristina)

        btnLoginCristina.setOnClickListener {
            val email = "cristina@email.com"
            val password = "1234"
            if (userDAO.loginUser(email, password)){
                Toast.makeText(this, "Bienvenida Cristina", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, BienvenidaActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, "Error al iniciar como Cristina", Toast.LENGTH_SHORT).show()
            }
        }

        textToggle.setOnClickListener {
            isLogin = !isLogin
            if (isLogin) {
                buttonAction.text = getString(R.string.iniciar_sesi_n)
                textToggle.text = getString(R.string.no_tienes_cuenta_reg_strate)
                editTextName.visibility = View.GONE // Oculta el campo nombre en login
            } else {
                buttonAction.text = getString(R.string.registrarse)
                textToggle.text = getString(R.string.ya_tienes_cuenta_inicia_sesi_n)
                editTextName.visibility = View.VISIBLE // Muestra el campo nombre en registro
            }
        }



        // Acción del botón principal (login o registro)
        buttonAction.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val name = editTextName.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && (isLogin || name.isNotEmpty())) {
                if (isLogin) {
                    if (userDAO.loginUser(email, password)) {
                        Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, BienvenidaActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val nuevoUsuario = Usuario(nombre = name, email = email, contrasena = password)
                    if (userDAO.insertarUsuario(nuevoUsuario)) {
                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        // Puedes redirigir directamente:
                        startActivity(Intent(this, BienvenidaActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Ocultar campo nombre si estás en login por defecto
        editTextName.visibility = View.GONE
    }
}






