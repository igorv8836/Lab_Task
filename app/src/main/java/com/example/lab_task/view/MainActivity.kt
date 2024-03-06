package com.example.lab_task.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.fragment.findNavController
import com.example.lab_task.BuildConfig
import com.example.lab_task.R
import com.example.lab_task.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import com.yandex.mapkit.MapKitFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host)
        val navController = navHostFragment?.findNavController()
        binding.bottomNavigationView.setOnItemSelectedListener( object: NavigationBarView.OnItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId){
                    R.id.menu_maps -> {
                        navController?.navigate(R.id.action_to_map_fragment)
                        return true

                    }

                    R.id.menu_settings -> {
                        navController?.navigate(R.id.action_to_settings_fragment)
                        return true
                    }
                }
                return false
            }
        }
        )

    }
}