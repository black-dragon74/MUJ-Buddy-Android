package com.black_dragon74.mujbuddy.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.black_dragon74.mujbuddy.R
import com.black_dragon74.mujbuddy.models.AttendanceModel
import kotlinx.android.synthetic.main.attendance_row.view.*
import kotlinx.android.synthetic.main.menu_row.view.*

class AttendanceAdapter(private val attendanceModel: Array<AttendanceModel>): RecyclerView.Adapter<AttendanceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.attendance_row, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return attendanceModel.size
    }

    override fun onBindViewHolder(parent: AttendanceViewHolder, positon: Int) {
        val currentAttendance = attendanceModel[positon]
        parent.view.attendanceSubjectTF.text = currentAttendance.course
        parent.view.attendancePresentTF.text = if (currentAttendance.present.isEmpty()) "NA" else currentAttendance.present
        parent.view.attendanceTotalTF.text = "Total - ${currentAttendance.total}"
        parent.view.attendanceAbsentTF.text = if (currentAttendance.absent.isEmpty()) "NA" else currentAttendance.absent
        parent.view.attendancePercentageTF.text = if (currentAttendance.percentage.isEmpty()) "NA" else currentAttendance.percentage
    }

}

class AttendanceViewHolder(val view: View): RecyclerView.ViewHolder(view)
