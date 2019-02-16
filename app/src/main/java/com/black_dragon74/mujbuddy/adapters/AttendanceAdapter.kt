package com.black_dragon74.mujbuddy.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.black_dragon74.mujbuddy.R
import kotlinx.android.synthetic.main.attendance_row.view.*
import kotlinx.android.synthetic.main.menu_row.view.*

class AttendanceAdapter: RecyclerView.Adapter<AttendanceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.attendance_row, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(parent: AttendanceViewHolder, positon: Int) {

    }

}

class AttendanceViewHolder(val view: View): RecyclerView.ViewHolder(view)
