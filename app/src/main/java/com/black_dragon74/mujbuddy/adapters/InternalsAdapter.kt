package com.black_dragon74.mujbuddy.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.black_dragon74.mujbuddy.R
import com.black_dragon74.mujbuddy.models.InternalsModel
import kotlinx.android.synthetic.main.internals_row.view.*

class InternalsAdapter(val internalsData: Array<InternalsModel>): RecyclerView.Adapter<InternalsViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): InternalsViewHolder {
        val inflater = LayoutInflater.from(p0.context)
        val view = inflater.inflate(R.layout.internals_row, p0, false)
        return InternalsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return internalsData.size
    }

    override fun onBindViewHolder(p0: InternalsViewHolder, p1: Int) {
        val currentSub = internalsData[p1]
        p0.view.internalSubTF.text = currentSub.subject_codes
        p0.view.internalMTE1TF.text = if (currentSub.mte_1.isNullOrEmpty()) "00.00" else currentSub.mte_1
        p0.view.internalMTE2TF.text = if (currentSub.mte_2.isNullOrEmpty()) "00.00" else currentSub.mte_2
        p0.view.internalCWSTF.text = if (currentSub.cws.isNullOrEmpty()) "00.00" else currentSub.cws
        p0.view.internalPRSTF.text = if (currentSub.prs.isNullOrEmpty()) "00.00" else currentSub.prs
        p0.view.internalTotalTF.text = if (currentSub.total.isNullOrEmpty()) "00.00" else currentSub.total
        p0.view.internalResessTF.text = if (currentSub.resession.isNullOrEmpty()) "00.00" else currentSub.resession
    }

}

class InternalsViewHolder(val view: View): RecyclerView.ViewHolder(view)