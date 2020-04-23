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
import kotlinx.android.synthetic.main.activity_game_viewer.*
import kotlinx.android.synthetic.main.activity_game_viewer.view.*
import kotlinx.android.synthetic.main.activity_game_viewer.view.game_name_viewer
import kotlinx.android.synthetic.main.game_row.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class GameDesignActivity : AppCompatActivity() {

    var editingGame: DrinkingGame? = null
    val mDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_design)

        editingGame = intent.getParcelableExtra(GameViewerActivity.GAME_KEY)
        if (editingGame != null) {
            editGame()
        }

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

    private fun editGame() {
        setCatSpinner()
        game_name_design.setText(editingGame?.title)
        onedrinkrules_edittext_design.setText(editingGame?.rules)
        privacy_switch_design.isChecked = editingGame?.private ?: false
    }

    private fun setCatSpinner() {
        val catSpinner = game_category_spinner_design
        when (editingGame?.category) {
            "Movie" -> catSpinner.setSelection(1)
            "Cards" -> catSpinner.setSelection(2)
            "Video" -> catSpinner.setSelection(3)
            "Event" -> catSpinner.setSelection(4)
            "Misc" -> catSpinner.setSelection(5)
            else -> {
                catSpinner.setSelection(0)
            }
        }
    }

    private fun createGame() {
        val title = game_name_design.text.toString()
        val private: Boolean = privacy_switch_design.isChecked
        val rules = onedrinkrules_edittext_design.text.toString()
        val cat = game_category_spinner_design.selectedItem.toString()

        if (editingGame != null) {
            val game = editingGame
            val path = "/games/${game?.gameId}"
            if (game?.title != title) {
                val refTitle = mDatabase.getReference("$path/title")
                refTitle.setValue(title)
            }
            if (game?.private != privacy_switch_design.isChecked) {
                val refPrivate = mDatabase.getReference("$path/private")
                refPrivate.setValue(private)
            }
            if (game?.rules != rules) {
                val refRules = mDatabase.getReference("$path/rules")
                refRules.setValue(rules)
            }
            if (game?.category != cat) {
                val refCat = mDatabase.getReference("$path/category")
                refCat.setValue(cat)
            }

        } else {

            val author = FirebaseAuth.getInstance().uid.toString()
            val currentDate =
                LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))


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
