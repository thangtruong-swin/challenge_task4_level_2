package com.example.challenge_task4_level_2

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

class NoteListAdapter(private var noteList: ArrayList<NoteModel>,
                      private val OnClickEditNote: OnClickEditNoteInterface,
                      private val OnClickDeleteNote: OnClickDeleteNoteInterface
                      ): RecyclerView.Adapter<NoteListAdapter.ViewHolder>() {
    interface OnClickEditNoteInterface {
        fun onClickAbstractMethodEditNote(item: NoteModel)
    }
    interface OnClickDeleteNoteInterface {
        fun onClickAbstractMethodDeleteNote(item: NoteModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteListAdapter.ViewHolder {
//        Log.i("Thang", "onCreateViewHolder")
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.layout_row, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount() = noteList.size

    override fun onBindViewHolder(holder: NoteListAdapter.ViewHolder, position: Int) {
        val item = noteList[position]
        holder.bind(item)
//        Log.i("Thang", "onBindViewHolder")
    }

    inner class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        private val vKeyID: TextView = v.findViewById(R.id.vKeyID)
        private val vNoteID: TextView = v.findViewById(R.id.vNoteID)
        private val vTitle: TextView = v.findViewById(R.id.vTitle)
        private val vDate: TextView = v.findViewById(R.id.vDate)
        private val vDescription: TextView = v.findViewById(R.id.vDescription)
        private val vEditIcon: ImageView = v.findViewById(R.id.editIcon)
        private val vDeleteIcon: ImageView = v.findViewById(R.id.deleteIcon)

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(item: NoteModel) {
            vKeyID.text = item.keyID
            vNoteID.text = item.noteID.toString()
            vTitle.text = item.noteTitle
            vDate.text = item.noteDate
            vDescription.text = item.noteDescription

            vEditIcon.setOnClickListener {
                OnClickEditNote.onClickAbstractMethodEditNote(item)
            }
            vDeleteIcon.setOnClickListener {
                OnClickDeleteNote.onClickAbstractMethodDeleteNote(item)
            }
        }


    }
}