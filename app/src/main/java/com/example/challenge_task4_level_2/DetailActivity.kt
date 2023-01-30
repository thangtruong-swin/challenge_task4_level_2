package com.example.challenge_task4_level_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
    }


    //    Display sub-items menu as  MainActivity and NoteList Activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.submenuitemsdetailactivity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        Log.i("Thang", "onOptionsItemSelected")
        return when (item.itemId) {
            R.id.mainActivity -> {
//                TODO show NoteList by using RecyclerView
                val intentNoteList = Intent(this, MainActivity::class.java)
                startActivity(intentNoteList)
                true
            }
            R.id.NoteListActivity -> {
//                TODO show NoteList by using RecyclerView
                val intentMainActivity = Intent(this, NoteList::class.java)
                startActivity(intentMainActivity)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}