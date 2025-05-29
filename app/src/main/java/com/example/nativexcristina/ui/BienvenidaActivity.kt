package com.example.nativexcristina.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.nativexcristina.R


class BienvenidaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        // Handler para redirigir a la pantalla de login después de 3 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            // Iniciar la actividad de autenticación
            val intent = Intent(this, PartidaActivity::class.java)
            startActivity(intent)
            finish() // Finalizar la actividad de bienvenida para que no se quede en el stack
        }, 3000) // 3000 milisegundos = 3 segundos
    }
}
