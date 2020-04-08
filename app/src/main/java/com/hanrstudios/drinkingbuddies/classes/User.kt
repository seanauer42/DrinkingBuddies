package com.hanrstudios.drinkingbuddies.classes

import android.os.Parcelable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String): Parcelable {
    constructor() : this("", "")
}