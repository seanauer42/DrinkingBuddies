package com.hanrstudios.drinkingbuddies.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

        rate_button.setOnClickListener {
            rateGame()
        }

//        supportActionBar?.title = currentGame.title

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

        val ratedGames = mutableListOf<String>()
        val refRatedGames = mDatabase.getReference("/users/$uid/ratings/drunk/${game.gameId}")
        refRatedGames.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    ratedGames.add(it.toString())
                    if (ratedGames.contains(game.gameId)) {
                        rate_button.isVisible = false
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

//    private fun setGameAsGameOfTheDay() {
//        val ref = FirebaseDatabase.getInstance().getReference("/games/-LujoKt5xDtfLwbYUTIP").push()
//        val author = ref.child("author").toString()
//        val title = ref.child("title").toString()
//        val private = ref.child("private").toString().toBoolean()
//        val category = ref.child("category").toString()
//        val rules = ref.child("rules").toString()
//        val created = ref.child("created").toString().toLong()
//
//        currentGame = DrinkingGame(author, title, private, category, rules, created)
//
//        game_name_viewer.text = title
//        author_gameviewer.text = author
//        gamerules_gameviewer.text = rules
//        posteddate_gameviewer.text = "today"
//
//    }



//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId) {
//            R.id.menu_go_to_menu -> {
//                val intent = Intent(this, BrowseGamesActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.nav_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
}


