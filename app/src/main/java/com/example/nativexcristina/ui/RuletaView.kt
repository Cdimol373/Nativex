package com.example.nativexcristina.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class RuletaView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    data class Segmento(val texto: String, val color: Int)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()

    // Lista de segmentos de la ruleta
    private val segmentos = listOf(
        Segmento("+220", 0xFFFF0000.toInt()),
        Segmento("-260", 0xFF444444.toInt()),
        Segmento("+165", 0xFFFF0000.toInt()),
        Segmento("Multiplica *2", 0xFF4CAF50.toInt()),
        Segmento("-220", 0xFF444444.toInt()),
        Segmento("+260", 0xFFFF0000.toInt()),
        Segmento("-180", 0xFF444444.toInt()),
        Segmento("+50", 0xFFFF0000.toInt()),
        Segmento("-100", 0xFF444444.toInt()),
        Segmento("+180", 0xFFFF0000.toInt()),
        Segmento("-340", 0xFF444444.toInt()),
        Segmento("Divide entre 2", 0xFF00FFFF.toInt()),
        Segmento("+100", 0xFFFF0000.toInt()),
        Segmento("-165", 0xFF444444.toInt()),
        Segmento("+340", 0xFFFF0000.toInt()),
        Segmento("-50", 0xFF444444.toInt())
    )

    // Callback para notificar cuando se selecciona un segmento
    interface RuletaCallback {
        fun onSegmentSelected(segmento: Segmento)
    }

    private var callback: RuletaCallback? = null

    fun setRuletaCallback(callback: RuletaCallback) {
        this.callback = callback
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val radius = min(width, height) / 2f
        val segmentAngle = 360f / segmentos.size

        // Inicializar el rectángulo para la ruleta (centrado)
        rect.set(cx - radius, cy - radius, cx + radius, cy + radius)

        for (i in segmentos.indices) {
            // Dibujo del segmento
            paint.color = segmentos[i].color
            canvas.drawArc(rect, segmentAngle * i, segmentAngle, true, paint)

            // Dibujo del texto en cada segmento
            paint.color = Color.WHITE
            paint.textSize = 36f
            paint.textAlign = Paint.Align.CENTER

            val angle = Math.toRadians((segmentAngle * i + segmentAngle / 2).toDouble())
            val textRadius = radius * 0.6f
            val x = (cx + textRadius * cos(angle)).toFloat()
            val y = (cy + textRadius * sin(angle)).toFloat()

            canvas.drawText(segmentos[i].texto, x, y, paint)
        }
    }

    // Método para animar el giro de la ruleta
    fun girarRuleta() {
        val vueltas = (5..10).random() * 360  // Aleatorio entre 5 y 10 vueltas
        val anguloFinal = (vueltas + (0..360).random()).toFloat()

        // Crear la animación para girar la ruleta
        val animator = ObjectAnimator.ofFloat(this, "rotation", rotation, anguloFinal)
        animator.duration = 3000  // Duración de 3 segundos
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                // Calcular el índice del segmento seleccionado
                val sectorIndex = ((anguloFinal % 360) / (360f / segmentos.size)).toInt()
                val resultado = segmentos[sectorIndex]

                // Notificar al callback el segmento seleccionado
                callback?.onSegmentSelected(resultado)
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        animator.start()
    }
}

