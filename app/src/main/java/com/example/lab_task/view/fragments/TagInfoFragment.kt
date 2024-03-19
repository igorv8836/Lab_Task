package com.example.lab_task.view.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.lab_task.R
import com.example.lab_task.databinding.FragmentTagInfoBinding
import com.example.lab_task.viewmodel.TagInfoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso


class TagInfoFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentTagInfoBinding
    lateinit var viewModel: TagInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[TagInfoViewModel::class.java]
        binding = FragmentTagInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val id = it.getString("tag_id")
            id?.let { it1 -> viewModel.getTag(it1) }
        }

        with(viewModel) {
            tag.observe(viewLifecycleOwner) {
                checkSubscription()
                showDeleteButton()
                it?.imagePath?.let {it1 ->
                    if (it1 == null)
                        binding.image.setImageResource(R.drawable.no_image)
                    Picasso.get().load(it1).resize(
                        resources.getDimensionPixelSize(R.dimen.image_size),
                        resources.getDimensionPixelSize(R.dimen.image_size)
                    ).centerCrop().into(binding.image)
                }
                with(binding) {
                    description.text = it?.description
                    coordinates.text = it?.getFormatCoord()
                    author.text = (("Автор: " + (it?.username ?: "-")))
                    likeCount.text = it?.likes.toString()
                    if (it?.isLiked == true)
                        like.setImageResource(R.drawable.red_heart)
                    else
                        like.setImageResource(R.drawable.heart)
                }
            }

            showDeleteButton.observe(viewLifecycleOwner) {
                binding.deleteButton.visibility = if (it) View.VISIBLE else View.GONE
            }

            isSubscribed.observe(viewLifecycleOwner) {
                binding.subscribeButton.visibility = View.VISIBLE
                if (it) {
                    binding.subscribeButton.text = "Отписаться"
                } else
                    binding.subscribeButton.text = "Подписаться"
            }

            helpingText.observe(viewLifecycleOwner){
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        with(binding) {
            like.setOnClickListener { viewModel.changeLike() }
            deleteButton.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Удаление метки")
                    .setMessage("Вы действительно хотите удалить метку?")
                    .setPositiveButton("Удалить") { _, _ ->
                        viewModel.delete()
                        dismiss()
                    }.setNegativeButton("Отменить") { dialog, _ ->
                        dialog.dismiss()
                    }.create().show()
            }
            subscribeButton.setOnClickListener { viewModel.subscribeButton() }
        }
    }
}