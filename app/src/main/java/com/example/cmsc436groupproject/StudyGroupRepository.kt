package com.example.cmsc436groupproject

import com.google.firebase.database.*

// A repository class to handle reading and writing StudyGroups from Firebase.
class StudyGroupRepository {

    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("groups")

    interface GroupsCallback {
        fun onGroupsLoaded(groups: List<StudyGroup>)
        fun onError(error: DatabaseError)
    }

    interface AddGroupCallback {
        fun onGroupAdded(success: Boolean)
    }

    fun addGroup(group: StudyGroup, callback: AddGroupCallback) {
        databaseRef.push().setValue(group)
            .addOnSuccessListener { callback.onGroupAdded(true) }
            .addOnFailureListener { callback.onGroupAdded(false) }
    }

    fun getGroups(preferredLocation: String, preferredGroupSize: Int, callback: GroupsCallback) {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groupsList = mutableListOf<StudyGroup>()
                for (groupSnapshot in snapshot.children) {
                    val group = groupSnapshot.getValue(StudyGroup::class.java)
                    if (group != null) {
                        if ((preferredLocation.isBlank() || group.location.equals(preferredLocation, ignoreCase = true))
                            && group.maxSize <= preferredGroupSize
                        ) {
                            groupsList.add(group)
                        }
                    }
                }
                callback.onGroupsLoaded(groupsList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onError(error)
            }
        })
    }
}
