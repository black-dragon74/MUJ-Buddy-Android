package com.black_dragon74.mujbuddy

import android.app.ProgressDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.black_dragon74.mujbuddy.adapters.AttendanceAdapter
import com.black_dragon74.mujbuddy.models.AttendanceModel
import com.black_dragon74.mujbuddy.utils.API_URL
import com.black_dragon74.mujbuddy.utils.HelperFunctions
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_attendance.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class AttendanceActivity : AppCompatActivity() {

    private var context: Context? = null
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        this.context = applicationContext
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        // Change the title
        supportActionBar?.title = "Attendance"

        // Show the progress loader
        this.progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Loading...")
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()

        // Bind the recycler view
        attendanceRecyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch and populate the attendance
        populateAttendance(false)
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
                populateAttendance(true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun populateAttendance(isRefresh: Boolean) {
        val helper = HelperFunctions(this)
        val gson = GsonBuilder().create()
        if (!isRefresh) {
            // Return the data from the DB itself
            val dbAttendance = helper.getAttendanceFromDB()
            if (dbAttendance != null) {
                val parsedDbAttendance = gson.fromJson(dbAttendance, Array<AttendanceModel>::class.java)
                runOnUiThread {
                    progressDialog?.dismiss()
                    attendanceRecyclerView.adapter = AttendanceAdapter(parsedDbAttendance)
                }
                return
            }
        }

        // Else we will launch a HTTP request and will return the data from there
        val client = OkHttpClient()
        val user = helper.getUserCredentials() ?: return
        val userid = user.username
        val usertype = user.usertype

        val request = Request.Builder().url("${API_URL}attendance?userid=$userid&usertype=$usertype").build()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                println("Http request failed")
                runOnUiThread {
                    progressDialog?.dismiss()
                    helper.showToast(this@AttendanceActivity, "Failed to refresh")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val respBody = response.body()?.string()
                runOnUiThread { progressDialog?.dismiss() }
                try {
                    val parsedResp = gson.fromJson(respBody, Array<AttendanceModel>::class.java)
                    // If attendance is paresed successfully, update in the DB
                    helper.updateAttendanceInDB(respBody!!)
                    runOnUiThread { attendanceRecyclerView.adapter = AttendanceAdapter(parsedResp) }
                }
                catch (e: Exception) {
                    runOnUiThread {
                        helper.showToast(this@AttendanceActivity, e.message ?: "Unable to show expcetion")
                    }
                }
            }

        })
    }
}
