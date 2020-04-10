package com.hanrstudios.drinkingbuddies.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hanrstudios.drinkingbuddies.R
import com.hanrstudios.drinkingbuddies.classes.DrinkingGame
import com.hanrstudios.drinkingbuddies.classes.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_user_page.*
import kotlinx.android.synthetic.main.gamerow_userpage.view.*
import java.text.DecimalFormat

class UserPageActivity : AppCompatActivity() {

    companion object {
        val GAME_KEY = "GAME_KEY"
    }

    var currentUser: User? = null

    var recentGame: String? = null

    val mDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_page)

        populateCreatedGames()

        currentUser = intent.getParcelableExtra(BrowseUsersActivity.USER_KEY)

        setUser(currentUser!!)

        recent_game_userpage.setOnClickListener {
            goToGame()
        }

        addfriend_button_userpage.setOnClickListener {
            addFriend()
        }
    }

    private fun populateCreatedGames() {
        val createdGames = createdgames_recyclerview_userpage
        val adapter = GroupAdapter<ViewHolder>()
        createdGames.adapter = adapter
        createdGames.layoutManager = LinearLayoutManager(this)

        val ref = mDatabase.getReference("/games/")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val game = it.getValue(DrinkingGame::class.java)
                    if (game != null && currentUser?.uid != null && game.author == currentUser?.uid ) {
                        adapter.add(CreatedGame(game))
                    }
                    adapter.setOnItemClickListener { item, view ->
                        val gameItem = item as CreatedGame
                        val intent = Intent(view.context, GameViewerActivity::class.java)

                        intent.putExtra(GAME_KEY, gameItem.game)
                        startActivity(intent)
                    }

                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun goToGame() {
        if (recentGame == null) {
            return
        } else {
            val ref = mDatabase.getReference("/games/")
            ref.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach{
                        val game = it.getValue(DrinkingGame::class.java)
                        if (game?.title == recentGame) {
                            val intent = Intent(this@UserPageActivity, GameViewerActivity::class.java)
                            intent.putExtra(GAME_KEY, game)
                            startActivity(intent)
                        }
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
        }
    }

    private fun addFriend() {
        val uid = currentUser?.uid
        val ref = mDatabase.getReference("/users/$uid")
    }

    private fun setUser(user: User) {
        val username = user.username

        username_userpage.text = username

        val refCurrentGame = mDatabase.getReference("/users/${user.uid}/currentGame")
        refCurrentGame.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                recentGame = p0.value?.toString() ?: "None"
                recent_game_userpage.text = recentGame
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}

class CreatedGame(val game: DrinkingGame) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        val mDatabase = FirebaseDatabase.getInstance()
        val df = DecimalFormat("#.##")

        viewHolder.itemView.gamename_gamerow_userpage.text = game.title
        view.datecreated_gamerow_userpage.text = game.created
        view.gamecategory_gamerow_userpage.text = game.category

        val refDrunk = mDatabase.getReference("/games/${game.gameId}/drunkRating/average")
        refDrunk.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val rating = p0.value?.toString()?.toFloat() ?: 0f
                view.drunkrating_gamerow_userpage.text = "Drunk Rating: ${df.format(rating)}"
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        val refFun = mDatabase.getReference("/games/${game.gameId}/funRating/average")
        refFun.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val rating = p0.value?.toString()?.toFloat() ?: 0f
                view.funrating_gamerow_userpage.text = "Fun Rating: ${df.format(rating)}"
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.gamerow_userpage
    }
}
