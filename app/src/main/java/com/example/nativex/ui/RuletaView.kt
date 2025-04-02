package com.example.nativex.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class RuletaView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val sectores = listOf(
        "+50" to Color.RED,
        "-100" to Color.DKGRAY,
        "+180" to Color.RED,
        "-340" to Color.DKGRAY,
        "Divide entre 2" to Color.CYAN,
        "+100" to Color.RED,
        "-165" to Color.DKGRAY,
        "+340" to Color.RED,
        "-50" to Color.DKGRAY,
        "+220" to Color.RED,
        "-260" to Color.DKGRAY,
        "+165" to Color.RED,
        "Multiplica *2" to Color.GREEN,
        "-220" to Color.DKGRAY,
        "+260" to Color.RED,
        "-180" to Color.DKGRAY
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()
    private var currentAngle = 0f
    private var onRuletaFinishListener: ((String) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = (width.coerceAtMost(height) / 2) - 20
        rectF.set(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius)

        val sweepAngle = 360f / sectores.size
        var startAngle = currentAngle

        sectores.forEach { (text, color) ->
            paint.color = color
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint)
            drawText(canvas, text, startAngle + sweepAngle / 2, radius)
            startAngle += sweepAngle
        }
    }

    private fun drawText(canvas: Canvas, text: String, angle: Float, radius: Float) {
        paint.color = Color.WHITE
        paint.textSize = 40f
        paint.textAlign = Paint.Align.CENTER

        val radian = Math.toRadians(angle.toDouble())
        val x = (width / 2 + cos(radian) * radius * 0.7).toFloat()
        val y = (height / 2 + sin(radian) * radius * 0.7).toFloat()

        canvas.drawText(text, x, y, paint)
    }

    fun girarRuleta() {
        val vueltas = Random.nextInt(5, 10) * 360
        val anguloFinal = (vueltas + Random.nextInt(0, 360)).toFloat()

        val animator = ObjectAnimator.ofFloat(this, "rotation", currentAngle, anguloFinal)
        animator.duration = 3000
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                val sectorIndex = ((anguloFinal % 360) / (360f / sectores.size)).toInt()
                val resultado = sectores[sectorIndex].first
                onRuletaFinishListener?.invoke(resultado)
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        animator.start()

        currentAngle = anguloFinal % 360
    }

    fun setOnRuletaFinishListener(listener: (String) -> Unit) {
        onRuletaFinishListener = listener
    }
}
