package com.hanrstudios.drinkingbuddies.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_browse_users.*
import kotlinx.android.synthetic.main.activity_privacy_selecting.*
import kotlinx.android.synthetic.main.game_row.view.*
import kotlinx.android.synthetic.main.user_data_row.view.*

class BrowseUsersActivity : AppCompatActivity() {

    companion object {
        val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_users)

        fetchUsers()

    }

    private fun fetchUsers() {
        val adapter = GroupAdapter<ViewHolder>()
        user_recyclerview.adapter = adapter
        user_recyclerview.layoutManager = LinearLayoutManager(this)

        val ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {
                    Log.d("PrivacySelection", it.toString())
                    val user = it.getValue(User::class.java)

                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                    adapter.setOnItemClickListener { item, view ->

                        val userItem = item as UserItem

                        val intent = Intent(view.context, UserPageActivity::class.java)
                        intent.putExtra(USER_KEY, userItem.user)
                        startActivity(intent)
                    }
                }

//                adapter.setOnItemClickListener { item, view ->
//
//                    val userItem = item as UserItem
//
//                    val intent = Intent(view.context, GameViewerActivity::class.java)
//                    intent.putExtra(BrowseGamesActivity.GAME_KEY, userItem.game)
//                    startActivity(intent)
//                }

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}

//populating the rows for users
class UserItem(val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        val myUid = FirebaseAuth.getInstance().uid

        view.username_userrow.text = user.username

        //get the most recent game for each user
        val refRecentGame = FirebaseDatabase.getInstance().getReference("users/${user.uid}/currentGame")
        refRecentGame.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val recentGame = p0.value.toString()
                viewHolder.itemView.most_recent_game_userrow.text = recentGame
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
        val green = R.color.green
        val red = R.color.red
        val friends = mutableListOf<String>()
        val refFriends = FirebaseDatabase.getInstance().getReference("users/$myUid/friends")
        refFriends.addValueEventListener(object: ValueEventListener {
            @SuppressLint("ResourceAsColor")
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    friends.add(it.key.toString())
                    if (friends.contains(user.uid)) {
                        view.friendstatus_userrow.text = "Friends"
//                        view.friendstatus_userrow.setTextColor(green)
                    } else {
                        view.friendstatus_userrow.text = ""
//                        view.friendstatus_userrow.setTextColor(red)
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.user_data_row
    }
}
