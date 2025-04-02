package com.example.nativex

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nativex.database.DatabaseHelper
import com.example.nativex.ui.BienvenidaActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa la base de datos
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase
        db.close()

        // Redirigir a BienvenidaActivity
        val intent = Intent(this, BienvenidaActivity::class.java)
        startActivity(intent)
        finish()
    }
}
