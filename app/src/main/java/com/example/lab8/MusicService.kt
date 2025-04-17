package com.example.lab8

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import android.content.pm.ServiceInfo

class MusicService : Service() {
    private lateinit var soundPlayer: MediaPlayer
    private val CHANNEL_ID = "music_channel_id"

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this, "Музыкальный сервис создан", Toast.LENGTH_SHORT).show()

        // Инициализация плеера
        soundPlayer = MediaPlayer.create(this, R.raw.song)
        soundPlayer.isLooping = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Музыкальный сервис запущен", Toast.LENGTH_SHORT).show()

        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music)
            .setContentTitle("Мой музыкальный плеер")
            .setContentText("Музыка проигрывается")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(1, notification)
        }

        soundPlayer.start()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Музыкальный сервис остановлен", Toast.LENGTH_SHORT).show()
        if (soundPlayer.isPlaying) {
            soundPlayer.stop()
        }
        soundPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Музыкальный канал"
            val descriptionText = "Уведомления музыкального сервиса"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
