package com.example.nativexcristina.ui

import android.os.Bundle
import android.content.Intent
import android.app.AlertDialog
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nativexcristina.R
import com.example.nativexcristina.database.DatabaseHelper

class HistorialActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var historialAdapter: HistorialAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        recyclerView = findViewById(R.id.recyclerViewHistorial)
        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseHelper = DatabaseHelper(this)
        val historialList = databaseHelper.obtenerHistorial()

        historialAdapter = HistorialAdapter(historialList)
        recyclerView.adapter = historialAdapter

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            val intent = Intent(this, OpcionesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Mostrar diálogo si viene de partida finalizada
        if (intent.getBooleanExtra("mostrarDialogo", false)) {
            mostrarDialogoOpciones()
        }
    }

    private fun mostrarDialogoOpciones() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Qué deseas hacer?")
        builder.setMessage("Has finalizado la partida. ¿Quieres jugar otra o salir?")

        builder.setPositiveButton("Volver a jugar") { _, _ ->
            val intent = Intent(this, PartidaActivity::class.java)
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("Salir") { _, _ ->
            finishAffinity()
        }

        builder.setCancelable(false)
        builder.show()
    }
}

