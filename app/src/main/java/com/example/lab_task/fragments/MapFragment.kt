package com.example.lab_task.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lab_task.R
import com.example.lab_task.databinding.FragmentMapBinding
import com.example.lab_task.viewmodels.MapViewModel
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.runtime.image.ImageProvider

class MapFragment : Fragment() {

    companion object {
        fun newInstance() = MapFragment()
    }

    private lateinit var viewModel: MapViewModel
    private lateinit var binding: FragmentMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]

        binding.mapview.onStart()

        viewModel.getTags()
        viewModel.tags.observe(viewLifecycleOwner){
            val imageProvider = ImageProvider.fromResource(requireContext(), R.drawable.placemark_icon)
            for(i in it.indices) {
                val placemark = binding.mapview.map.mapObjects.addPlacemark().apply {
                    geometry = Point(it[i].latitude, it[i].longitude)
                    setIcon(imageProvider)
                }
                placemark.addTapListener(placemarkTapListener)
            }
        }

        binding.mapview.map.move(
            CameraPosition(
                Point(55.669986, 37.480409),
                /* zoom = */ 17.0f,
                /* azimuth = */ 0.0f,
                /* tilt = */ 0.0f
            )
        )
    }

    private val placemarkTapListener = MapObjectTapListener { _, point ->
        Snackbar.make(
            binding.root,
            "Tapped the point (${point.longitude}, ${point.latitude})",
            Snackbar.LENGTH_SHORT
        ).show()
        true
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