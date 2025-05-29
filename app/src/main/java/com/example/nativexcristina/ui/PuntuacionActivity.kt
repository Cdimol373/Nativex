package com.example.nativexcristina.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nativexcristina.database.DatabaseHelper
import com.example.nativexcristina.R
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class PuntuacionActivity : AppCompatActivity() {

    private lateinit var tvMonedas: TextView
    private lateinit var listPuntuaciones: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puntuacion) // Asegúrate de usar el layout correcto

        tvMonedas = findViewById(R.id.tvMonedasActuales)
        listPuntuaciones = findViewById(R.id.listPuntuaciones)

        // Obtener datos de la base de datos
        val dbHelper = DatabaseHelper(this)

        lifecycleScope.launch {
            val historial = dbHelper.obtenerHistorial()

            runOnUiThread {
                if (historial.isNotEmpty()) {
                    val ultimaPartida = historial.last()
                    tvMonedas.text = getString(R.string.monedas_label, ultimaPartida.monedas)
                }

                val listaStrings = historial.map { h ->
                    "${h.partidasList} → ${h.monedas} monedas\nResumen: ${h.partida}"
                }

                val adapter = ArrayAdapter(
                    this@PuntuacionActivity,
                    android.R.layout.simple_list_item_1, // Usa un layout adecuado para el ListView
                    listaStrings
                )
                listPuntuaciones.adapter = adapter
            }
        }
    }
}
