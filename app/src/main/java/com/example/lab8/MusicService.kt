package com.example.lab8

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MusicService : Service() {
    private lateinit var soundPlayer: MediaPlayer
    private val CHANNEL_ID = "music_channel_id"
    private val TAG = "MusicService"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: Музыкальный сервис создается")
        Toast.makeText(this, "Музыкальный сервис создан", Toast.LENGTH_SHORT).show()

        try {
            // Инициализация плеера
            soundPlayer = MediaPlayer.create(this, R.raw.song)
            if (soundPlayer == null) {
                Log.e(TAG, "onCreate: MediaPlayer не удалось создать")
            } else {
                Log.d(TAG, "onCreate: MediaPlayer успешно создан")
                soundPlayer.isLooping = false

                // Добавляем обработчики ошибок
                soundPlayer.setOnErrorListener { mp, what, extra ->
                    Log.e(TAG, "MediaPlayer ошибка: код=$what, extra=$extra")
                    true
                }

                soundPlayer.setOnPreparedListener {
                    Log.d(TAG, "MediaPlayer готов к воспроизведению")
                }

                soundPlayer.setOnCompletionListener {
                    Log.d(TAG, "MediaPlayer завершил воспроизведение")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при инициализации MediaPlayer", e)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: запуск музыкального сервиса")
        Toast.makeText(this, "Музыкальный сервис запущен", Toast.LENGTH_SHORT).show()

        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music)
            .setContentTitle("Мой музыкальный плеер")
            .setContentText("Музыка проигрывается")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        Log.d(TAG, "onStartCommand: запуск foreground сервиса с уведомлением")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(1, notification)
        }

        try {
            if (::soundPlayer.isInitialized && !soundPlayer.isPlaying) {
                Log.d(TAG, "onStartCommand: запуск воспроизведения")
                soundPlayer.start()
                Log.d(TAG, "onStartCommand: воспроизведение начато успешно")
            } else {
                Log.e(TAG, "onStartCommand: MediaPlayer не инициализирован или уже воспроизводится")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при запуске воспроизведения", e)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: остановка сервиса")
        super.onDestroy()
        Toast.makeText(this, "Музыкальный сервис остановлен", Toast.LENGTH_SHORT).show()
        try {
            if (::soundPlayer.isInitialized) {
                if (soundPlayer.isPlaying) {
                    Log.d(TAG, "onDestroy: остановка воспроизведения")
                    soundPlayer.stop()
                }
                Log.d(TAG, "onDestroy: освобождение ресурсов MediaPlayer")
                soundPlayer.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при остановке MediaPlayer", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind вызван")
        return null
    }

    private fun createNotificationChannel() {
        Log.d(TAG, "createNotificationChannel: создание канала уведомлений")
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
            Log.d(TAG, "createNotificationChannel: канал уведомлений создан")
        }
    }
}