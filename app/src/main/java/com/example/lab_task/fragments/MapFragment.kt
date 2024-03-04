package com.example.lab_task.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.lab_task.R
import com.example.lab_task.databinding.FragmentMapBinding
import com.example.lab_task.viewmodels.MapViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
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
import java.io.FileOutputStream
import kotlin.math.min

class MapFragment : Fragment() {

    companion object { fun newInstance() = MapFragment() }

    private val READ_MEDIA_IMAGES_PERMISSION = 1
    private val GALLERY_START_CODE = 2
    private lateinit var viewModel: MapViewModel
    private lateinit var binding: FragmentMapBinding
    private var userPlacemark: PlacemarkMapObject? = null
    private lateinit var sharedPref: SharedPreferences
    private var photo: File? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        binding.mapview.onStart()
        viewModel.getTags()


        binding.incudeAddTag.closeButton.bringToFront()

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
            val customLayout = LayoutInflater.from(
                requireContext()).inflate(R.layout.new_tag_dialog, null)
            val editText = customLayout.findViewById<EditText>(R.id.editTextDescription_inputText)
            val addImageButton  = customLayout.findViewById<ImageView>(R.id.add_photo_button)

            addImageButton.setOnClickListener {
                pickPhoto()

                viewModel.photoForNewTag.observe(viewLifecycleOwner){
                    val size = resources.getDimensionPixelSize(R.dimen.image_size)
                    Picasso.get().load(it!!).resize(size, size).centerCrop()
                        .into(addImageButton)
                    }
            }

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
                    clickedTag.latitude.toString().substring(
                        0, min(clickedTag.latitude.toString().length, 10)) + ", " +
                            clickedTag.longitude.toString().substring(
                                0, min(clickedTag.longitude.toString().length, 10))
                author.text = (("Автор: " + (clickedTag.user?.username ?: "-")))
                likeCount.text = clickedTag.likes.toString()
                if (clickedTag.isLiked)
                    like.setImageResource(R.drawable.red_heart)
                else
                    like.setImageResource(R.drawable.heart)
                if (clickedTag.image != null) {
                    Picasso.get().load(viewModel.getPhoto(clickedTag.image))
                        .resize(
                            resources.getDimensionPixelSize(R.dimen.image_size),
                            resources.getDimensionPixelSize(R.dimen.image_size)
                        ).centerCrop().into(image)
                } else {
                    image.setImageBitmap(null)
                }

                binding.tagInfoFrame.visibility = View.VISIBLE

                like.setOnClickListener {
                    viewModel.changeLike(clickedTag.id)
                }
            }
        true
    }

    fun pickPhoto(){
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_MEDIA_IMAGES
        ) != PackageManager.PERMISSION_GRANTED) { // izin alınmadıysa
            requestPermissions(arrayOf(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ), READ_MEDIA_IMAGES_PERMISSION)
        } else {
            startActivityForResult(
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                GALLERY_START_CODE
            )
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_MEDIA_IMAGES_PERMISSION) {
            if (grantResults.size > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,GALLERY_START_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_START_CODE && resultCode == Activity.RESULT_OK && data != null) {
            var pickedPhoto : Uri? = data.data
            var pickedBitMap : Bitmap? = null

            if (pickedPhoto != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(requireContext().contentResolver, pickedPhoto)
                    pickedBitMap = ImageDecoder.decodeBitmap(source)
                } else {
                    pickedBitMap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver,pickedPhoto)
                }
                photo = createTempFile()
                FileOutputStream(photo).use {
                    pickedBitMap!!.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                viewModel.setImage(photo)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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