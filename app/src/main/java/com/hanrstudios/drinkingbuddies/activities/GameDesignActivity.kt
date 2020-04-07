package com.hanrstudios.drinkingbuddies.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hanrstudios.drinkingbuddies.R
import com.hanrstudios.drinkingbuddies.classes.DrinkingGame
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_game_design.*
import kotlinx.android.synthetic.main.activity_game_viewer.view.*
import kotlinx.android.synthetic.main.game_row.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class GameDesignActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_design)

        game_design_submit_button.setOnClickListener {
            createGame()
        }

        val spinner: Spinner = findViewById(R.id.game_category_spinner_design)
        ArrayAdapter.createFromResource (this, R.array.categories, android.R.layout.simple_spinner_item).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    companion object {
        val GAME_KEY = "GAME_KEY"
    }

    private fun createGame() {
        val title = game_name_design.text.toString()
        val private: Boolean = privacy_switch_design.isChecked
        val rules = onedrinkrules_edittext_design.text.toString()
        val cat = game_category_spinner_design.selectedItem.toString()
        val author = FirebaseAuth.getInstance().uid.toString()
        val currentDate = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))

        val ref = FirebaseDatabase.getInstance().getReference("/games").push()
        val gameId = ref.key

        val drinkingGame = DrinkingGame(author, title, private, cat, rules, currentDate, gameId)

        ref.setValue(drinkingGame)
            .addOnSuccessListener {

                if (private) {
                    val gameItem = ThisGame(drinkingGame)

                    val intent = Intent(this, PrivacySelectingActivity::class.java)
                    intent.putExtra(GAME_KEY, gameItem.game)
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    this.finish()

                } else {
                    val gameItem = ThisGame(drinkingGame)

                    val intent = Intent(this, GameViewerActivity::class.java)
                    intent.putExtra(GAME_KEY, gameItem.game)
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    this.finish()

                }


            }
    }

}


class ThisGame(val game: DrinkingGame): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.game_name_viewer.text = game.title
        viewHolder.itemView.author_gameviewer.text = game.author
        viewHolder.itemView.gamerules_gameviewer.text = game.rules
        viewHolder.itemView.posteddate_gameviewer.text = game.created
    }

    override fun getLayout(): Int {
        return R.layout.activity_game_viewer
    }
}
