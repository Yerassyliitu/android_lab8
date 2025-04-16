package com.example.lab8

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import java.util.Random
import kotlin.concurrent.thread

class RandomCharacterService : Service() {
    private var isRandomGeneratorOn = false
    private val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
    private val TAG = "RandomCharacterService"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(applicationContext, "Фоновый сервис запущен", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "Сервис запущен...")
        Log.i(TAG, "В OnStartCommand ID потока: ${Thread.currentThread().id}")

        isRandomGeneratorOn = true

        thread {
            startRandomGenerator()
        }

        return START_STICKY
    }

    private fun startRandomGenerator() {
        while (isRandomGeneratorOn) {
            try {
                Thread.sleep(1000)
                if (isRandomGeneratorOn) {
                    val MIN = 0
                    val MAX = 25
                    val randomIdx = Random().nextInt(MAX - MIN + 1) + MIN
                    val myRandomCharacter = alphabet[randomIdx]

                    Log.i(TAG, "ID потока: ${Thread.currentThread().id}, Случайный символ: $myRandomCharacter")

                    val broadcastIntent = Intent().apply {
                        action = "my.custom.action.tag.lab6"
                        putExtra("randomCharacter", myRandomCharacter)
                    }

                    sendBroadcast(broadcastIntent)
                }
            } catch (e: InterruptedException) {
                Log.i(TAG, "Поток прерван.")
            }
        }
    }

    private fun stopRandomGenerator() {
        isRandomGeneratorOn = false
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRandomGenerator()
        Toast.makeText(applicationContext, "Фоновый сервис остановлен", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "Сервис уничтожен...")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}