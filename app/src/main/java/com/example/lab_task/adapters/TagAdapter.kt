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

class TagAdapter(var data: ArrayList<TagEntity>, private val listener: OnAdapterActionListener,  var currUsername: String? = null): RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    interface OnAdapterActionListener{
        fun onLikeClick(tagId: String, isLiked: Boolean)
        fun onDeleteClick(tadId: String)
        fun onSubscribeClick(tagId: String)
    }

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
        val mainLayout = binding.mainLayout
        val additionalLayout = binding.additionalInfo
    }

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

        holder.likeButton.setOnClickListener {
            listener.onLikeClick(data[position].id, data[position].isLiked)
        }

        holder.deleteButton.setOnClickListener {
            listener.onDeleteClick(data[position].id)
        }

        if (currUsername != null && currUsername == data[position].username)
            holder.deleteButton.visibility = View.VISIBLE

        holder.mainLayout.setOnClickListener{
            if (holder.additionalLayout.visibility == View.VISIBLE)
                holder.additionalLayout.visibility = View.GONE
            else
                holder.additionalLayout.visibility = View.VISIBLE
        }
    }

    fun updateTags(updatedTags: List<TagEntity>) {
        val iterator = data.iterator()

        while (iterator.hasNext()) {
            val existingTag = iterator.next()
            val pos = updatedTags.indexOfFirst { it.id == existingTag.id }

            if (pos == -1) {
                val indexToRemove = data.indexOf(existingTag)
                iterator.remove()
                notifyItemRemoved(indexToRemove)
            } else {
                if (existingTag != updatedTags[pos]) {
                    data[data.indexOf(existingTag)] = updatedTags[pos]
                    notifyItemChanged(data.indexOf(updatedTags[pos]))
                }
            }
        }

        for (updatedTag in updatedTags) {
            val pos = data.indexOfFirst { it.id == updatedTag.id }
            if (pos == -1) {
                data.add(updatedTag)
                notifyItemInserted(data.size)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TagViewHolder(
            RecyclerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root)

    override fun getItemCount() = data.size
}