package com.example.cmsc436groupproject.controller

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cmsc436groupproject.R
import com.example.cmsc436groupproject.StudyGroup
import com.example.cmsc436groupproject.StudyGroupRepository
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.database.DatabaseError

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var groupNameInput: EditText
    private lateinit var subjectInput: EditText
    private lateinit var locationInput: EditText
    private lateinit var timeInput: EditText
    private lateinit var maxSizeInput: EditText
    private var locationSelected: String? = null

    private val studyGroupRepository = StudyGroupRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_group)

        // Initialize UI components
        groupNameInput = findViewById(R.id.group_name)
        subjectInput = findViewById(R.id.subject)
        locationInput = findViewById(R.id.location)
        timeInput = findViewById(R.id.time)
        maxSizeInput = findViewById(R.id.max_size)

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "YOUR_API_KEY_HERE")
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

            // Create the group object and add via repository
            val group = StudyGroup(groupName, subject, locationSelected!!, time, maxSize)
            studyGroupRepository.addGroup(group, object : StudyGroupRepository.AddGroupCallback {
                override fun onGroupAdded(success: Boolean) {
                    if (success) {
                        Toast.makeText(this@CreateGroupActivity, "Group created successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@CreateGroupActivity, "Failed to create group", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val place = Autocomplete.getPlaceFromIntent(data)
            locationSelected = place.address
            locationInput.setText(locationSelected)
            Toast.makeText(this, "Location selected: $locationSelected", Toast.LENGTH_SHORT).show()
        }
    }
}
