package com.example.downloadbound

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.downloadbound.BoundService.LocalBinder


class MainActivity : AppCompatActivity() {

    var myService: BoundService? = null
    var isBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= M) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 1
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i("MainActivity", "onStart called")
        val intent = Intent(this, BoundService::class.java)
        //start service with binding
        bindService(intent, MyConnection, Context.BIND_AUTO_CREATE)
    }

    private val MyConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocalBinder
            myService = binder.service
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }
    fun downloadFile() {
// Download file
        myService!!.downloadFile("https://static.pexels.com/photos/4825/red-love-romantic-flowers.jpg", )
       val progress: TextView = findViewById(R.id.progress)
       progress.text = downloadFile().toString()

    }
    override fun onStop() {
        super.onStop()
        Log.i("MainActivity", "onStop called")
        if (isBound) {
            //unbind service
            unbindService(MyConnection)
            isBound = false
        }
    }
}