package com.example.lab_task.adapters

import android.annotation.SuppressLint
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
        fun onSubscribeClick(userId: String, isSubscribed: Boolean)
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
        holder.deleteButton.visibility = View.GONE
        if (data[position].isLiked)
            holder.likeButton.setImageResource(R.drawable.red_heart)
        else
            holder.likeButton.setImageResource(R.drawable.heart)

        if (data[position].imagePath == null)
            holder.photo.setImageResource(R.drawable.no_image)
        else
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
        holder.subscribeButton.setOnClickListener {
            data[position].userId?.let {
                listener.onSubscribeClick(it, (data[position] as SubscribedTag).isSubscribed)
            }
        }

        if (currUsername != null && currUsername == data[position].username)
            holder.deleteButton.visibility = View.VISIBLE

        holder.subscribeButton.visibility = if (data[position].userId != null)
            View.VISIBLE
        else
            View.GONE
        holder.subscribeButton.text = if ((data[position] as SubscribedTag).isSubscribed)
            "Отписаться"
        else
            "Подписаться"

        holder.mainLayout.setOnClickListener{
            if (holder.additionalLayout.visibility == View.VISIBLE)
                holder.additionalLayout.visibility = View.GONE
            else
                holder.additionalLayout.visibility = View.VISIBLE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTags(updatedTags: List<TagEntity>) {
        if (updatedTags.size == data.size){
            for (i in 0 until data.size){
                val a = (data[i] as SubscribedTag).isSubscribed != (updatedTags[i] as SubscribedTag).isSubscribed
                if (data[i] != updatedTags[i] || a){
                    data[i] = updatedTags[i]
                    notifyItemChanged(i)
                }
            }
        } else {
            data = ArrayList(updatedTags)
            notifyDataSetChanged()
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