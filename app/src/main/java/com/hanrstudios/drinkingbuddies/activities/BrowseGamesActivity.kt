package com.hanrstudios.drinkingbuddies.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
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
import kotlinx.android.synthetic.main.activity_browse_games.*
import kotlinx.android.synthetic.main.game_row.view.*

class BrowseGamesActivity : AppCompatActivity() {

//    companion object {
//        var currentUser: User? = null
//    }

    companion object {
        val GAME_KEY = "GAME_KEY"
    }

//    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_games)

        verifyUserIsLoggedIn()

        fetchCurrentUser()

        fetchGames()

        supportActionBar?.title = ""
    }

//    private fun gameOfTheDay() {
//        val intent = Intent(this, GameViewerActivity::class.java)
//        startActivity(intent)
//    }

    private fun fetchCurrentUser() {
        var currentUser: User?
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current user is: ${currentUser?.username}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        val intent = Intent(this, RegisterActivity::class.java)
        if (uid == null) {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun fetchGames() {
        val adapter = GroupAdapter<ViewHolder>()
        recyclerview_browsegames.adapter = adapter
        recyclerview_browsegames.layoutManager = LinearLayoutManager(this)

        val ref = FirebaseDatabase.getInstance().getReference("games")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {
                    Log.d("NewGame", it.toString())
                    val game = it.getValue(DrinkingGame::class.java)

                    if (game != null) {
                        adapter.add(GameItem(game))
                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val gameItem = item as GameItem
                    val intent = Intent(view.context, GameViewerActivity::class.java)

                    intent.putExtra(GAME_KEY, gameItem.game)
                    startActivity(intent)
                }

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.menu_create_game -> {
                val intent = Intent(this, GameDesignActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_show_users -> {
                val intent = Intent(this, BrowseUsersActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

class GameItem(val game: DrinkingGame) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        //will be called in our user layout
        viewHolder.itemView.gametitle_gamerow.text = game.title
        viewHolder.itemView.category_gamerow.text = game.category
        viewHolder.itemView.createddate_gamerow.text = game.created

        //displaying the username in the recycler view
        val authorId = game.author
        val ref = FirebaseDatabase.getInstance().getReference("/users/$authorId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                viewHolder.itemView.author_gamerow.text = user?.username
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
        //displaying the drunk rating in the recycler view
        val refDrunkAvg = FirebaseDatabase.getInstance().getReference("/games/${game.gameId}/drunkRating/average")
        refDrunkAvg.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                    val drunkAvg = p0.value?.toString()
                    viewHolder.itemView.drunkrating_gamerow.text = "Drunk Rating: $drunkAvg"
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
        //displaying the fun rating in the recycler view
        val refFunAvg = FirebaseDatabase.getInstance().getReference("/games/${game.gameId}/funRating/average")
        refFunAvg.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                    val funAvg = p0.value.toString()
                    viewHolder.itemView.funrating_gamerow.text = "Fun Rating: $funAvg"
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.game_row
    }
}
