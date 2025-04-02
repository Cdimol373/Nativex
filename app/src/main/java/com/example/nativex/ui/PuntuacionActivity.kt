package com.example.nativex.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.nativex.model.Historial
import com.example.nativex.database.DatabaseHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.example.nativex.R

class PuntuacionActivity : AppCompatActivity() {

    private lateinit var tvMonedas: TextView
    private lateinit var listPuntuaciones: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puntuacion)

        tvMonedas = findViewById(R.id.tvMonedasActuales)
        listPuntuaciones = findViewById(R.id.listPuntuaciones)

        // Obtener datos de la base de datos
        val dbHelper = DatabaseHelper(this)

        GlobalScope.launch {
            val historial = dbHelper.obtenerHistorial()

            runOnUiThread {
                if (historial.isNotEmpty()) {
                    val ultimaPartida = historial[0]
                    tvMonedas.text = getString(R.string.monedas_label, ultimaPartida.monedas)
                }

                val listaStrings = historial.map { h ->
                    getString(R.string.puntuacion_item, h.partidasList, h.monedas)
                }

                val adapter = ArrayAdapter(
                    this@PuntuacionActivity,
                    android.R.layout.simple_list_item_1,
                    listaStrings
                )
                listPuntuaciones.adapter = adapter
            }
        }
    }
}