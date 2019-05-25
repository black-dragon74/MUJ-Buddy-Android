package com.black_dragon74.mujbuddy

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.black_dragon74.mujbuddy.adapters.EventsAdapter
import com.black_dragon74.mujbuddy.adapters.GPAAdapter
import com.black_dragon74.mujbuddy.models.ErrorModel
import com.black_dragon74.mujbuddy.models.EventsModel
import com.black_dragon74.mujbuddy.models.GPAModel
import com.black_dragon74.mujbuddy.utils.API_URL
import com.black_dragon74.mujbuddy.utils.HelperFunctions
import com.black_dragon74.mujbuddy.utils.LOGIN_FAILED
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_events.*
import kotlinx.android.synthetic.main.activity_gpa.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class EventsActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null
    private var mAdapter: EventsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        this.progressDialog = ProgressDialog(this, R.style.DarkProgressDialog)
        progressDialog?.setMessage("Loading...")
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()

        supportActionBar?.title = "Upcoming Events"

        eventsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Call the function
        populateEvents()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.generic_refresh_menu, menu)
        menuInflater.inflate(R.menu.search, menu)

        val item = menu?.findItem(R.id.menu_search)
        val s = item?.actionView as SearchView
        s.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                mAdapter?.filter?.filter(p0)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                mAdapter?.filter?.filter(p0)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.generic_refresh -> {
                progressDialog?.setMessage("Refreshing...")
                progressDialog?.show()
                populateEvents(true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun populateEvents(isRefresh: Boolean = false) {
        // Init GSON and the Requests and the helper class
        val helper = HelperFunctions(this)
        val client = OkHttpClient()
        val json = GsonBuilder().create()

        // If not refresh call and data exists, return that and exit
        if (!isRefresh && helper.getEventsFromDB() != null) {
            val eventsInDB = helper.getEventsFromDB()
            val parsedData = json.fromJson(eventsInDB, Array<EventsModel>::class.java)

            // Set the adapter for the recycler view on the main thread also dismiss the progress dialog
            runOnUiThread {
                progressDialog?.dismiss()
                mAdapter = EventsAdapter(parsedData)
                eventsRecyclerView.adapter = mAdapter
            }

            // Coz, Kuntal Mam says, exit jaroori hai
            return
        }

        // Else we will send the URL request
        val sessionID = helper.getSessionID()

        if (sessionID.isNullOrEmpty()) {
            helper.showToast(this, "Invalid request, access denied.")
            return
        }

        val request = Request.Builder().url("${API_URL}events?sessionid=$sessionID").build()

        // Dispatch the request
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressDialog?.dismiss()
                    helper.showToast(this@EventsActivity, "Unable to fetch Events!")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val respBody = response.body()?.string()
                // Try to decode using the GPA MODEL
                try {
                    val parsedEvents = json.fromJson(respBody, Array<EventsModel>::class.java)

                    // If we are here means the JSON was parsed sucessfully, save in the DB too
                    helper.setEventsInDB(respBody!!)

                    // Set the adapter and dismiss the dialog
                    runOnUiThread {
                        progressDialog?.dismiss()
                        mAdapter = EventsAdapter(parsedEvents)
                        eventsRecyclerView.adapter = mAdapter
                    }
                }
                catch (e: Exception) {
                    // Try and see if the reponse conforms to error model type
                    try {
                        val parsed = json.fromJson(respBody, ErrorModel::class.java)
                        if (parsed.error == LOGIN_FAILED) {
                            // Do something to reneew the login credentials
                            progressDialog?.dismiss()
                            val intent = Intent(this@EventsActivity, LoginActivity::class.java)
                            intent.putExtra("reauth", true)
                            startActivity(intent)
                        }
                        else {
                            // Just show the error message
                            runOnUiThread {
                                helper.showToast(this@EventsActivity, parsed.error)
                            }
                        }
                    }

                    catch (e: Exception) {
                        runOnUiThread {
                            helper.showToast(this@EventsActivity, e.message ?: "Unable to show expcetion")
                        }
                    }
                }
            }

        })
    }
}
