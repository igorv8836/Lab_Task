package com.example.lab_task.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lab_task.BuildConfig
import com.example.lab_task.R
import com.example.lab_task.databinding.ActivityMainBinding
import com.example.lab_task.databinding.FragmentMapBinding
import com.example.lab_task.view.fragments.ListFragment
import com.example.lab_task.view.fragments.MapFragment
import com.example.lab_task.view.fragments.SettingsFragment
import com.example.lab_task.viewmodel.MainActivityViewModel
import com.google.android.material.navigation.NavigationBarView
import com.yandex.mapkit.MapKitFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var mapFragment: MapFragment
    lateinit var settingsFragment: SettingsFragment
    lateinit var listFragment: ListFragment
    private lateinit var viewModel: MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapFragment = MapFragment()
        settingsFragment = SettingsFragment()
        listFragment = ListFragment()

        supportFragmentManager.beginTransaction().replace(R.id.nav_host, mapFragment).commit()

        binding.bottomNavigationView.setOnItemSelectedListener( object: NavigationBarView.OnItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId){
                    R.id.menu_maps -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.nav_host, mapFragment).commit()
                        return true
                    }

                    R.id.menu_list -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.nav_host, listFragment).commit()
                        return true
                    }

                    R.id.menu_settings -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.nav_host, settingsFragment).commit()
                        return true
                    }
                }
                return false
            }
        }
        )

        checkPermission()

    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 33){
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 0)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}