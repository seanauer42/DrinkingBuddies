package com.hanrstudios.drinkingbuddies.classes

import android.os.Parcelable
import android.util.Log
import com.google.firebase.database.*
import kotlinx.android.parcel.Parcelize
import java.security.Timestamp

@Parcelize
class DrinkingGame(val author: String, val title: String, val private: Boolean, val category: String,
                   val rules: String, val created: String, val gameId: String?) :
    Parcelable {
    constructor() : this ("", "", false, "", "", "", "")

    fun averageDrunkRating(drunkRating: Float) {
//        val ratings = mutableListOf<Double>()
        var avg: Float
        var num: Int
        val refAvg = FirebaseDatabase.getInstance().getReference("games/$gameId/drunkRating/average")
        val refNum = FirebaseDatabase.getInstance().getReference("games/$gameId/drunkRating/num")


        refAvg.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                avg = p0.value.toString().toFloat()

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
//        val avg = refAvg.toString().toFloat()
//        val num = refNum.toString().toFloat()


        val newAverage = avg + ((drunkRating - avg) / (num + 1))

        refAvg.setValue(newAverage)
        refNum.setValue(num + 1)

        //trying to retrieve the data and add the ratings to a list of which I will find the average
//        ref.addChildEventListener(object : ChildEventListener {
//            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//
//            }
//            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//
//            }
//
//            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//            }
//
//            override fun onChildRemoved(p0: DataSnapshot) {
//
//            }
//            override fun onCancelled(p0: DatabaseError) {
//            }
//        })

//        ref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(p0: DataSnapshot) {
//                p0.children.forEach {
//                    val rating = it.value.toString().toDouble()
//                    ratings.add(rating)
//                    Log.d("$title rating", rating.toString())
//                }
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//            }
//        })

//        Log.d("$title rating", ratings.toString())
//        if (ratings.size == 0) {
//            return "No Drunk ratings yet"
//        } else {
//            return "Drunk: " + ratings.average().toString()
//        }
    }

    fun getDrunkRating(): String {
        val refAvg = FirebaseDatabase.getInstance().getReference("games/$gameId/drunkRating/average")
        return refAvg.database.toString()
//        return refAvg.datasnapshot
//        refAvg.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(p0: DataSnapshot): DataSnapshot {
//                return p0.child()
//            }
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
    }

    fun averageFunRating(funRating: Float) {
        val refAvg = FirebaseDatabase.getInstance().getReference("games/$gameId/funRating/average")
        val refNum = FirebaseDatabase.getInstance().getReference("games/$gameId/funRating/num")
        val avg = refAvg.toString().toFloat()
        val num = refNum.toString().toFloat()

        val newAverage = avg + ((funRating- avg) / (num + 1))

        refAvg.setValue(newAverage)
        refNum.setValue(num + 1)

//        val ratings = mutableListOf<Double>()
//        val ref = FirebaseDatabase.getInstance().getReference("games/$gameId/funRating")

//        ref.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(p0: DataSnapshot) {
//                p0.children.forEach{
//                    val rating = it.value.toString().toDouble()
//                    ratings.add(rating)
//                }
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//            }
//        })
//
//        if (ratings.size == 0) {
//            return "No fun ratings yet"
//        } else {
//            return "Fun: " + ratings.average().toString()
//        }
    }

    fun getFunRating() : String {
        val refFun = FirebaseDatabase.getInstance().getReference("games/$gameId/funRating/average")
        return refFun.database.toString()
    }
}