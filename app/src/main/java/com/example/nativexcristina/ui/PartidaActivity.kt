package com.example.nativexcristina.ui


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.nativexcristina.R
import com.example.nativexcristina.database.DatabaseHelper
import com.example.nativexcristina.model.Historial
import com.example.nativexcristina.services.MusicService
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.RequiresPermission
import com.example.nativexcristina.database.GameDAO
import com.example.nativexcristina.model.Partida
import com.example.nativexcristina.services.GameServices
import com.google.android.material.button.MaterialButton
import androidx.core.graphics.createBitmap


class PartidaActivity : AppCompatActivity() {
    private lateinit var tvMonedas: TextView
    private lateinit var tvTiradas: TextView
    private lateinit var ruletaImageView: ImageView
    private lateinit var btnTirar: Button
    private lateinit var btnSalir: Button
    private lateinit var btnSonido: ImageView
    private lateinit var btnGuardarCaptura: MaterialButton
    private lateinit var imageViewCaptura: ImageView



    private var monedas = 100
    private var tiradasRestantes = 5
    private var historialJugadas = mutableListOf<String>()

    private var lastAngle = 0f
    private val totalSegmentos = 8
    private val gradosPorSegmento = 360f / totalSegmentos

    private var sonidoActivo = true //Sonido de fondo

    private lateinit var soundPool: SoundPool //Para el sonido de start
    private var soundIdBoton: Int = -1

    // Variables para la música de victoria y derrota
    private var victoriaPlayer: MediaPlayer? = null
    private var derrotaPlayer: MediaPlayer? = null

    // Variable de control global para silenciar
    private var sonidoSilenciado = false

    //Clase que maneja captura de pantalla
    private lateinit var gameServices: GameServices
    private var partidaId: Long = -1


    // Base de datos para registrar las partidas y jugadas
    private lateinit var gameDAO: GameDAO



    @SuppressLint("ImplicitSamInstance", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partida)

        tvMonedas = findViewById(R.id.tvMonedas)
        tvTiradas = findViewById(R.id.tvTiradas)
        ruletaImageView = findViewById(R.id.ruletaImageView)
        btnTirar = findViewById(R.id.btnTirar)
        btnSalir = findViewById(R.id.btnSalir)
        val btnReiniciar = findViewById<Button>(R.id.btnReiniciar)
        btnSonido = findViewById(R.id.btnSonido)
        gameServices = GameServices(this)
        btnGuardarCaptura = findViewById(R.id.btnGuardarCaptura)
        imageViewCaptura = findViewById(R.id.imageViewCaptura)

        // Inicializar el GameDAO
        gameDAO = GameDAO(this)
        partidaId = obtenerPartidaId()



        //Inicio el sonido de start
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()
        soundIdBoton = soundPool.load(this, R.raw.boton_corto, 1)

        // Inicialización de MediaPlayer para las canciones
        victoriaPlayer = MediaPlayer.create(this, R.raw.victoria1)
        derrotaPlayer = MediaPlayer.create(this, R.raw.perdiste)

        inicializarRuleta()
        actualizarTexto()

        // Manejador del botón de sonido
        btnSonido.setOnClickListener {
            sonidoActivo = !sonidoActivo
            sonidoSilenciado = !sonidoSilenciado // Cambiar estado de silenciar
            if (sonidoActivo) {
                startService(Intent(this, MusicService::class.java))
                btnSonido.setImageResource(R.drawable.altavoz_on)
            } else {
                stopService(Intent(this, MusicService::class.java))
                btnSonido.setImageResource(R.drawable.altavoz_off)
            }
            // Silenciar todo el juego
            manejarSilencio()
        }

