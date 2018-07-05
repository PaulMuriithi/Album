package com.album.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.album.R

class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title by lazy { view.findViewById<TextView>(R.id.photos_list_title) }
    val thumbnail by lazy { view.findViewById<ImageView>(R.id.photos_list_thumbnail) }
    val progressBar by lazy { view.findViewById<ProgressBar>(R.id.photos_list_progressBar) }
}