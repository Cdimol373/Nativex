package com.example.nativexcristina.services

import android.app.Activity
import android.graphics.Bitmap
import android.provider.CalendarContract
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
//noinspection SuspiciousImport
import android.R
import android.content.ContentValues
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationChannelCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Build
import android.widget.Toast
import com.example.nativexcristina.database.DatabaseHelper
import com.google.android.gms.location.LocationServices

@Suppress("DEPRECATION")
class GameServices(private val context: Context) {

    // Captura de pantalla
    fun capturarPantalla(activity: Activity) {
        try {
            val view = activity.window.decorView.rootView
            view.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false

            // Guarda la imagen en la galería
            guardarImagenEnGaleria(bitmap)

            // También puedes crear un archivo y enviarlo a la galería
            MediaScannerConnection.scanFile(context, arrayOf(bitmap.toString()), null, null)

        } catch (e: Exception) {
            Log.e("GameServices", "Error capturando pantalla", e)
        }
    }

    // Guardar imagen en la galería
    private fun guardarImagenEnGaleria(bitmap: Bitmap) {
        try {
            val file = File(context.getExternalFilesDir(null), "victoria_${System.currentTimeMillis()}.png")
            val outStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()

            val contentUri: Uri = Uri.fromFile(file)
            MediaScannerConnection.scanFile(context, arrayOf(contentUri.toString()), null, null)

        } catch (e: IOException) {
            Log.e("GameServices", "Error al guardar la imagen", e)
        }
    }

    // Crear una notificación
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun enviarNotificacionDeVictoria() {
        val notificationManager = NotificationManagerCompat.from(context)
        val channelId = "victory_channel"
        val channel = NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName("Victoria")
            .setDescription("Notificación de victoria")
            .build()
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("¡Victoria!")
            .setContentText("¡Has ganado el juego!")
            .setSmallIcon(R.drawable.star_on)
            .build()

        notificationManager.notify(1, notification)
    }

    // Guardar en el calendario
    fun guardarVictoriaEnCalendario() {
        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, 1)
            put(CalendarContract.Events.TITLE, "Victoria en el juego")
            put(CalendarContract.Events.DTSTART, Calendar.getInstance().timeInMillis)
            put(CalendarContract.Events.DTEND, Calendar.getInstance().timeInMillis + 60000) // duración de un minuto
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    }

    // Revisar permisos
    fun revisarPermisos(activity: Activity) {
        val permisosPendientes = mutableListOf<String>()

        // Permiso de notificaciones (solo si Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            permisosPendientes.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Permisos de ubicación
        if (
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            permisosPendientes.addAll(
                listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        // Pedir permisos si hay alguno pendiente
        if (permisosPendientes.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, permisosPendientes.toTypedArray(), 101)
        }
    }


    @SuppressLint("MissingPermission")
    fun obtenerYGuardarUbicacion() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    guardarUbicacionEnBD(it.latitude, it.longitude)
                } ?: run {
                    Toast.makeText(context, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun guardarUbicacionEnBD(lat: Double, lon: Double) {
        val dbHelper = DatabaseHelper(context)
        dbHelper.guardarUbicacion(lat, lon)
    }
}
