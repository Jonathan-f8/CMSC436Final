package com.example.cmsc436groupproject.controller

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cmsc436groupproject.R
import com.example.cmsc436groupproject.StudyGroup
import com.example.cmsc436groupproject.StudyGroupRepository
import com.google.firebase.database.DatabaseError

class GroupsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferredLocation : String
    private var preferredGroupSize : Int = 0
    private lateinit var studyGroupRepository: StudyGroupRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.groups)

        // Initialize ListView
        listView = findViewById(R.id.groups_list_view)

        // Get SharedPreferences
        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE)
        preferredLocation = sharedPreferences.getString("preferredLocation", "").orEmpty()
        preferredGroupSize = sharedPreferences.getInt("preferredGroupSize", 3)

        studyGroupRepository = StudyGroupRepository()
        loadGroups()
    }

    private fun loadGroups() {
        studyGroupRepository.getGroups(preferredLocation, preferredGroupSize, object : StudyGroupRepository.GroupsCallback {
            override fun onGroupsLoaded(groups: List<StudyGroup>) {
                val groupsList = groups.map { "${it.groupName}: ${it.subject} at ${it.location} (${it.time})" }
                val adapter = ArrayAdapter(this@GroupsActivity, android.R.layout.simple_list_item_1, groupsList)
                listView.adapter = adapter
            }

            override fun onError(error: DatabaseError) {
                Toast.makeText(this@GroupsActivity, "Failed to load groups", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
