package com.black_dragon74.mujbuddy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.black_dragon74.mujbuddy.R
import kotlinx.android.synthetic.main.mess_menu_row.view.*

class MessMenuAdapter(val menu: Array<String>) : RecyclerView.Adapter<MessMenuViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessMenuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.mess_menu_row, parent,false)
        return MessMenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menu.count()
    }

    override fun onBindViewHolder(holder: MessMenuViewHolder, position: Int) {
        // Dequeue cell here
        holder.view.menuNameTF.text = menu[position]
    }
}


class MessMenuViewHolder(val view: View) : RecyclerView.ViewHolder(view)