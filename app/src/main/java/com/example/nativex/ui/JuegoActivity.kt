package com.example.nativex.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nativex.R
import com.example.nativex.database.DatabaseHelper


class JuegoActivity : AppCompatActivity() {
    private lateinit var ruletaView: RuletaView
    private lateinit var tvMonedas: TextView
    private lateinit var tvTiradas: TextView
    private var puntuacion = 0
    private var tiradas = 5
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        dbHelper = DatabaseHelper(this)

        ruletaView = findViewById(R.id.ruletaView)
        tvMonedas = findViewById(R.id.tvMonedas)
        tvTiradas = findViewById(R.id.tvTiradas)

        findViewById<Button>(R.id.btnTirar).setOnClickListener {
            if (tiradas > 0) {
                tiradas--
                actualizarUI()
                ruletaView.girarRuleta()
            }
        }

        ruletaView.setOnRuletaFinishListener { resultado ->
            procesarResultado(resultado)
            actualizarUI()
        }

        actualizarUI()
    }

    private fun procesarResultado(resultado: String) {
        when {
            resultado.contains("Multiplica") -> {
                puntuacion *= 2
            }
            resultado.contains("Divide") -> {
                puntuacion /= 2
            }
            resultado.contains("+") || resultado.contains("-") -> {
                puntuacion += resultado.toIntOrNull() ?: 0
            }
        }
        // Guardar cambios en la base de datos
        dbHelper.guardarPuntuacion(puntuacion)
    }

    private fun actualizarUI() {
        tvMonedas.text = "Monedas: $puntuacion"
        tvTiradas.text = "Tiradas: $tiradas"
    }
}
