package com.black_dragon74.mujbuddy.adapters

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.black_dragon74.mujbuddy.ContactInfoActivity
import com.black_dragon74.mujbuddy.R

class ContactsAdapter: RecyclerView.Adapter<ContactsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val contactLayout = inflater.inflate(R.layout.contacts_row, parent, false)
        return ContactsViewHolder(contactLayout)
    }

    override fun getItemCount(): Int {
        return 100
    }

    override fun onBindViewHolder(parent: ContactsViewHolder, position: Int) {

    }

}

class ContactsViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    init {
        view.setOnClickListener {
            val intent = Intent(view.context, ContactInfoActivity::class.java)
            startActivity(view.context, intent, null)
        }
    }
}