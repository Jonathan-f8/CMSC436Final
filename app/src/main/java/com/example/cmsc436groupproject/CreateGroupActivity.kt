package com.example.cmsc436groupproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var groupNameInput: EditText
    private lateinit var subjectInput: EditText
    private lateinit var locationInput: EditText
    private lateinit var timeInput: EditText
    private lateinit var maxSizeInput: EditText
    private var locationSelected: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_group)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference

        // Initialize UI components
        groupNameInput = findViewById(R.id.group_name)
        subjectInput = findViewById(R.id.subject)
        locationInput = findViewById(R.id.location)
        timeInput = findViewById(R.id.time)
        maxSizeInput = findViewById(R.id.max_size)

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyAzw31iuVjsRWwcbffDDTPi12RqWiZHH_U") // Replace with your API key
        }

        // Set up Places Autocomplete for location field
        locationInput.setOnClickListener {
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this)
            startActivityForResult(intent, 1)
        }

        val createGroupButton: Button = findViewById(R.id.create_group_button)
        createGroupButton.setOnClickListener {
            val groupName = groupNameInput.text.toString().trim()
            val subject = subjectInput.text.toString().trim()
            val time = timeInput.text.toString().trim()
            val maxSize = maxSizeInput.text.toString().trim().toIntOrNull()

            if (groupName.isEmpty() || subject.isEmpty() || locationSelected.isNullOrEmpty() || time.isEmpty() || maxSize == null) {
                Toast.makeText(this, "Please fill out all fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save the group to Firebase
            val group = StudyGroup(groupName, subject, locationSelected!!, time, maxSize)
            database.child("groups").push().setValue(group)
                .addOnSuccessListener {
                    Toast.makeText(this, "Group created successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(data!!)
            locationSelected = place.address
            locationInput.setText(locationSelected)
            Toast.makeText(this, "Location selected: $locationSelected", Toast.LENGTH_SHORT).show()
        }
    }

    // Data class to represent a Group
    data class StudyGroup(
        val groupName: String,
        val subject: String,
        val location: String,
        val time: String,
        val maxSize: Int
    )
}
