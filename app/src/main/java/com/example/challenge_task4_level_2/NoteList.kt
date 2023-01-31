package com.example.challenge_task4_level_2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*


class NoteList: AppCompatActivity() {
    private lateinit var noteListRecyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var noteList: ArrayList<NoteModel>
    lateinit var detailActivity: DetailActivity
    init{
        detailActivity  = DetailActivity()
    }

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
        noteListRecyclerView.setHasFixedSize(true)
        noteListRecyclerView.layoutManager = LinearLayoutManager(this)
//        add divider
        noteListRecyclerView.addItemDecoration(DividerItemDecoration(this,RecyclerView.VERTICAL))
        noteList = arrayListOf<NoteModel>()
        //        call fun to process
        loadNoteFromFirebase(objOnClickEditNote, objOnClickDeleteNote)
    }

    private fun showToastEdit(item: NoteModel){
        Toast.makeText(this,"EDIT: ${item.noteTitle} ",Toast.LENGTH_SHORT).show()
    }
    private fun openUpdateNoteDialog(
       item: NoteModel
    ) {
         lateinit var vNodeID: EditText
         lateinit var vTitle: EditText
         lateinit var vDate: EditText
         lateinit var vDateIcon: ImageView
         lateinit var vDescription: EditText
         lateinit var btnClear: Button
         lateinit var btnSubmit: Button

        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.openupdatenotedialog, null)
        mDialog.setView(mDialogView)
        mDialog.setTitle("Updating NoteID - ${item.noteID} ")
        val alertDialog = mDialog.create()
        alertDialog.show()
//        val view =findViewById(R.id.openUpdateDialogView).getRootView()

        vNodeID = mDialogView.findViewById(R.id.noteID)
        vTitle = mDialogView.findViewById(R.id.noteTitle)
        vDate = mDialogView.findViewById(R.id.noteDate)
        vDateIcon = mDialogView.findViewById(R.id.dateIcon)
        vDescription = mDialogView.findViewById(R.id.noteDescription)

        mDialogView.setOnClickListener{
            detailActivity.Validation(mDialogView)
        }
        vNodeID.addTextChangedListener {
            detailActivity.Validation(mDialogView)
        }
//        mDialogView.addOnLayoutChangeListener()
//        vNodeID.setText(item.noteID.toString())
        btnClear = mDialogView.findViewById(R.id.btnClear)
        btnClear.setOnClickListener{
            detailActivity.clearButton(mDialogView)
        }

        btnSubmit = mDialogView.findViewById(R.id.btnSubmit)
        btnSubmit.setOnClickListener{
            detailActivity.submitButton(mDialogView)
        }
//        val intent = Intent(this, DetailActivity::class.java)
//        startActivity(intent)
//        vNodeID = findViewById(R.id.noteID)
//        vNodeID.setText(item.noteID.toString())

//        detailActivity.vNodeID.setText(item.noteID.toString())
//        Log.i("YOUAREHER", "${item.noteID.toString()}")

//        detailActivity.Validation()
    }
    private fun deleteNoteFromFireBase(
        keyID: String
    ){
        val database = FirebaseDatabase.getInstance().getReference("Notes").child(keyID)
        val mTask = database.removeValue()
        mTask.addOnSuccessListener {
            Toast.makeText(this, " Note KeyID: ${keyID} - has been deleted successfully.", Toast.LENGTH_LONG).show()
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
}

