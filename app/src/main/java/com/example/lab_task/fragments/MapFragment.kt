package com.example.lab_task.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.lab_task.R
import com.example.lab_task.databinding.FragmentMapBinding
import com.example.lab_task.viewmodels.MapViewModel
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.ClusterListener
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkitdemo.objects.ClusterView
import com.yandex.mapkitdemo.objects.PlacemarkType
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import java.io.File

class MapFragment : Fragment() {

    companion object { fun newInstance() = MapFragment() }

    private lateinit var viewModel: MapViewModel
    private lateinit var binding: FragmentMapBinding
    private var userPlacemark: PlacemarkMapObject? = null
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        binding.mapview.onStart()
        viewModel.getTags()

        sharedPref = requireActivity().getSharedPreferences("tokens", Context.MODE_PRIVATE)
        val token = sharedPref.getString(getString(R.string.tag_api_token), null)
        var username: String? = null
        if (token != null) {
            username = sharedPref.getString(getString(R.string.username), null)
            viewModel.username = username
            viewModel.addToken(token)
        }

        binding.mapview.map.move(
            CameraPosition(
                Point(55.666123, 37.478622),
                /* zoom = */ 17.0f,
                /* azimuth = */ 0.0f,
                /* tilt = */ 0.0f
            )
        )

        val clusterizedCollection =
            binding.mapview.map.mapObjects.addClusterizedPlacemarkCollection(clusterListener)


        viewModel.tags.observe(viewLifecycleOwner){
            clusterizedCollection.clear()
            val imageProvider = ImageProvider.fromResource(requireContext(), R.drawable.placemark)
            for(i in it.indices) {
                val placemark = clusterizedCollection.addPlacemark().apply {
                    geometry = Point(it[i].latitude, it[i].longitude)
                    setText(it[i].description)
                    setIcon(imageProvider)
                }
                placemark.addTapListener(placemarkTapListener)
            }
            clusterizedCollection.clusterPlacemarks(60.0, 15)
        }

        viewModel.helpingText.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        }
        viewModel.changedLikeOfTag.observe(viewLifecycleOwner){
            binding.incude.likeCount.text = it.likes.toString()
            if (it.isLiked)
                binding.incude.like.setImageResource(R.drawable.red_heart)
            else
                binding.incude.like.setImageResource(R.drawable.heart)
        }

        binding.incude.closeButton.setOnClickListener {
            binding.tagInfoFrame.visibility = View.GONE
        }
        binding.incudeAddTag.closeButton.setOnClickListener {
            userPlacemark?.isVisible = false
            binding.newTagButtonFrame.visibility = View.GONE
        }
        binding.mapview.map.addInputListener(inputListener)

        binding.incudeAddTag.addTagButton.setOnClickListener{
            val customLayout = LayoutInflater.from(requireContext()).inflate(R.layout.new_tag_dialog, null)
            val editText = customLayout.findViewById<EditText>(R.id.editTextDescription_inputText)
            val builder = AlertDialog.Builder(requireContext())
                .setView(customLayout)
                .setTitle("Новая метка")
                .setPositiveButton("Добавить") { dialog, which ->
                    if (userPlacemark != null) {
                        viewModel.addTag(
                            userPlacemark!!.geometry.latitude,
                            userPlacemark!!.geometry.longitude,
                            editText.text.toString(),
                            null
                        )
                    }
                }.setNegativeButton("Отменить") {dialog, which ->
                    dialog.dismiss()
                }
            builder.create().show()
        }

        viewModel.auth("igorv88361", "123123")

    }

    val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            val image = ImageProvider.fromResource(requireContext(), R.drawable.white_placemark)
            if (userPlacemark == null) {
                userPlacemark = map.mapObjects.addPlacemark().apply {
                    geometry = point
                    setIcon(image)
                }
            } else
                userPlacemark?.geometry = point
            userPlacemark?.isVisible = true

            binding.tagInfoFrame.visibility = View.GONE
            binding.newTagButtonFrame.visibility = View.VISIBLE

        }

        override fun onMapLongTap(map: Map, point: Point) {
        }
    }

    val clusterListener = ClusterListener { cluster ->
        val placemarkTypes = cluster.placemarks.map {
            PlacemarkType.RED
        }
        cluster.appearance.setView(
            ViewProvider(
                ClusterView(requireContext()).apply {
                    setData(placemarkTypes)
                })
        )
    }

    @SuppressLint("SetTextI18n")
    private val placemarkTapListener = MapObjectTapListener { obj, point ->
        binding.newTagButtonFrame.visibility = View.GONE
        binding.tagInfoFrame.visibility = View.VISIBLE
        userPlacemark?.isVisible = false
        val clickedTag = viewModel.findTagByCoord(
            (obj as PlacemarkMapObject).geometry.latitude,
            obj.geometry.longitude
        )
        if (clickedTag != null)
            binding.incude.apply {
                description.text = clickedTag.description
                coordinates.text =
                    (clickedTag.latitude.toString()) + ", " +
                            (clickedTag.longitude.toString())
                author.text = (("Автор: " + (clickedTag.user?.username ?: "-")))
                likeCount.text = clickedTag.likes.toString()
                if (clickedTag.isLiked)
                    like.setImageResource(R.drawable.red_heart)
                else
                    like.setImageResource(R.drawable.heart)
                if (clickedTag.image != null)
                    image.setImageBitmap(BitmapFactory.decodeFile(File(clickedTag.image).absolutePath))

                binding.tagInfoFrame.visibility = View.VISIBLE

                binding.incude.like.setOnClickListener {
                    viewModel.changeLike(clickedTag.id)
                }
            }
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