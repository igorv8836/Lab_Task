package com.example.lab_task.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.lab_task.R
import com.example.lab_task.databinding.RecyclerItemBinding
import com.example.lab_task.model.sqlite.TagEntity
import com.squareup.picasso.Picasso

class TagAdapter(private var data: List<TagEntity>): RecyclerView.Adapter<TagAdapter.TagViewHolder>() {


    class TagViewHolder(itemView: View): ViewHolder(itemView) {
        val binding = RecyclerItemBinding.bind(itemView)
        val likeButton = binding.like
        val deleteButton = binding.deleteButton
        val subscribeButton = binding.subscribeButton
        val coordText = binding.coordinates
        val description = binding.description
        val countLike = binding.likeCount
        val photo = binding.image
        val author = binding.author
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TagViewHolder(
            RecyclerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root)

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.author.text = data[position].username
        holder.description.text = data[position].description
        holder.coordText.text = data[position].getFormatCoord()
        holder.countLike.text = data[position].likes.toString()
        if (data[position].isLiked)
            holder.likeButton.setImageResource(R.drawable.red_heart)
        else
            holder.likeButton.setImageResource(R.drawable.heart)


        Picasso.get().load(data[position].imagePath).resize(
            holder.itemView.resources.getDimensionPixelSize(R.dimen.image_size),
            holder.itemView.resources.getDimensionPixelSize(R.dimen.image_size)
        ).into(holder.photo)



    }

    override fun getItemCount() = data.size
}