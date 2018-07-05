package com.album.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

//for the sake of Realm, annotate with open and have default values for properties
open class Album(
        @PrimaryKey var id: Long = 0,
        var userId: Int = 0,
        var title: String = ""
) : RealmObject()