package com.album.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.album.R
import com.album.holders.PhotoViewHolder
import com.album.interfaces.PhotoClickListener
import com.album.models.Photo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class PhotoAdapter(private val context: Context,
                   private val photoList: List<Photo>,
                   private val photoClickListener: PhotoClickListener) : RecyclerView.Adapter<PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photos_list_item, parent, false)
        return PhotoViewHolder(view)
    }

    override fun getItemCount(): Int = photoList.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.progressBar.visibility = View.VISIBLE
        val photo = photoList[position]
        holder.title.text = photo.title
        holder.itemView.setOnClickListener { photoClickListener.onPhotoItemClickListener(photo.url) }
        Glide.with(context).load(photo.thumbnailUrl).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                holder.progressBar.visibility = View.GONE
                return false
            }
        }).into(holder.thumbnail)
    }
}