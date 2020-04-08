package com.hanrstudios.drinkingbuddies.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hanrstudios.drinkingbuddies.R
import com.hanrstudios.drinkingbuddies.classes.User
import kotlinx.android.synthetic.main.activity_user_page.*

class UserPageActivity : AppCompatActivity() {

    var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_page)

        currentUser = intent.getParcelableExtra(BrowseUsersActivity.USER_KEY)

        setUser(currentUser!!)

        addfriend_button_userpage.setOnClickListener {
            addFriend()
        }
    }

    private fun addFriend() {
        val uid = currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/${currentUser?.uid}")
    }

    private fun setUser(user: User) {
        val username = user.username

        username_userpage.text = username

        val refCurrentGame = FirebaseDatabase.getInstance().getReference("/users/${user.uid}/currentGame")
        refCurrentGame.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                recent_game_userpage.text = p0.value.toString()
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }


}
