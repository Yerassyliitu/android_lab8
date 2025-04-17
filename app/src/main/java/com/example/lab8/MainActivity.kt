package com.example.lab8
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private lateinit var randomCharacterEditText: EditText
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var bgServiceIntent: Intent
    private lateinit var fgServiceIntent: Intent
    private val NOTIFICATION_PERMISSION_CODE = 123
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        randomCharacterEditText = findViewById(R.id.editText_randomCharacter)
        val startBgButton: Button = findViewById(R.id.button_start_bg)
        val stopBgButton: Button = findViewById(R.id.button_stop_bg)

        val startFgButton: Button = findViewById(R.id.button_start_fg)
        val stopFgButton: Button = findViewById(R.id.button_stop_fg)

        broadcastReceiver = MyBroadcastReceiver()

        bgServiceIntent = Intent(this, RandomCharacterService::class.java)
        fgServiceIntent = Intent(applicationContext, MusicService::class.java)

        startBgButton.setOnClickListener {
            startService(bgServiceIntent)
        }

        stopBgButton.setOnClickListener {
            stopService(bgServiceIntent)
            randomCharacterEditText.setText("")
        }

        startFgButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.startForegroundService(this, fgServiceIntent)
                } else {
                    Toast.makeText(this, "Требуется разрешение на уведомления", Toast.LENGTH_SHORT).show()
                    requestNotificationPermission()
                }
            } else {
                ContextCompat.startForegroundService(this, fgServiceIntent)
            }
        }

        stopFgButton.setOnClickListener {
            stopService(fgServiceIntent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        Log.d(TAG, "Запрос разрешения на уведомления")
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_CODE
            )
        } else {
            Log.d(TAG, "Разрешение на уведомления уже предоставлено")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Разрешение на уведомления получено")
            } else {
                Toast.makeText(this, "Без разрешения на уведомления музыкальный сервис не сможет работать", Toast.LENGTH_LONG).show()
            }
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