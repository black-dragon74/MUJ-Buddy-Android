package com.black_dragon74.mujbuddy

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.black_dragon74.mujbuddy.adapters.ContactsAdapter
import com.black_dragon74.mujbuddy.models.ContactsModel
import com.black_dragon74.mujbuddy.models.ErrorModel
import com.black_dragon74.mujbuddy.utils.API_URL
import com.black_dragon74.mujbuddy.utils.HelperFunctions
import com.black_dragon74.mujbuddy.utils.LOGIN_FAILED
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_contacts.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class ContactsActivity : AppCompatActivity() {
    // Shared progress bar instance for this class
    private var progressDialog: ProgressDialog? = null

    private var mAdapter: ContactsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        // Set the instance in global shared var
        this.progressDialog = ProgressDialog(this, R.style.DarkProgressDialog)
        progressDialog?.setMessage("Loading...")
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()

        supportActionBar?.title = "Faculty Contacts"

        // Bind the recycler view
        contactsRecyclerView.layoutManager =
            LinearLayoutManager(this)

        contactsRecyclerView.adapter = mAdapter

        // Call the function and it'll handle the rest
        populateContactList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.generic_refresh_menu, menu)
        menuInflater.inflate(R.menu.search, menu)

        val item = menu?.findItem(R.id.menu_search)
        val s: SearchView = item?.actionView as SearchView
        s.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                mAdapter!!.filter.filter(p0)
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                mAdapter!!.filter.filter(p0)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
                mAdapter = ContactsAdapter(parsed)
                contactsRecyclerView.adapter = mAdapter
            }
            // Exit
            return
        }

        // Else send the request and try to parse the response
        val sessionID = helper.getSessionID()

        if (sessionID.isNullOrEmpty()) {
            helper.showToast(this, "Invalid request, access denied.")
            return
        }

        val request = Request.Builder().url("${API_URL}contacts?sessionid=$sessionID").build()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressDialog?.dismiss()
                    helper.showToast(this@ContactsActivity, "Failed to fetch contacts")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val respBody = response.body?.string()
                // Try to parse
                try {
                    val parsed = json.fromJson(respBody, Array<ContactsModel>::class.java)
                    // Save in DB
                    helper.setContactsInDB(respBody!!)

                    // Set the data
                    runOnUiThread {
                        progressDialog?.dismiss()
                        mAdapter = ContactsAdapter(parsed)
                        contactsRecyclerView.adapter = mAdapter
                    }
                }
                catch (e: Exception) {
                    // Try and see if the reponse conforms to error model type
                    try {
                        val parsed = json.fromJson(respBody, ErrorModel::class.java)
                        if (parsed.error == LOGIN_FAILED) {
                            // Do something to reneew the login credentials
                            progressDialog?.dismiss()
                            val intent = Intent(this@ContactsActivity, LoginActivity::class.java)
                            intent.putExtra("reauth", true)
                            startActivity(intent)
                        }
                        else {
                            // Just show the error message
                            runOnUiThread {
                                helper.showToast(this@ContactsActivity, parsed.error)
                                progressDialog?.dismiss()
                            }
                        }
                    }

                    catch (e: Exception) {
                        runOnUiThread {
                            helper.showToast(this@ContactsActivity, e.message ?: "Unable to show expcetion")
                        }
                    }
                }
            }

        })
    }
}
