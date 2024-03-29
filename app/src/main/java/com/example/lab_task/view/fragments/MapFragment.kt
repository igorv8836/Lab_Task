package com.example.lab_task.view.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import com.example.lab_task.R
import com.example.lab_task.databinding.FragmentMapBinding
import com.example.lab_task.databinding.NewTagDialogBinding
import com.example.lab_task.viewmodel.MapViewModel
import com.squareup.picasso.Picasso
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.ClusterListener
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.example.lab_task.view.ClusterView
import com.example.lab_task.model.other.MapPosition
import com.example.lab_task.model.other.PlacemarkType
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection
import com.yandex.mapkit.map.IconStyle
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import java.io.FileOutputStream

class MapFragment : Fragment() {
    private val READ_MEDIA_IMAGES_PERMISSION = 1
    private val GALLERY_START_CODE = 2

    private lateinit var viewModel: MapViewModel
    private lateinit var binding: FragmentMapBinding
    private var userPlacemark: PlacemarkMapObject? = null
    private lateinit var clusterizedCollection: ClusterizedPlacemarkCollection

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        with(binding) {
            mapview.map.addInputListener(inputListener)

            addTagButtonLayout.closeButton.setOnClickListener {
                userPlacemark?.isVisible = false
                binding.newTagButtonFrame.visibility = View.GONE
            }

            addTagButtonLayout.addTagButton.setOnClickListener {
                createNewTagCustomDialog()
            }
        }

        clusterizedCollection =
            binding.mapview.map.mapObjects.addClusterizedPlacemarkCollection(clusterListener)

        with(viewModel) {
            getStartingPos()
            getTags()
            tags.observe(viewLifecycleOwner) {
                clusterizedCollection.clear()
                for (i in it) {
                    clusterizedCollection.addPlacemark().apply {
                        geometry = Point(i.latitude, i.longitude)
                        userData = i.id
                        setIcon(
                            ImageProvider.fromResource(requireContext(), R.drawable.placemark),
                            IconStyle().setAnchor(PointF(0.5f, 1f))
                        )
                        addTapListener(placemarkTapListener)
                    }
                }
                clusterizedCollection.clusterPlacemarks(60.0, 15)
            }

            startingPos.observe(viewLifecycleOwner) {
                binding.mapview.map.move(
                    CameraPosition(
                        Point(it.latitude, it.longitude),
                        /* zoom = */ it.zoom.toFloat(),
                        /* azimuth = */ it.azimuth.toFloat(),
                        /* tilt = */ it.tilt.toFloat()
                    )
                )
            }

            helpingText.observe(viewLifecycleOwner) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNewTagCustomDialog() {
        userPlacemark?.isVisible = false
        binding.newTagButtonFrame.visibility = View.GONE
        val bindingDialog = NewTagDialogBinding.inflate(LayoutInflater.from(requireContext()))
        bindingDialog.addPhotoButton.setOnClickListener { pickPhoto() }
        viewModel.photoForNewTag.observe(viewLifecycleOwner) {
            val size = resources.getDimensionPixelSize(R.dimen.image_size)
            Picasso.get().load(it!!).resize(size, size).centerCrop()
                .into(bindingDialog.addPhotoButton)
        }

        AlertDialog.Builder(requireContext())
            .setView(bindingDialog.root)
            .setTitle("Новая метка")
            .setPositiveButton("Добавить") { _, _ ->
                val drawable = bindingDialog.addPhotoButton.drawable
                val bitmap = (drawable as? BitmapDrawable)?.toBitmapOrNull()
                viewModel.addTag(
                    userPlacemark!!.geometry.latitude,
                    userPlacemark!!.geometry.longitude,
                    bindingDialog.editTextDescriptionInputText.text.toString()
                )
            }.setNegativeButton("Отменить") { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            if (userPlacemark == null || userPlacemark?.isValid == false) {
                userPlacemark = map.mapObjects.addPlacemark().apply {
                    geometry = point
                    setIcon(
                        ImageProvider.fromResource(
                            requireContext(),
                            R.drawable.white_placemark
                        ),
                        IconStyle().setAnchor(PointF(0.5f, 1f))
                    )
                }
                Log.i("ll1", userPlacemark?.isValid.toString())
            } else {
                Log.i("ll", userPlacemark?.isValid.toString())
                userPlacemark?.geometry = point
            }
            userPlacemark?.isVisible = true

            binding.newTagButtonFrame.visibility = View.VISIBLE
        }
        override fun onMapLongTap(map: Map, point: Point) {}
    }
    private val clusterListener = ClusterListener { cluster ->
        cluster.appearance.setView(
            ViewProvider(
                ClusterView(requireContext()).apply {
                    setData(cluster.placemarks.map { PlacemarkType.RED })
                }
            )
        )
    }


    private val placemarkTapListener = MapObjectTapListener { obj, t ->
        binding.newTagButtonFrame.visibility = View.GONE
        val bundle = Bundle()
        bundle.putString("tag_id", obj.userData.toString())
        val fragment = TagInfoFragment()
        fragment.arguments = bundle
        fragment.show(requireFragmentManager(), "tag_info_fragment")
        if (userPlacemark?.isValid == true)
            userPlacemark?.isVisible = false
        true
    }

    private fun pickPhoto() {
        val permission = when {
            Build.VERSION.SDK_INT >= 33 -> android.Manifest.permission.READ_MEDIA_IMAGES
            else -> android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(permission), READ_MEDIA_IMAGES_PERMISSION)
        } else {
            startActivityForResult(
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                GALLERY_START_CODE
            )
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_MEDIA_IMAGES_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, GALLERY_START_CODE)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_START_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val pickedPhoto: Uri? = data.data
            val pickedBitMap: Bitmap?

            if (pickedPhoto != null) {
                pickedBitMap = if (Build.VERSION.SDK_INT >= 33) {
                    val source =
                        ImageDecoder.createSource(requireContext().contentResolver, pickedPhoto)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, pickedPhoto)
                }
                val photo = createTempFile()
                FileOutputStream(photo).use {
                    pickedBitMap!!.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                viewModel.setSelectedImage(photo)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        binding.mapview.onStart()
    }

    override fun onStop() {
        binding.mapview.onStop()
        super.onStop()
        viewModel.setStartingPos(
            binding.mapview.map.cameraPosition.run {
                MapPosition(
                    target.latitude,
                    target.longitude,
                    zoom.toDouble(),
                    azimuth.toDouble(),
                    tilt.toDouble()
                )
            }
        )
    }
}