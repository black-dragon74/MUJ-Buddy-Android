package com.black_dragon74.mujbuddy.adapters

import android.support.v7.widget.RecyclerView
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
        parent.view.attendanceSectionTF.text = if (currentAttendance.section.isEmpty()) "NA" else currentAttendance.section
        parent.view.attendanceBatchTF.text = if (currentAttendance.batch.isEmpty()) "NA" else currentAttendance.batch
        parent.view.attendancePresentTF.text = if (currentAttendance.present.isEmpty()) "NA" else currentAttendance.present
        parent.view.attendanceTotalTF.text = currentAttendance.total
        parent.view.attendanceAbsentTF.text = if (currentAttendance.absent.isEmpty()) "NA" else currentAttendance.absent
        parent.view.attendancePercentageTF.text = if (currentAttendance.total == "0") "0 %" else currentAttendance.percentage + " %"
    }

}

class AttendanceViewHolder(val view: View): RecyclerView.ViewHolder(view)
