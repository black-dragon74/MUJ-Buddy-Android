package com.black_dragon74.mujbuddy

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.black_dragon74.mujbuddy.adapters.ContactsAdapter
import com.black_dragon74.mujbuddy.models.ContactsModel
import com.black_dragon74.mujbuddy.utils.API_URL
import com.black_dragon74.mujbuddy.utils.HelperFunctions
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_contacts.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class ContactsActivity : AppCompatActivity() {
    // Shared progress bar instance for this class
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        // Set the instance in global shared var
        this.progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Loading...")
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()

        supportActionBar?.title = "Faculty Contacts"

        // Bind the recycler view
        contactsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Call the function and it'll handle the rest
        populateContactList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.generic_refresh_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.generic_refresh -> {
                progressDialog?.setMessage("Refreshing...")
                progressDialog?.show()
                populateContactList(true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun populateContactList(isRefresh: Boolean = false) {
        val helper = HelperFunctions(this)
        val json = GsonBuilder().create()
        val client = OkHttpClient()

        if (!isRefresh && helper.getContactsFromDB() != null) {
            // Return from the DB
            val contactsInDB = helper.getContactsFromDB()
            val parsed = json.fromJson(contactsInDB, Array<ContactsModel>::class.java)
            runOnUiThread {
                progressDialog?.dismiss()
                contactsRecyclerView.adapter = ContactsAdapter(parsed)
            }
            // Exit
            return
        }

        // Else send the request and try to parse the response
        val user = helper.getUserCredentials() ?: return
        val userid = user.username
        val usertype = user.usertype

        val request = Request.Builder().url("${API_URL}faculties?userid=$userid&usertype=$usertype").build()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressDialog?.dismiss()
                    helper.showToast(this@ContactsActivity, "Failed to fetch contacts")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val respBody = response.body()?.string()
                // Try to parse
                try {
                    val parsed = json.fromJson(respBody, Array<ContactsModel>::class.java)
                    // Save in DB
                    helper.setContactsInDB(respBody!!)

                    // Set the data
                    runOnUiThread {
                        progressDialog?.dismiss()
                        contactsRecyclerView.adapter = ContactsAdapter(parsed)
                    }
                }
                catch (e: Exception) {
                    runOnUiThread {
                        progressDialog?.dismiss()
                        helper.showToast(this@ContactsActivity, e.localizedMessage)
                    }
                }
            }

        })
    }
}
