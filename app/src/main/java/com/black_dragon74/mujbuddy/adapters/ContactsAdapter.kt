package com.black_dragon74.mujbuddy.adapters

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.black_dragon74.mujbuddy.ContactInfoActivity
import com.black_dragon74.mujbuddy.R
import com.black_dragon74.mujbuddy.models.ContactsModel
import kotlinx.android.synthetic.main.contacts_row.view.*

class ContactsAdapter(val contactList: Array<ContactsModel>): RecyclerView.Adapter<ContactsViewHolder>(), Filterable {
    private var contactSearchList: MutableList<ContactsModel>? = null

    init {
        this.contactSearchList = contactList.toMutableList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val contactLayout = inflater.inflate(R.layout.contacts_row, parent, false)
        return ContactsViewHolder(contactLayout)
    }

    override fun getItemCount(): Int {
        return contactSearchList!!.size
    }

    override fun onBindViewHolder(parent: ContactsViewHolder, position: Int) {
        parent.view.contactTeacherName.text = contactSearchList!![position].name
        parent.view.contactTeacherDepartment.text = contactSearchList!![position].department
        parent.currentFaculty = contactSearchList!![position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                if (charString.isEmpty()) {
                    contactSearchList = contactList.toMutableList()
                } else {
                    val filteredList = ArrayList<ContactsModel>()
                    for (row in contactList) {
                        if (row.name.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    contactSearchList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = contactSearchList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                contactSearchList = results?.values as MutableList<ContactsModel>
                notifyDataSetChanged()
            }
        }
    }
}

class ContactsViewHolder(val view: View, var currentFaculty: ContactsModel? = null): RecyclerView.ViewHolder(view) {

    // Static memers, will be tossed and passed around in Intents
    companion object {
        val FACULTY_NAME = "faculty_name"
        val FACULTY_IMAGE = "faculty_image"
        val FACULTY_EMAIL = "faculty_email"
        val FACULTY_PHONE = "faculty_phone"
        val FACULTY_DEPARTMENT = "faculy_department"
        val FACULTY_DESIGNATION = "Faculty_designation"
    }

    init {
        view.setOnClickListener {
            val contactsInfoIntent = Intent(view.context, ContactInfoActivity::class.java)
            contactsInfoIntent.putExtra(FACULTY_NAME, currentFaculty?.name)
            contactsInfoIntent.putExtra(FACULTY_IMAGE, currentFaculty?.image)
            contactsInfoIntent.putExtra(FACULTY_EMAIL, currentFaculty?.email)
            contactsInfoIntent.putExtra(FACULTY_PHONE, currentFaculty?.phone)
            contactsInfoIntent.putExtra(FACULTY_DEPARTMENT, currentFaculty?.department)
            contactsInfoIntent.putExtra(FACULTY_DESIGNATION, currentFaculty?.designation)
            view.context.startActivity(contactsInfoIntent)
        }
    }
}