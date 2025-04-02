package com.example.nativex.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.nativex.R

class OpcionesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opciones)

        val btnJugar = findViewById<Button>(R.id.btnJugarRuleta)
        val btnHistorial = findViewById<Button>(R.id.btnHistorial)
        val btnConfig = findViewById<Button>(R.id.btnConfig)
        val btnSalir = findViewById<Button>(R.id.btnSalir)

        btnJugar.setOnClickListener {
            // Ir a la pantalla del juego (la ruleta)
            val intent = Intent(this, JuegoActivity::class.java)
            startActivity(intent)
        }

        btnHistorial.setOnClickListener {
            // Ir a la pantalla del Historial
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }

        btnConfig.setOnClickListener {
            // Ir a la pantalla de Configuración
            val intent = Intent(this, ConfigActivity::class.java)
            startActivity(intent)
        }

        btnSalir.setOnClickListener {
            finish()  // Cierra esta activity
        }
    }
}