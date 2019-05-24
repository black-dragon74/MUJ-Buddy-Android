package com.black_dragon74.mujbuddy.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.black_dragon74.mujbuddy.R
import com.black_dragon74.mujbuddy.models.EventsModel
import kotlinx.android.synthetic.main.events_row.view.*

class EventsAdapter(private val eventsData: Array<EventsModel>): RecyclerView.Adapter<EventsViewHolder>(), Filterable {
    private var filteredEvents: MutableList<EventsModel>? = null

    // Reverse the array to show the list in ascending order
    init {
        eventsData.reverse()
        filteredEvents = eventsData.toMutableList()
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchString = constraint.toString()
                if (searchString.isEmpty()) {
                    filteredEvents = eventsData.toMutableList()
                }
                else {
                    // Perform the filtering
                    val filtered = ArrayList<EventsModel>()
                    for (row in eventsData) {
                        if (row.name.toLowerCase().contains(searchString)) {
                            filtered.add(row)
                        }
                    }
                    filteredEvents = filtered
                }

                val returnResults = FilterResults()
                returnResults.values = filteredEvents
                return returnResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredEvents = results?.values as MutableList<EventsModel>
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.events_row, parent, false)
        return EventsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredEvents!!.size
    }

    override fun onBindViewHolder(parent: EventsViewHolder, position: Int) {
        val currentEvent = filteredEvents!![position]
        parent.view.eventsNameTF.text = currentEvent.name
        parent.view.eventsDateTF.text = currentEvent.date
    }

}

class EventsViewHolder(val view: View): RecyclerView.ViewHolder(view)