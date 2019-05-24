package com.black_dragon74.mujbuddy.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.black_dragon74.mujbuddy.R
import com.black_dragon74.mujbuddy.models.ResultsModel
import kotlinx.android.synthetic.main.results_row.view.*

class ResultsAdapter(val resultData: Array<ResultsModel>): RecyclerView.Adapter<ResultsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.results_row, parent, false)
        return ResultsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return resultData.size
    }

    override fun onBindViewHolder(parent: ResultsViewHolder, position: Int) {
        // Laters
        val currentSub = resultData[position]
        parent.view.resultSubjectTF.text = currentSub.courseName
        parent.view.resultCourseCodeTF.text = currentSub.courseCode
        parent.view.resultCreditsTF.text = currentSub.credits
        parent.view.resultGradeTF.text = currentSub.grade
    }

}

class ResultsViewHolder(val view: View): RecyclerView.ViewHolder(view)