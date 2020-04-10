package com.hanrstudios.drinkingbuddies.classes

import android.os.Parcelable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.parcel.Parcelize
import kotlin.math.roundToLong

@Parcelize
class DrinkingGame(
    val author: String, val title: String, val private: Boolean, val category: String,
    val rules: String, val created: String, val gameId: String?
) :
    Parcelable {
    constructor() : this("", "", false, "", "", "", "")

    fun averageDrunkRating(drunkRating: Float) {
//        val ratings = mutableListOf<Double>()
        var avg: Float
        var num: Int
        val refAvg =
            FirebaseDatabase.getInstance().getReference("games/$gameId/drunkRating/average")
        val refNum = FirebaseDatabase.getInstance().getReference("games/$gameId/drunkRating/num")

        refAvg.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value == null) {
                    refAvg.setValue(0)
                }
                avg = p0.value?.toString()?.toFloat() ?: 0f
                refNum.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.value == null) {
                            refNum.setValue(0)
                        }
                        num = p0.value?.toString()?.toInt() ?: 0
                        val newAverage = avg + ((drunkRating - avg) / (num + 1))
                        refAvg.setValue(newAverage)
                        refNum.setValue(num + 1)
                    }


                    override fun onCancelled(p0: DatabaseError) {

                    }

                })


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
//
    }

    fun averageFunRating(funRating: Float) {
        var avg: Float
        var num: Int
        val refAvg = FirebaseDatabase.getInstance().getReference("games/$gameId/funRating/average")
        val refNum = FirebaseDatabase.getInstance().getReference("games/$gameId/funRating/num")

        refAvg.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value == null) {
                    refAvg.setValue(0)
                }
                avg = p0.value?.toString()?.toFloat() ?: 0f
                refNum.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.value == null) {
                            refNum.setValue(0)
                        }
                        num = p0.value?.toString()?.toInt() ?: 0
                        val newAverage = avg + ((funRating - avg) / (num + 1))
                        refAvg.setValue(newAverage)
                        refNum.setValue(num + 1)
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

    }
}