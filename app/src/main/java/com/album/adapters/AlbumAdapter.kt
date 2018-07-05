package com.album.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.album.R
import com.album.holders.AlbumViewHolder
import com.album.interfaces.AlbumClickListener
import com.album.models.Album

class AlbumAdapter(private var albumList: List<Album>,
                   private val albumClickListener: AlbumClickListener) : RecyclerView.Adapter<AlbumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_list_item, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albumList[position]
        holder.txtAlbumList.text = album.title
        holder.itemView.setOnClickListener { albumClickListener.onAlbumItemClickListener(album.id) }
    }

    override fun getItemCount(): Int = albumList.size

}