package com.example.nativex.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nativex.R
import com.example.nativex.database.DatabaseHelper
import com.example.nativex.model.Historial

class HistorialActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var historialAdapter: HistorialAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        recyclerView = findViewById(R.id.recyclerHistorial)
        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseHelper = DatabaseHelper(this)
        val historialList = databaseHelper.obtenerHistorial()

        historialAdapter = HistorialAdapter(historialList)
        recyclerView.adapter = historialAdapter
    }
}
