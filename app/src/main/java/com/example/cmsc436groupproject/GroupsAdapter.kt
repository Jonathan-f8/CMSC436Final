package com.example.cmsc436groupproject.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cmsc436groupproject.R
import com.example.cmsc436groupproject.StudyGroup

class GroupsAdapter(private val groupsList: List<StudyGroup>) :
    RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.group_name2)
        val groupSubject: TextView = itemView.findViewById(R.id.group_subject)
        val groupLocation: TextView = itemView.findViewById(R.id.group_location)
        val groupTime: TextView = itemView.findViewById(R.id.group_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_item, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupsList[position]
        holder.groupName.text = group.groupName
        holder.groupSubject.text = group.subject
        holder.groupLocation.text = group.location
        holder.groupTime.text = group.time
    }

    override fun getItemCount(): Int {
        return groupsList.size
    }
}
