package com.example.lab8
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    private lateinit var randomCharacterEditText: EditText
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var bgServiceIntent: Intent
    private lateinit var fgServiceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация компонентов для Background Service
        randomCharacterEditText = findViewById(R.id.editText_randomCharacter)
        val startBgButton: Button = findViewById(R.id.button_start_bg)
        val stopBgButton: Button = findViewById(R.id.button_stop_bg)

        // Инициализация компонентов для Foreground Service
        val startFgButton: Button = findViewById(R.id.button_start_fg)
        val stopFgButton: Button = findViewById(R.id.button_stop_fg)

        // Создание Broadcast Receiver для Background Service
        broadcastReceiver = MyBroadcastReceiver()

        // Инициализация Intent для обоих сервисов
        bgServiceIntent = Intent(this, RandomCharacterService::class.java)
        fgServiceIntent = Intent(applicationContext, MusicService::class.java)

        // Настройка обработчиков кнопок для Background Service
        startBgButton.setOnClickListener {
            startService(bgServiceIntent)
        }

        stopBgButton.setOnClickListener {
            stopService(bgServiceIntent)
            randomCharacterEditText.setText("")
        }

        // Настройка обработчиков кнопок для Foreground Service
        startFgButton.setOnClickListener {
            ContextCompat.startForegroundService(this, fgServiceIntent)
        }

        stopFgButton.setOnClickListener {
            stopService(fgServiceIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter()
        intentFilter.addAction("my.custom.action.tag.lab6")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, intentFilter, RECEIVER_NOT_EXPORTED)
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                val data = intent.getCharExtra("randomCharacter", '?')
                runOnUiThread {
                    randomCharacterEditText.setText(data.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}