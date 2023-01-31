package com.example.challenge_task4_level_2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*


class NoteList: AppCompatActivity() {
    private lateinit var noteListRecyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var noteList: ArrayList<NoteModel>
    private var state: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        val objOnClickEditNote = object: NoteListAdapter.OnClickEditNoteInterface {
            override fun onClickAbstractMethodEditNote(item: NoteModel) {
                openUpdateNoteDialog(item)
            }
        }
        val objOnClickDeleteNote = object: NoteListAdapter.OnClickDeleteNoteInterface {
            override fun onClickAbstractMethodDeleteNote(item: NoteModel) {
                deleteNoteFromFireBase(item.keyID.toString())
            }
        }
        noteListRecyclerView = findViewById(R.id.noteListRecyclerView)
//        noteListRecyclerView.setHasFixedSize(true)
//        add divider
        noteListRecyclerView.addItemDecoration(DividerItemDecoration(this,RecyclerView.VERTICAL))
        noteListRecyclerView.layoutManager = LinearLayoutManager(this)


        noteList = arrayListOf()
        //        call fun to process
        loadNoteFromFirebase(objOnClickEditNote, objOnClickDeleteNote)
    }

    private fun openUpdateNoteDialog(
       item: NoteModel
    ) {
         lateinit var vNodeID: EditText
         lateinit var vTitle: EditText
         lateinit var vDate: EditText
         lateinit var vDescription: EditText

        val sharedPref = this.getSharedPreferences("updatedState", Context.MODE_PRIVATE)?: return
        with (sharedPref.edit()){
            putString("KeyID", item.keyID)
            apply()
        }

        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.openupdatenotedialog, null)
        mDialog.setView(mDialogView)
        mDialog.setTitle("Updating NoteID - ${item.noteID} ")
        val alertDialog = mDialog.create()
        alertDialog.show()

        vNodeID = mDialogView.findViewById(R.id.noteID)
        vNodeID.setText(item.noteID.toString())

        vTitle = mDialogView.findViewById(R.id.noteTitle)
        vTitle.setText(item.noteTitle.toString())

        vDate = mDialogView.findViewById(R.id.noteDate)
        vDate.setText(item.noteDate.toString())

        vDescription = mDialogView.findViewById(R.id.noteDescription)
        vDescription.setText(item.noteDescription.toString())

        val validate = Validation(mDialogView, alertDialog, state)
        validate.validateNodeID()
        validate.validateTitle()
        validate.openDatePickerIcon()
        validate.validateDate()
        validate.validateDescription()
        validate.clearButton()
        validate.submitButton()
    }
    private fun deleteNoteFromFireBase(
        keyID: String
    ){
        val database = FirebaseDatabase.getInstance().getReference("Notes").child(keyID)
        val mTask = database.removeValue()
        mTask.addOnSuccessListener {
            Toast.makeText(this, " Note KeyID: $keyID - has been deleted successfully.", Toast.LENGTH_LONG).show()
        }.addOnFailureListener{ error ->
            Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
        }
    }
    //        load data from firebase
    private fun loadNoteFromFirebase(
        objOnClickEditNote: NoteListAdapter.OnClickEditNoteInterface,
        objOnClickDeleteNote: NoteListAdapter.OnClickDeleteNoteInterface
    ) {
        dbRef = FirebaseDatabase.getInstance().getReference("Notes")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                noteList.clear()
                if (snapshot.exists()){
                    for (noteSnap in snapshot.children){
                        val noteData = noteSnap.getValue(NoteModel::class.java)
                        noteList.add(noteData!!)
                    }
                    val adapter = NoteListAdapter(noteList, objOnClickEditNote, objOnClickDeleteNote)
                    noteListRecyclerView.adapter = adapter
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.i("loadNoteFromFirebase-onCancelled", databaseError.toException().toString())
            }
        })
    }

    //    Display arrow Icon to go back MainActivity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menuiconnotelist, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.noteListIcon -> {
//                TODO show NoteList by using RecyclerView
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

