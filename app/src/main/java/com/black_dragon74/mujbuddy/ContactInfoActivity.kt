package com.black_dragon74.mujbuddy

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.black_dragon74.mujbuddy.adapters.ContactsViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_contact_info.*

class ContactInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_info)

        // Extract the values from the intent
        val intent = intent
        val name = intent.getStringExtra(ContactsViewHolder.FACULTY_NAME)
        val email = intent.getStringExtra(ContactsViewHolder.FACULTY_EMAIL)
        val phone = intent.getStringExtra(ContactsViewHolder.FACULTY_PHONE)
        val designation = intent.getStringExtra(ContactsViewHolder.FACULTY_DESIGNATION)
        val department = intent.getStringExtra(ContactsViewHolder.FACULTY_DEPARTMENT)
        val image = intent.getStringExtra(ContactsViewHolder.FACULTY_IMAGE)

        // Set the title to the faculty's name
        supportActionBar?.title = name

        // Set the values in the view
        cinfoName.text = name
        cinfoEmail.text = if (email.isEmpty()) "NA" else email
        cinfoPhone.text = if (phone.isEmpty()) "NA" else phone
        cinfoDesignation.text = designation
        cinfoDepartment.text = if (department.isEmpty()) "NA" else department
        Picasso.get().load(image).into(cinfoImage)

        cinfoPhone.setOnClickListener {
            callFaculty(cinfoPhone.text.toString())
        }

        cinfoEmail.setOnClickListener {
            emailFaculty(cinfoEmail.text.toString())
        }

        cinfophone_icon.setOnClickListener {
            callFaculty(cinfoPhone.text.toString())
        }

        cinfoemail_icon.setOnClickListener {
            emailFaculty(cinfoEmail.text.toString())
        }
    }

    private fun callFaculty(cinfoPhone: String?) {
        if (cinfoPhone != null && cinfoPhone != "NA") {
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + cinfoPhone.trim()))
            startActivity(callIntent)
        }
    }

    private fun emailFaculty(cinfoEmail: String?) {
        if (cinfoEmail != null && cinfoEmail != "NA") {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + cinfoEmail.trim()))
            startActivity(Intent.createChooser(emailIntent, "Email faculty using"))
        }
    }
}
