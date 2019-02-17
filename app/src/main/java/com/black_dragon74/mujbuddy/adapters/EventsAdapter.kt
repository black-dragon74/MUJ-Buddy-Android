package com.black_dragon74.mujbuddy.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.black_dragon74.mujbuddy.R
import com.black_dragon74.mujbuddy.models.EventsModel
import kotlinx.android.synthetic.main.events_row.view.*

class EventsAdapter(private val eventsData: Array<EventsModel>): RecyclerView.Adapter<EventsViewHolder>() {
    // Reverse the array to show the list in ascending order
    init {
        eventsData.reverse()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.events_row, parent, false)
        return EventsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return eventsData.size
    }

    override fun onBindViewHolder(parent: EventsViewHolder, position: Int) {
        val currentEvent = eventsData[position]
        parent.view.eventsNameTF.text = currentEvent.name
        parent.view.eventsDateTF.text = currentEvent.date
    }

}

class EventsViewHolder(val view: View): RecyclerView.ViewHolder(view)