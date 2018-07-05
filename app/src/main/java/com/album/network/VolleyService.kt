package com.album.network

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleyService constructor(private val context: Context) {

    private constructor(builder: Builder) : this(context = builder.context)

    val requestQueue: RequestQueue by lazy { Volley.newRequestQueue(context) }

    companion object {
        fun build(context: Context) = Builder(context.applicationContext).build()
    }

    class Builder(val context: Context) {

        fun build() = VolleyService(this)
    }
}