package com.black_dragon74.mujbuddy

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.black_dragon74.mujbuddy.adapters.EventsAdapter
import com.black_dragon74.mujbuddy.adapters.GPAAdapter
import com.black_dragon74.mujbuddy.models.EventsModel
import com.black_dragon74.mujbuddy.models.GPAModel
import com.black_dragon74.mujbuddy.utils.API_URL
import com.black_dragon74.mujbuddy.utils.HelperFunctions
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_events.*
import kotlinx.android.synthetic.main.activity_gpa.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class EventsActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        this.progressDialog = ProgressDialog(this)
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
                eventsRecyclerView.adapter = EventsAdapter(parsedData)
            }

            // Coz, Kuntal Mam says, exit jaroori hai
            return
        }

        // Else we will send the URL request
        val user = helper.getUserCredentials() ?: return
        val userid = user.username
        val usertype = user.usertype

        val request = Request.Builder().url("${API_URL}events?userid=$userid&usertype=$usertype").build()

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
                        eventsRecyclerView.adapter = EventsAdapter(parsedEvents)
                    }
                }
                catch (e: Exception) {
                    runOnUiThread {
                        progressDialog?.dismiss()
                        helper.showToast(this@EventsActivity, "Unable to parse events")
                    }
                }
            }

        })
    }
}
