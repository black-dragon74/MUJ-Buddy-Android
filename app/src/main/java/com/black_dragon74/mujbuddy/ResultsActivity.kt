package com.black_dragon74.mujbuddy

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.black_dragon74.mujbuddy.adapters.ResultsAdapter
import com.black_dragon74.mujbuddy.models.ResultsModel
import com.black_dragon74.mujbuddy.utils.API_URL
import com.black_dragon74.mujbuddy.utils.HelperFunctions
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_results.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class ResultsActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // Pupulate the progress bar instance
        this.progressDialog = ProgressDialog(this, R.style.DarkProgressDialog)
        progressDialog?.setMessage("Loading...")
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()

        // Set the title
        supportActionBar?.title = "Results"

        // Render the view
        resultsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch the results and show it here
        populateResults()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.generic_refresh_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.generic_refresh -> {
                // Handle refresh
                progressDialog?.setMessage("Refreshing....")
                progressDialog?.show()
                populateResults(true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun populateResults(isRefresh: Boolean = false) {
        val helper = HelperFunctions(this)
        val json = GsonBuilder().setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        val client = OkHttpClient()

        if (!isRefresh && helper.getResultsFromDB() != null) {
            // Populate from db
            val resultsFromDB = helper.getResultsFromDB()
            val parsed = json.fromJson(resultsFromDB, Array<ResultsModel>::class.java)
            runOnUiThread {
                progressDialog?.dismiss()
                resultsRecyclerView.adapter = ResultsAdapter(parsed)
            }

            return
        }

        // Else fire a request
        val sessionID = helper.getSessionID()

        if (sessionID.isNullOrEmpty()) {
            helper.showToast(this, "Invalid request, access denied.")
            return
        }

        val request = Request.Builder().url("${API_URL}results?sessionid=$sessionID&semester=${helper.getCurrentSemester()}").build()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressDialog?.dismiss()
                    helper.showToast(this@ResultsActivity, "Unable to get results for Semester: ${helper.getCurrentSemester()}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val respBody = response.body()?.string()
                try {
                    val parsed = json.fromJson(respBody, Array<ResultsModel>::class.java)
                    helper.setResultInDB(respBody!!)
                    runOnUiThread {
                        progressDialog?.dismiss()
                        resultsRecyclerView.adapter = ResultsAdapter(parsed)
                    }
                }
                catch (_: Exception) {
                    runOnUiThread {
                        progressDialog?.dismiss()
                        helper.showToast(this@ResultsActivity, "Unable to get results for Semester: ${helper.getCurrentSemester()}")
                    }
                }
            }

        })
    }
}
