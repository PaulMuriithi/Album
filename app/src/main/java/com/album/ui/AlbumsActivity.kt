package com.album.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.album.R
import com.album.adapters.AlbumAdapter
import com.album.constants.AlbumId
import com.album.constants.AlbumUrl
import com.album.interfaces.AlbumClickListener
import com.album.models.Album
import com.album.network.VolleyService
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.toolbar.*
import java.io.IOException


class AlbumsActivity : AppCompatActivity(), Response.Listener<String>, Response.ErrorListener, AlbumClickListener, SwipeRefreshLayout.OnRefreshListener {

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.album_recycler_view) }
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.album_progressBar) }
    private val noDataTextView by lazy { findViewById<TextView>(R.id.album_no_data_textView) }
    private val swipeRefreshLayout by lazy { findViewById<SwipeRefreshLayout>(R.id.album_swipe_layout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_albums)
        setSupportActionBar(toolbar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(divider)
        fetchAlbumsIfConnected()
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    private fun fetchAlbumsIfConnected() {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
            sendRequest(AlbumUrl)
            noDataTextView.visibility = View.GONE
        } else {
            noDataTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            noDataTextView.text = getString(R.string.no_connection)
            progressBar.visibility = View.GONE
        }
    }

    private fun sendRequest(url: String) {
        val request = StringRequest(Request.Method.GET, url, this, this)
        VolleyService.build(this).requestQueue.add(request)
        VolleyService.build(this).requestQueue.start()
    }

    private fun castByGSon(response: String) {
        val albumResponse = object : TypeToken<List<Album>>() {}.type
        val albumList = Gson().fromJson<List<Album>>(response, albumResponse)

        recyclerView.adapter = AlbumAdapter(albumList, this)
        recyclerView.adapter.notifyDataSetChanged()

    }

    override fun onResponse(response: String) {
        try {
            castByGSon(response)
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onErrorResponse(error: VolleyError?) {
        VolleyLog.e("onErrorResponse" + error?.message)
    }

    override fun onAlbumItemClickListener(albumId: Long) {
        val intent = Intent(this, PhotosActivity::class.java)
        intent.putExtra(AlbumId, albumId)
        startActivity(intent)
    }

    override fun onRefresh() {
        fetchAlbumsIfConnected()
        swipeRefreshLayout.isRefreshing = false
    }
}
