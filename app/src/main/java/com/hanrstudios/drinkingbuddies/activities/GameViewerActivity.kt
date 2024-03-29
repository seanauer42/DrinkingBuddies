package com.hanrstudios.drinkingbuddies.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.INVISIBLE
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hanrstudios.drinkingbuddies.R
import com.hanrstudios.drinkingbuddies.classes.DrinkingGame
import com.hanrstudios.drinkingbuddies.classes.User
import kotlinx.android.synthetic.main.activity_game_viewer.*

class GameViewerActivity : AppCompatActivity() {

    val mDatabase = FirebaseDatabase.getInstance()

    companion object {
        val GAME_KEY = "GAME_KEY"
    }

    var currentGame: DrinkingGame? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_viewer)

        currentGame = intent.getParcelableExtra(GameDesignActivity.GAME_KEY)
//        if(currentGame == null) {
//            setGameAsGameOfTheDay()
//        } else {
        setGame(currentGame!!)
//        }

        displayText()

        editgame_gameviewer.setOnClickListener {
            editGame()
        }

        rate_button_gameviewer.setOnClickListener {
            rateGame()
        }

//        supportActionBar?.title = currentGame.title

    }

    private fun displayText() {
        val uid = FirebaseAuth.getInstance().uid
        val game = currentGame ?: return

        //show if game is private
        if (currentGame?.private!!) {
            privatetextview_gameviewer.text = "Private"
        } else {
            privatetextview_gameviewer.text = ""
        }

        //makes the edit button disapear if game has been edited by this user
        val ratedGames = mutableListOf<String>()
        val refRatedGames = mDatabase.getReference("/users/$uid/ratings/drunk/")
        refRatedGames.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    ratedGames.add(it.key.toString())
                    Log.d("gameIds", ratedGames.toString())
                    Log.d("this gameId", game.gameId!!)
                    if (ratedGames.contains(game.gameId)) {
                        rate_button_gameviewer.visibility = GONE
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })

        //makes edit show if the viewer is the creator
        if (game.author == uid) {
            editgame_gameviewer.text = "edit"
        } else {
            editgame_gameviewer.text = ""
        }
    }

    private fun editGame() {
        val intent = Intent(this, GameDesignActivity::class.java)
        intent.putExtra(GAME_KEY, currentGame)
        startActivity(intent)
    }

    private fun rateGame() {
//        Toast.makeText(this, "Sorry, but this feature isn't available yet.", Toast.LENGTH_LONG).show()
        val intent = Intent(this, RatingActivity::class.java)
        intent.putExtra(GAME_KEY, currentGame)
        startActivity(intent)
    }

    private fun setGame(game: DrinkingGame) {
        //set this game as user's current game
        val uid = FirebaseAuth.getInstance().uid
        val refCurrentGame = mDatabase.getReference("/users/$uid/currentGame")
        refCurrentGame.setValue(game.title)

        val creator = game.author
        val ref = mDatabase.getReference("/users/$creator")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val author = p0.getValue(User::class.java)
                author_gameviewer.text = author?.username
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        game_name_viewer.text = game.title
        gamerules_gameviewer.text = game.rules
        posteddate_gameviewer.text = game.created

    }

    override fun onBackPressed() {
        this.finish()
    }
}


