package com.hanrstudios.drinkingbuddies.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock.sleep
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hanrstudios.drinkingbuddies.R
import com.hanrstudios.drinkingbuddies.classes.DrinkingGame
import kotlinx.android.synthetic.main.activity_rating.*

class RatingActivity : AppCompatActivity() {

    var game: DrinkingGame? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        submit_button.setOnClickListener {
            submitRating()
        }

        game = intent.getParcelableExtra(GameViewerActivity.GAME_KEY)
    }

    private fun submitRating() {
        val funRating: Float = fun_ratingbar.rating
        val drunkRating: Float = drunk_ratingbar.rating
        val gameId: String? = game?.gameId
        val uid = FirebaseAuth.getInstance().uid
        val refDrunk = FirebaseDatabase.getInstance().getReference("users/$uid/ratings/drunk/$gameId")
        val refFun = FirebaseDatabase.getInstance().getReference("users/$uid/ratings/fun/$gameId")

        game?.averageDrunkRating(drunkRating)
        game?.averageFunRating(funRating)

        refDrunk.setValue(drunkRating)
        refFun.setValue(funRating)
            .addOnSuccessListener {
                Toast.makeText(this, "you set fun rating to: $funRating and drunk rating to: $drunkRating", Toast.LENGTH_LONG).show()
                finish()
            }


    }
}
