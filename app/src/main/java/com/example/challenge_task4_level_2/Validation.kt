package com.example.challenge_task4_level_2

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Matcher
import java.util.regex.Pattern

class Validation(
    private var view: View, private var alertDialog: AlertDialog?= null,
    private var state: Boolean? = false) {
    private lateinit var vNodeID: EditText
    private lateinit var vTitle: EditText
    private lateinit var vDate: EditText
    private lateinit var vDateIcon: ImageView
    private lateinit var vDescription: EditText
    private lateinit var btnClear: Button
    private lateinit var btnSubmit: Button

    private lateinit var dbRef: DatabaseReference
    private val sharedPref = view.context.getSharedPreferences("updatedState", Context.MODE_PRIVATE)!!
    private var KeyID = sharedPref.getString("KeyID", "0").toString()


    fun validateNodeID() {
         vNodeID = view.findViewById(R.id.noteID)
        vNodeID.addTextChangedListener{
            for(i in 0  until vNodeID.length()){
                if(vNodeID.text[i].isLetter() || !vNodeID.text[i].isDigit()) {
                    vNodeID.error = "NoteID should be digits"
                }
            }
            if(vNodeID.text.trim().isEmpty()){
                vNodeID.error = "NoteID should not empty"
            }
            if(vNodeID.text.trim().length > 10){
                vNodeID.error = "NoteID should not greater than 10 digits"
            }
        }
    }
    fun validateTitle() {
        vTitle = view.findViewById(R.id.noteTitle)
        val special = Pattern.compile("[^a-z\\d ]", Pattern.CASE_INSENSITIVE)
        vTitle.addTextChangedListener{
            val matcher: Matcher = special.matcher(vTitle.text.trim())
            val specialSymbols = matcher.find()
            Log.i("specialSymbols",specialSymbols.toString())
            if(specialSymbols){
                vTitle.error = "Title should not have special characters"
            }
            if(vTitle.text.trim().isEmpty()){
                vTitle.error = "Title should not empty"
            }
            if(vTitle.text.trim().length > 100){
                vTitle.error = "Title should not greater than 100 characters"
            }
        }
    }
    fun openDatePickerIcon() {
        vDateIcon = view.findViewById(R.id.dateIcon)
        vDateIcon.setOnClickListener{
            displayDatePicker()
        }
    }

    //    fun to process DatePicker
    val displayDatePicker = {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth  = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            view.context,
            { _, year, monthOfYear, dayOfMonth ->
                val dateOfMonth = if(dayOfMonth<10) "0$dayOfMonth" else dayOfMonth.toString()
                val monthOfYear = if((monthOfYear+1)<10) "0"+(monthOfYear+1).toString() else (monthOfYear+1).toString()
                vDate.setText(dateOfMonth + "-" + monthOfYear + "-" + year)
            },
            //passing year, month, day for the selected date in our date picker.
            year,
            month,
            dayOfMonth
        )
        // to display our date picker dialog.
        datePickerDialog.show()
    }
    fun validateDate() {
        vDate = view.findViewById(R.id.noteDate)
        vDate.addTextChangedListener {
            if(!dateValidation() || vDate.text.trim().length > 10){
                vDate.error = "Date input invalid"
            }
            else{
                vDate.setError(null)
            }
        }
    }
    private fun dateValidation():Boolean{
        for(i in 0 until vDate.length()){
            if (vDate.text?.get(i)?.isLetter()==true) {
                return false
            }
        }
        val dayPicker = vDate.text.toString().substringBefore("-")
        val d2 = vDate.text.toString().substringAfter("-")
        val monthPicker = d2.substringBefore("-")
        val yearPicker = d2.substringAfter("-")
        if(dayPicker.isNotEmpty() && monthPicker.isNotEmpty() && yearPicker.isNotEmpty()){
            return isValidDate(dayPicker.toInt(),monthPicker.toInt(),yearPicker.toInt())
        }
        return false
    }
    fun validateDescription() {
        vDescription = view.findViewById(R.id.noteDescription)
        vDescription.addTextChangedListener {
            if (vDescription.text.trim().isEmpty()) {
                vDescription.error = "Description should not empty"
            }
        }
    }
    fun clearButton() {
        btnClear = view.findViewById(R.id.btnClear)
        btnClear.setOnClickListener{
        resetEmptyFields()
    }
    }
    private fun saveNote() {
//    Save NoteData into Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("Notes")
        val noteKey = dbRef.push().key!!
        val note = NoteModel(noteKey,  vNodeID.text.toString().toInt(),
            vTitle.text.toString(),vDate.text.toString(),vDescription.text.toString())
        dbRef.child(noteKey).setValue(note)
            .addOnCompleteListener {
                Toast.makeText(view.context, "Note inserted successfully", Toast.LENGTH_LONG).show()
            }.addOnFailureListener { err ->
                Toast.makeText(view.context, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
    }
    fun submitButton() {
        btnSubmit = view.findViewById(R.id.btnSubmit)
        btnSubmit.setOnClickListener{
            if(state!!){  // state == true Update Note
                if( (vNodeID.error == null) && (vNodeID.text.trim().toString().isNotEmpty())
                    && (vTitle.error == null) && (vTitle.text.trim().toString().isNotEmpty())
                    && (vDate.error == null) && (vDate.text.trim().toString().isNotEmpty())
                    && (vDescription.error == null) && (vDescription.text.trim().toString()
                        .isNotEmpty())
                ){
                    updateNote(KeyID,vNodeID.text.trim().toString().toInt(),vTitle.text.trim().toString(),
                        vDate.text.trim().toString(),vDescription.text.trim().toString())
                    Toast.makeText(view.context, "Note Data Updated successfully.", Toast.LENGTH_LONG).show()
                    alertDialog?.dismiss()
                }
                else{
                    if(vNodeID.text.isEmpty()){
                        vNodeID.error = "NoteID should not empty"
                    }
                    if(vTitle.text.isEmpty()){
                        vTitle.error = "Title should not empty"
                    }
                    if(vDate.text.isEmpty()){
                        vDate.error = "Date should not empty"
                    }
                    if(vDescription.text.isEmpty()){
                        vDescription.error = "vDescription should not empty"
                    }
                }
            }
            else { // Save a new note
                if( (vNodeID.error == null) && (vNodeID.text.trim().toString().isNotEmpty())
                    && (vTitle.error == null) && (vTitle.text.trim().toString().isNotEmpty())
                    && (vDate.error == null) && (vDate.text.trim().toString().isNotEmpty())
                    && (vDescription.error == null) && (vDescription.text.trim().toString()
                        .isNotEmpty())
                ){
                    saveNote()
                    resetEmptyFields()
                }
                else{
                    if(vNodeID.text.isEmpty()){
                        vNodeID.error = "NoteID should not empty"
                    }
                    if(vTitle.text.isEmpty()){
                        vTitle.error = "Title should not empty"
                    }
                    if(vDate.text.isEmpty()){
                        vDate.error = "Date should not empty"
                    }
                    if(vDescription.text.isEmpty()){
                        vDescription.error = "vDescription should not empty"
                    }
                }
            }
        }
    }


    private fun updateNote(
        keyID: String,
        noteID: Int,
        noteTitle: String,
        noteDate: String,
        noteDescription: String
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Notes").child(keyID)
        val noteInfo = NoteModel(keyID, noteID, noteTitle, noteDate, noteDescription)
        dbRef.setValue(noteInfo)
    }

    private fun resetEmptyFields() {
        vNodeID.text.clear()
        vNodeID.setError(null)

        vTitle.text.clear()
        vTitle.setError(null)

        vDate.text.clear()
        vDate.setError(null)

        vDescription.text.clear()
        vDescription.setError(null)
    }
        //    Handle LeapYear
        private fun isLeapYear(year: Int): Boolean {
//        check isLeap year
        return (((year % 4 == 0) &&
                (year % 100 != 0)) ||
                (year % 400 == 0))
        }

    private fun isValidDate(dayValue: Int, monthValue: Int, yearValue: Int): Boolean {
        val maxValidYear = 9999
        val minValidYear = 1800
        if (yearValue > maxValidYear || yearValue < minValidYear) {
            return false
        }
        if (monthValue < 1 || monthValue > 12) {
            return false
        }
        if (dayValue < 1 || dayValue > 31) {
            return false
        }
        // Handle with leap year
        if (monthValue == 2) {
            return if (isLeapYear(yearValue)) {
                (dayValue <= 29)
            } else {
                (dayValue <= 28)
            }
        }
        // Months of April, June, Sept and Nov must have number of days less than or equal to 30.
        if (monthValue == 4 || monthValue == 6 ||
            monthValue == 9 || monthValue == 11
        )
            return (dayValue <= 30)

        return true
    }
}