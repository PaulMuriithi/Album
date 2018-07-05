package com.album.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.album.R

class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val txtAlbumList by lazy { view.findViewById<TextView>(R.id.album_list_item) }
}