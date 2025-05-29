package com.example.nativexcristina.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nativexcristina.R
import com.example.nativexcristina.database.SegmentoInitializer
import com.example.nativexcristina.auth.AuthActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar segmentos
        val segmentoInitializer = SegmentoInitializer(this)
        segmentoInitializer.inicializarSegmentos()

        // Lanzar la pantalla de autenticación
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }



    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Inicializo el valor de mis segmentos llamando a SegmentoInitializer de la datebase
        val segmentoInitializer = SegmentoInitializer(this)
        segmentoInitializer.inicializarSegmentos()


        // Iniciar la actividad de bienvenida pq es mi primera activity a mostrar
        val intent = Intent(this, BienvenidaActivity::class.java)
        startActivity(intent)
        finish() // Finalizo MainActivity para que no quede en el stack
    }*/
}
/*Clase para pobrar el juego en si
* override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Inicializar segmentos en base de datos
    val segmentoInitializer = SegmentoInitializer(this)
    segmentoInitializer.inicializarSegmentos()

    // TEMPORAL: lanzar directamente la partida para pruebas
    val intent = Intent(this, PartidaActivity::class.java)
    startActivity(intent)
    finish()
}
*/