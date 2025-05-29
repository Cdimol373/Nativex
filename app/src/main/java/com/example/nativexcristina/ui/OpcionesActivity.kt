package com.example.nativexcristina.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nativexcristina.R
import com.example.nativexcristina.database.DatabaseHelper
import androidx.appcompat.app.AlertDialog
import kotlin.system.exitProcess


class OpcionesActivity : AppCompatActivity() {
    private lateinit var btnVerHistorial: Button
    private lateinit var btnVerPuntuacion: Button
    private lateinit var btnVolverAJugar: Button
    private lateinit var btnSalir: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opciones)

        btnVerHistorial = findViewById(R.id.btnVerHistorial)
        btnVerPuntuacion = findViewById(R.id.btnVerPuntuacion)
        btnVolverAJugar = findViewById(R.id.btnVolverAJugar)
        btnSalir = findViewById(R.id.btnSalir)

        btnVerHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }

        btnVerPuntuacion.setOnClickListener {
            val dbHelper = DatabaseHelper(this)
            val puntuacion = dbHelper.obtenerUltimaPuntuacion()
            Toast.makeText(this, "Última puntuación: $puntuacion monedas", Toast.LENGTH_LONG).show()
        }

        btnVolverAJugar.setOnClickListener {
            val intent = Intent(this, PartidaActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnSalir.setOnClickListener {
            mostrarDialogoSalida()
        }
    }

    private fun mostrarDialogoSalida() {
        AlertDialog.Builder(this)
            .setTitle("Salir de la app")
            .setMessage("¿Estás seguro de que quieres salir?")
            .setPositiveButton("Sí") { _, _ ->
                finishAffinity()
                exitProcess(0)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
