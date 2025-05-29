package com.example.nativexcristina.services

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.IBinder
import com.example.nativexcristina.R

class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var audioManager: AudioManager
    private lateinit var focusRequest: AudioFocusRequest

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setOnAudioFocusChangeListener { focusChange ->
                when (focusChange) {
                    AudioManager.AUDIOFOCUS_LOSS,
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pauseMusic()
                    AudioManager.AUDIOFOCUS_GAIN -> resumeMusic()
                }
            }
            .build()

        if (audioManager.requestAudioFocus(focusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer = MediaPlayer.create(this, R.raw.sonido_fondo_animado).apply {
                isLooping = true
                start()
            }
        }
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
    }

    private fun resumeMusic() {
        if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        audioManager.abandonAudioFocusRequest(focusRequest)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}