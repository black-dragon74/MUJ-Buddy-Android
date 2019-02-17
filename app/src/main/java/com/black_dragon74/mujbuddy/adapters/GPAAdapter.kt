package com.black_dragon74.mujbuddy.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.black_dragon74.mujbuddy.R
import com.black_dragon74.mujbuddy.models.GPAModel
import kotlinx.android.synthetic.main.gpa_row.view.*

class GPAAdapter(val gpaData: GPAModel): RecyclerView.Adapter<GPAViewHolder>() {
    var gpaToRender: MutableMap<String, String> = mutableMapOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GPAViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val gpaCell = inflater.inflate(R.layout.gpa_row, parent, false)
        return GPAViewHolder(gpaCell)
    }

    override fun getItemCount(): Int {
        var count = 0
        // Coz DMS sucks, right?
        if (gpaData.semester_1 != null) {
            count++
            gpaToRender["Semester 1"] = gpaData.semester_1
        }
        if (gpaData.semester_2 != null) {
            count++
            gpaToRender["Semester 2"] = gpaData.semester_2
        }
        if (gpaData.semester_3 != null) {
            count++
            gpaToRender["Semester 3"] = gpaData.semester_3
        }
        if (gpaData.semester_4 != null) {
            count++
            gpaToRender["Semester 4"] = gpaData.semester_4
        }
        if (gpaData.semester_5 != null) {
            count++
            gpaToRender["Semester 5"] = gpaData.semester_5
        }
        if (gpaData.semester_6 != null) {
            count++
            gpaToRender["Semester 6"] = gpaData.semester_6
        }
        if (gpaData.semester_7 != null) {
            count++
            gpaToRender["Semester 7"] = gpaData.semester_7
        }
        if (gpaData.semester_8 != null) {
            count++
            gpaToRender["Semester 8"] = gpaData.semester_8
        }

        return count
    }

    override fun onBindViewHolder(parent: GPAViewHolder, postion: Int) {
        var i = 0
        val key = mutableListOf<String>()
        gpaToRender.keys.forEach {
            key.add(i, it)
            i++
        }
        parent.view.gpaSemesterTF.text = key[postion]
        parent.view.gpaGPATF.text = gpaToRender[key[postion]]
    }

}


class GPAViewHolder(val view: View): RecyclerView.ViewHolder(view)