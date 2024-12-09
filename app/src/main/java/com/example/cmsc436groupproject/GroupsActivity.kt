package com.example.cmsc436groupproject

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class GroupsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var database: DatabaseReference
    private lateinit var groupsList: MutableList<String>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.groups)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().getReference("groups")

        // Initialize ListView
        listView = findViewById(R.id.groups_list_view)
        groupsList = mutableListOf()

        // Get SharedPreferences
        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE)

        // Load groups from Firebase
        loadGroups()
    }

    private fun loadGroups() {
        val preferredLocation = sharedPreferences.getString("preferredLocation", "").orEmpty()
        val preferredGroupSize = sharedPreferences.getInt("preferredGroupSize", 3)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupsList.clear()
                for (groupSnapshot in snapshot.children) {
                    val group = groupSnapshot.getValue(StudyGroup::class.java)
                    if (group != null) {
                        if ((preferredLocation.isBlank() || group.location.equals(preferredLocation, ignoreCase = true)) &&
                            group.maxSize <= preferredGroupSize) {
                            groupsList.add("${group.groupName}: ${group.subject} at ${group.location} (${group.time})")
                        }
                    }
                }

                val adapter = ArrayAdapter(this@GroupsActivity, android.R.layout.simple_list_item_1, groupsList)
                listView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GroupsActivity, "Failed to load groups", Toast.LENGTH_SHORT).show()
            }
        })
    }
}