package com.example.lab8

import android.content.pm.ServiceInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MusicService : Service() {
    private lateinit var soundPlayer: MediaPlayer
    private val CHANNEL_ID = "channelId"
    private lateinit var notifManager: NotificationManager

    override fun onCreate() {
        Toast.makeText(this, "Музыкальный сервис создан", Toast.LENGTH_SHORT).show()
        soundPlayer = MediaPlayer.create(this, R.raw.song)
        soundPlayer.isLooping = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Музыкальный сервис запущен", Toast.LENGTH_SHORT).show()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val offerChannelName = "Канал сервиса"
            val offerChannelDescription = "Канал для музыки"
            val offerChannelImportance = NotificationManager.IMPORTANCE_DEFAULT

            val notifChannel = NotificationChannel(
                CHANNEL_ID,
                offerChannelName,
                offerChannelImportance
            ).apply {
                description = offerChannelDescription
            }

            notifManager = getSystemService(NotificationManager::class.java)
            notifManager.createNotificationChannel(notifChannel)
        }

        val sNotifBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music)
            .setContentTitle("Мой музыкальный плеер")
            .setContentText("Музыка проигрывается")

        // ... existing code ...
        val servNotification = sNotifBuilder.build()
        startForeground(1, servNotification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)

        soundPlayer.start()
        return START_STICKY
    }

    override fun onDestroy() {
        Toast.makeText(this, "Музыкальный сервис остановлен", Toast.LENGTH_SHORT).show()
        soundPlayer.stop()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}