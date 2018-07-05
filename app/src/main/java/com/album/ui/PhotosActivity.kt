package com.album.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.album.R
import com.album.adapters.PhotoAdapter
import com.album.constants.AlbumId
import com.album.constants.PhotoUrl
import com.album.interfaces.PhotoClickListener
import com.album.models.Photo
import com.album.network.VolleyService
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.realm.Realm
import kotlinx.android.synthetic.main.toolbar.*
import java.io.IOException

const val TAG = "PhotosActivity"

class PhotosActivity : AppCompatActivity(), Response.Listener<String>, Response.ErrorListener, PhotoClickListener {

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.photos_recycler_view) }
    private val imageDialog by lazy { AlertDialog.Builder(this).create() }
    private val dialogImage by lazy { imageDialog.findViewById<ImageView>(R.id.dialog_image_view) }
    private val noDataTextView by lazy { findViewById<TextView>(R.id.photos_no_data_textView) }
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.photos_progressBar) }
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Open the realm for the UI thread.
        realm = Realm.getDefaultInstance()

        val albumId = intent.getLongExtra(AlbumId, 0)
        val url = PhotoUrl.replace("{albumId}", "$albumId")

        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            } else {
                recyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            }
            recyclerView.setHasFixedSize(true)
            sendRequest(url)
            noDataTextView.visibility = View.GONE
        } else {
            //check if there are some saved photos
            val photos = realm.where(Photo::class.java).findAll()
            if (photos.isEmpty()) {
                noDataTextView.visibility = View.VISIBLE
                noDataTextView.text = getString(R.string.no_connection)
                progressBar.visibility = View.GONE
            } else {
                noDataTextView.visibility = View.GONE
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                recyclerView.adapter = PhotoAdapter(this, photos, this)
                recyclerView.adapter.notifyDataSetChanged()
            }
        }
    }

    private fun sendRequest(url: String) {
        val request = StringRequest(Request.Method.GET, url, this, this)
        VolleyService.build(this).requestQueue.add(request)
        VolleyService.build(this).requestQueue.start()
    }

    private fun handleResponse(response: String) {
        val photoResponse = object : TypeToken<List<Photo>>() {}.type
        val photoList = Gson().fromJson<List<Photo>>(response, photoResponse)

        recyclerView.adapter = PhotoAdapter(this, photoList, this)
        recyclerView.adapter.notifyDataSetChanged()

        //save the photos
        realm.executeTransaction { realm ->
            photoList.forEach {
                val existingPhoto = realm.where(Photo::class.java).equalTo("id", it.id).findFirst()
                if (existingPhoto != null) {
                    //update the existing photo
                    existingPhoto.albumId = it.albumId
                    existingPhoto.title = it.title
                    existingPhoto.url = it.url
                    existingPhoto.thumbnailUrl = it.thumbnailUrl
                } else {
                    //create a new one
                    val photo = realm.createObject(Photo::class.java, it.id)
                    photo.albumId = it.albumId
                    photo.title = it.title
                    photo.url = it.url
                    photo.thumbnailUrl = it.thumbnailUrl
                }
            }
        }
    }


    override fun onResponse(response: String) {
        try {
            handleResponse(response)
            progressBar.visibility = View.GONE
        } catch (e: IOException) {
            Log.e(TAG, e.localizedMessage)
        }
    }

    override fun onErrorResponse(error: VolleyError?) {
        VolleyLog.e("onErrorResponse" + error?.message)
    }

    override fun onPhotoItemClickListener(url: String) {
        val layoutInflater = LayoutInflater.from(this)
        val alertView = layoutInflater.inflate(R.layout.dialog, null, false)
        imageDialog.setView(alertView)
        imageDialog.show()
        Glide.with(this).load(url).into(dialogImage!!)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, AlbumsActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}