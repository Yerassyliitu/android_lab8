package com.example.lab8

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.widget.EditText


class MainActivity : AppCompatActivity() {
    private lateinit var randomCharacterEditText: EditText
    private lateinit var serviceIntent: Intent
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        randomCharacterEditText = findViewById(R.id.editText_randomCharacter)
        serviceIntent = Intent(this, RandomCharacterService::class.java)

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val char = intent?.getCharExtra("randomCharacter", '?')
                randomCharacterEditText.setText(char.toString())
            }
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.button_start -> startService(serviceIntent)
            R.id.button_end -> {
                stopService(serviceIntent)
                randomCharacterEditText.setText("")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter("my.custom.action.tag.lab6")
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }
}
