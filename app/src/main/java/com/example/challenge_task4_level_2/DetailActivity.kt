package com.example.challenge_task4_level_2

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Matcher
import java.util.regex.Pattern


open class DetailActivity : AppCompatActivity() {

    private lateinit var vNodeID: EditText
    private lateinit var vTitle: EditText
    private lateinit var vDate: EditText
    private lateinit var vDateIcon: ImageView
    private lateinit var vDescription: EditText
    private lateinit var btnClear: Button
    private lateinit var btnSubmit: Button
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val rootView =
            (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup

//        call fun to submit
        val currentView = getWindow().getDecorView().getRootView()
        //cal fun to validate widgets
        Validation(rootView)

        //call fun to clear error and input
        clearButton(rootView)

        submitButton(rootView)

    }
    fun submitButton(view: View) {
        btnSubmit = view.findViewById(R.id.btnSubmit)
        btnSubmit.setOnClickListener{
            if( (vNodeID.error == null) && (vNodeID.text.trim().toString().length > 0)
                && (vTitle.error == null) && (vTitle.text.trim().toString().length > 0)
                && (vDate.error == null) && (vDate.text.trim().toString().length > 0)
                && (vDescription.error == null) && (vDescription.text.trim().toString().length > 0)
            ){
                saveNote()
            }
            else{
//                Toast.makeText(this, "Please enter values", Toast.LENGTH_LONG).show()
                vNodeID.error = "NoteID should not empty"
                vTitle.error = "Title should not empty"
                vDate.error = "Date should not empty"
                vDescription.error = "Description should not empty"
            }
        }
    }
    fun clearButton(view: View) {
        btnClear = view.findViewById(R.id.btnClear)
        btnClear.setOnClickListener{
            resetEmptyfields()
        }
    }
    fun Validation(view: View) {
//        val view =  (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup

        vNodeID = view.findViewById(R.id.noteID)
        vNodeID.addTextChangedListener{
            for(i in 0  until vNodeID.length()){
                if(vNodeID.text.get(i).isLetter() == true ||
                    vNodeID.text.get(i).isDigit() == false) {
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
        vTitle = view.findViewById(R.id.noteTitle)
        val special = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        vTitle.addTextChangedListener{
            var matcher: Matcher = special.matcher(vTitle.text.trim())
            var specialSymbols = matcher.find()
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

        vDateIcon = view.findViewById(R.id.dateIcon)
        vDateIcon.setOnClickListener{
            displayDatePicker()
        }
        vDate = view.findViewById(R.id.noteDate)
        vDate.addTextChangedListener {

            if(!dateValidation() || vDate.text.trim().length > 10){
                vDate.error = "Date input invalid"
            }
            else{
                vDate.setError(null)
            }
        }

        vDescription = view.findViewById(R.id.noteDescription)
        vDescription.addTextChangedListener {
            if(vDescription.text.trim().isEmpty()){
                vDescription.error = "Description should not empty"
            }
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
                Toast.makeText(this, "Note inserted successfully", Toast.LENGTH_LONG).show()
                resetEmptyfields()
            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
    }
     fun resetEmptyfields() {
        vNodeID.text.clear()
        vNodeID.setError(null)

        vTitle.text.clear()
        vTitle.setError(null)

        vDate.text.clear()
        vDate.setError(null)

        vDescription.text.clear()
        vDescription.setError(null)
    }

    //    fun to process DatePicker
     val displayDatePicker = {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth  = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            // on below line we are passing context.
            this,
            { _, year, monthOfYear, dayOfMonth ->
                //  setting date to edit text.
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
    //    Checking any letters in vDate
    fun dateValidation():Boolean{
        val regex = "^[A-Za-z-]*$"
        for(i in 0 until vDate.length()){
            if (vDate.text?.get(i)?.isLetter()==true) {
//                Log.i("parsedDate", vDate.text.toString())
                return false
            }
        }
        val dayPicker = vDate.text.toString().substringBefore("-")
        val d2 = vDate.text.toString().substringAfter("-")
        val monthPicker = d2.substringBefore("-")
        val yearPicker = d2.substringAfter("-")
//        Handle delete day, month, year by keyboard
//        Otherwise system crash
        if(dayPicker.length>0 && monthPicker.length>0 && yearPicker.length>0){
            return isValidDate(dayPicker.toInt(),monthPicker.toInt(),yearPicker.toInt())
        }
        return false
    }
    fun isValidDate(dayValue: Int, monthValue: Int, yearValue: Int): Boolean{
        val maxValidYear = 9999;
        val minValidYear = 1800;
        if (yearValue > maxValidYear ||   yearValue < minValidYear){
            return false;
        }
        if (monthValue < 1 || monthValue > 12){
            return false;
        }
        if (dayValue < 1 || dayValue > 31){
            return false;
        }
        // Handle with leap year
        if (monthValue == 2){
            return if(isLeapYear(yearValue)){
                (dayValue <= 29);
            } else{
                (dayValue <= 28);
            }
        }
        // Months of April, June, Sept and Nov must have number of days less than or equal to 30.
        if (monthValue == 4 || monthValue == 6 ||
            monthValue == 9 || monthValue == 11)
            return (dayValue <= 30);

        return true;
    }
    //    Handle LeapYear
    fun isLeapYear(year: Int):Boolean{
//        check isLeap year
        return (((year % 4 == 0) &&
                (year % 100 != 0)) ||
                (year % 400 == 0));
    }
//    private fun validationProcess() {
//        val NoteID = vNodeID.text.trim().toString()
//        val noteTitle = vTitle.text.trim().toString()
//        val noteDate = vDate.text.trim().toString()
//        val noteDescription = vDescription.text.trim().toString()
//
//
//    }

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
