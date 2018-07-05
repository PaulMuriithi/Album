package com.album

import android.app.Application
import com.album.network.VolleyService
import io.realm.Realm

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        VolleyService.build(this)
    }
}