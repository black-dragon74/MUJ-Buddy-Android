package com.black_dragon74.mujbuddy.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.black_dragon74.mujbuddy.R

class GPAAdapter: RecyclerView.Adapter<GPAViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GPAViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val gpaCell = inflater.inflate(R.layout.gpa_row, parent, false)
        return GPAViewHolder(gpaCell)
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(parent: GPAViewHolder, postion: Int) {
        // We will do this later's
    }

}


class GPAViewHolder(val view: View): RecyclerView.ViewHolder(view)