package com.hanrstudios.drinkingbuddies.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
import kotlinx.android.synthetic.main.activity_privacy_selecting.*
import kotlinx.android.synthetic.main.game_row.view.*
import kotlinx.android.synthetic.main.selectable_users_row.view.*

class PrivacySelectingActivity : AppCompatActivity() {

    class UserSelect(val user: User) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.username_checkbox.text = user.username
        }
        override fun getLayout(): Int {
            return R.layout.selectable_users_row
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_selecting)

        fetchUsers()

        donebutton_privacy.setOnClickListener {
            goToGame()

        }
    }

    private fun goToGame() {
        val drinkingGame: DrinkingGame = intent.getParcelableExtra(GameDesignActivity.GAME_KEY) ?: return
        val gameItem = ThisGame(drinkingGame)

        val intent = Intent(this, GameViewerActivity::class.java)
        intent.putExtra(GameDesignActivity.GAME_KEY, gameItem.game)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        this.finish()

    }

    private fun sendPrivateGame(user: User) {
        val drinkingGame: DrinkingGame = intent.getParcelableExtra(GameDesignActivity.GAME_KEY) ?: return
        val uid = user.uid

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/privateGames/${drinkingGame.gameId}")
        ref.setValue(drinkingGame)

    }

    private fun fetchUsers() {
        val adapter = GroupAdapter<ViewHolder>()
        recyclerview_private.adapter = adapter
        recyclerview_private.layoutManager = LinearLayoutManager(this)
        val ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {
                    Log.d("PrivacySelection", it.toString())
                    val user = it.getValue(User::class.java)

                    if (user != null) {
                        adapter.add(UserSelect(user))
                    }
                }

                adapter.setOnItemClickListener { item, view ->

                    val gameItem = item as GameItem

                    val intent = Intent(view.context, GameViewerActivity::class.java)
                    intent.putExtra(BrowseGamesActivity.GAME_KEY, gameItem.game)
                    startActivity(intent)
                }

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}
