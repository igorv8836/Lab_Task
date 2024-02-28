package com.example.lab_task

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.lab_task.databinding.ActivityMainBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(com.example.lab_task.BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.i("info", com.example.lab_task.BuildConfig.MAPKIT_API_KEY)

        binding.mapview.map.move(
            CameraPosition(
                Point(55.751225, 37.629540),
                /* zoom = */ 17.0f,
                /* azimuth = */ 150.0f,
                /* tilt = */ 30.0f
            )
        )
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}