        // Método para el botón de tirar
        btnTirar.setOnClickListener {
            // Reproducir el sonido de botón solo si no está silenciado
            if (!sonidoSilenciado) {
                soundPool.play(soundIdBoton, 1f, 1f, 0, 0, 1f)
            }
            if (tiradasRestantes > 0) {
                tiradasRestantes--
                actualizarTexto()
                btnTirar.isEnabled = false
                girarRuletaConImagen { index ->
                    aplicarResultado(index)
                    btnTirar.isEnabled = true
                }
            } else {
                Toast.makeText(this, "¡No te quedan más tiradas!", Toast.LENGTH_SHORT).show()
                mostrarFinDePartida()
            }
        }
        btnGuardarCaptura.setOnClickListener {
            val bitmap = capturaPantalla()
            if (bitmap != null) {
                imageViewCaptura.visibility = View.VISIBLE
                imageViewCaptura.setImageBitmap(bitmap)
                Toast.makeText(this, "Captura realizada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al capturar", Toast.LENGTH_SHORT).show()
            }
        }


        btnSalir.setOnClickListener {
            finish()
        }

        btnReiniciar.setOnClickListener {
            reiniciarPartida()
        }
    }


    // Esta funcion inicia la música si está activa al volver a la actividad
    override fun onResume() {
        super.onResume()
        if (sonidoActivo) {
            startService(Intent(this, MusicService::class.java))
        }
    }

    // Esta función detiene la música cuando la actividad queda en segundo plano
    @SuppressLint("ImplicitSamInstance")
    override fun onPause() {
        super.onPause()
        stopService(Intent(this, MusicService::class.java))
    }



    private fun inicializarRuleta() {
        lastAngle = 0f
        ruletaImageView.rotation = lastAngle
    }

    private fun girarRuletaConImagen(onResultado: (Int) -> Unit) {
        val segmentoGanador = (0 until totalSegmentos).random()
        val anguloGanador = segmentoGanador * gradosPorSegmento + gradosPorSegmento / 2
        val vueltasCompletas = (5..8).random() * 360f
        val anguloFinal = vueltasCompletas + anguloGanador

        val rotateAnimation = RotateAnimation(
            lastAngle,
            lastAngle + anguloFinal,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        rotateAnimation.duration = 3000
        rotateAnimation.fillAfter = true

        rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                lastAngle = (lastAngle + anguloFinal) % 360f
                if (lastAngle < 0) lastAngle += 360f
                val segmento = ((lastAngle + gradosPorSegmento / 2) / gradosPorSegmento).toInt() % totalSegmentos
                onResultado(segmento)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        ruletaImageView.startAnimation(rotateAnimation)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun aplicarResultado(index: Int) {
        val colores = arrayOf("Rojo", "Negro", "Rojo", "Negro", "Rojo", "Negro", "Rojo", "Negro")
        val color = colores[index]
        val mensaje: String

        if (color == "Rojo") {
            if (monedas < 5) {
                mensaje = "¡Color ROJO! No tienes suficientes monedas para dividir más. El juego ha terminado."
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                mostrarFinDePartida()
                return
            }
            monedas /= 5
            mensaje = "¡Color ROJO! Tus monedas se DIVIDEN entre 5."
        } else {
            monedas *= 5
            mensaje = "¡Color NEGRO! Tus monedas se MULTIPLICAN por 5."
        }

        // Registrar el cambio en las monedas
        val usuarioId = obtenerUsuarioId()  // Deberías tener una función para obtener el ID del usuario
        gameDAO.actualizarMonedas(usuarioId, monedas)

        // Registrar la jugada
        val segmentoId = index  // El segmento correspondiente (en este caso el índice de color)
        val numeroGanador = (0 until totalSegmentos).random()  // Número ganador al azar (esto debe ser cambiado si tienes una lógica diferente para el número ganador)
        gameDAO.registrarJugada(partidaId, segmentoId, numeroGanador)

        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        historialJugadas.add(color)

        // Verificar si la partida debe terminar
        if (monedas <= 0 || tiradasRestantes == 0) {
            mostrarFinDePartida()
        }
    }

    // Simula obtener el ID de usuario desde el intent o preferencias
    private fun obtenerUsuarioId(): Int {
        // TODO: Cambia esto si obtienes el ID desde otro sitio como SharedPreferences o intent
        return intent.getIntExtra("usuario_id", -1)
    }

    // Guarda la partida al iniciar la actividad y devuelve su ID
    private fun obtenerPartidaId(): Long {
        val usuarioId = obtenerUsuarioId()
        val partida = Partida(
            usuarioId = usuarioId,
            resultado = "ganada" // o "perdida", según tu lógica
        )
        return gameDAO.insertarPartida(partida)
    }


    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @SuppressLint("ImplicitSamInstance")
    private fun mostrarFinDePartida() {
        // Guardar en historial
        guardarResultadoEnHistorial()

        // Detener música de fondo si está activa
        if (!sonidoSilenciado) {
            stopService(Intent(this, MusicService::class.java))
        }

        if (monedas <= 0) {
            // Derrota
            Toast.makeText(this, "¡Te has quedado sin monedas! Has perdido.", Toast.LENGTH_LONG).show()
            if (!sonidoSilenciado) {
                derrotaPlayer?.start()
            }
        } else {
            // Victoria
            Toast.makeText(this, "¡Has terminado con $monedas monedas! ¡Victoria!", Toast.LENGTH_LONG).show()
            if (!sonidoSilenciado) {
                victoriaPlayer?.start()
            }

            // Funciones extra al ganar
            gameServices.revisarPermisos(this)
            gameServices.capturarPantalla(this)
            gameServices.guardarVictoriaEnCalendario()
            gameServices.obtenerYGuardarUbicacion()
            gameServices.enviarNotificacionDeVictoria()
        }

        // Volver a OpcionesActivity tras 6 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, OpcionesActivity::class.java)
            startActivity(intent)
            finish()
        }, 6000)
    }


    private fun reiniciarPartida() {
        monedas = 100
        tiradasRestantes = 5
        historialJugadas.clear()
        actualizarTexto()
        inicializarRuleta()

        // Reiniciar partida en la base de datos
        partidaId = obtenerPartidaId()

        // Detener sonidos anteriores si están activos
        victoriaPlayer?.stop()
        victoriaPlayer?.prepare()
        derrotaPlayer?.stop()
        derrotaPlayer?.prepare()

        // Reiniciar música si corresponde
        if (sonidoActivo && !sonidoSilenciado) {
            startService(Intent(this, MusicService::class.java))
        }

        Toast.makeText(this, "Partida reiniciada", Toast.LENGTH_SHORT).show()
    }
    private fun actualizarTexto() {
        tvMonedas.text = getString(R.string.monedas_label, monedas)
        tvTiradas.text = getString(R.string.tiradas_label, tiradasRestantes)
    }

    private fun guardarResultadoEnHistorial() {
        val dbHelper = DatabaseHelper(this)
        val partidaList = historialJugadas.joinToString(",")
        val resumen = "Realizó ${historialJugadas.size} tiradas y tienes $monedas monedas."

        val historial = Historial(
            idHistorial = 0,
            partidasList = partidaList,
            monedas = monedas,
            partida = resumen
        )
        dbHelper.guardarPartida(historial)
    }

    @SuppressLint("ImplicitSamInstance")
    private fun manejarSilencio() {
        // Si se silencia el juego, detener toda la música y efectos de sonido
        if (sonidoSilenciado) {
            victoriaPlayer?.pause()
            derrotaPlayer?.pause()
            soundPool.autoPause() // Pausar todos los efectos de sonido
            stopService(Intent(this, MusicService::class.java)) // Detener la música de fondo
        } else {
            if (sonidoActivo) {
                startService(Intent(this, MusicService::class.java)) // Iniciar música de fondo si está activa
            }
        }
    }

    // Función para capturar la pantalla (la vista root)
    private fun capturaPantalla(): Bitmap? {
        return try {
            val view = window.decorView.rootView
            val bitmap = createBitmap(view.width, view.height)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        victoriaPlayer?.release()
        derrotaPlayer?.release()
        soundPool.release()
    }
}