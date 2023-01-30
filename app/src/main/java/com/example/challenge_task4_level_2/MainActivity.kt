package com.example.challenge_task4_level_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }


//    Display Note List Icon Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menuiconmainactivity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        Log.i("Thang", "onOptionsItemSelected")
        return when (item.itemId) {
            R.id.noteListIcon -> {
//                TODO show List Not by RecyclerView
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}