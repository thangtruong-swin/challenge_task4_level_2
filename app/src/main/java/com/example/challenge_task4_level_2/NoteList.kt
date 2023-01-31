package com.example.challenge_task4_level_2

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class NoteList : AppCompatActivity() {

    private lateinit var noteListRecyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var noteList: ArrayList<NoteModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        noteListRecyclerView = findViewById(R.id.noteListRecyclerView)
        noteListRecyclerView.setHasFixedSize(true)
        noteListRecyclerView.layoutManager = LinearLayoutManager(this)
//        add divider
        noteListRecyclerView.addItemDecoration(DividerItemDecoration(this,RecyclerView.VERTICAL))
        noteList = arrayListOf<NoteModel>()
        //        call fun to process
        loadNoteFromFirebase()

    }

    //        load data from firebase
    private fun loadNoteFromFirebase() {

        dbRef = FirebaseDatabase.getInstance().getReference("Notes")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                noteList.clear()
                if (snapshot.exists()){
                    for (noteSnap in snapshot.children){
                        val noteData = noteSnap.getValue(NoteModel::class.java)
                        noteList.add(noteData!!)
                    }
                    val adapter = NoteListAdapter(noteList)
                    noteListRecyclerView.adapter = adapter
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }
}