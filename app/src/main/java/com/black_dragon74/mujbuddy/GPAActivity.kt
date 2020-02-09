package com.black_dragon74.mujbuddy

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.black_dragon74.mujbuddy.adapters.GPAAdapter
import com.black_dragon74.mujbuddy.models.ErrorModel
import com.black_dragon74.mujbuddy.models.GPAModel
import com.black_dragon74.mujbuddy.utils.API_URL
import com.black_dragon74.mujbuddy.utils.HelperFunctions
import com.black_dragon74.mujbuddy.utils.LOGIN_FAILED
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_gpa.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import com.google.gson.JsonParseException
import java.lang.IllegalArgumentException


class GPAActivity : AppCompatActivity() {
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gpa)

        // Set the global instance of the progress to some value
        this.progressDialog = ProgressDialog(this, R.style.DarkProgressDialog)
        progressDialog?.setMessage("Loading..")
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()

        supportActionBar?.title = "CGPA Details"

        // Bind the recycler view
        gpaRecyclerView.layoutManager =
            LinearLayoutManager(this)

        // Populate the GPA data
        populateGPA()
        Log.e("NICK", HelperFunctions(this).getSessionID())
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
                populateGPA(true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun populateGPA(isRefresh: Boolean = false) {
        // Init GSON and the Requests and the helper class
        val helper = HelperFunctions(this)
        val client = OkHttpClient()
        val json = GsonBuilder().create()

        // If not refresh call and data exists, return that and exit
        if (!isRefresh && helper.getGPAFromDB() != null) {
            val gpaInDb = helper.getGPAFromDB()
            val parsedData = json.fromJson(gpaInDb, GPAModel::class.java)

            // Set the adapter for the recycler view on the main thread also dismiss the progress dialog
            runOnUiThread {
                progressDialog?.dismiss()
                gpaRecyclerView.adapter = GPAAdapter(parsedData)
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

        val request = Request.Builder().url("${API_URL}gpa?sessionid=$sessionID").build()

        // Dispatch the request
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressDialog?.dismiss()
                    helper.showToast(this@GPAActivity, "Unable to fetch GPA!")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val respBody = response.body?.string()

                try {
                    // Decode using the error model first
                    val parsed = json.fromJson(respBody, ErrorModel::class.java)

                    // As GSON and JSON parsing in android sucks, if the parsed error is null, throw the exception manually
                    if (parsed.error == null) {
                        throw IllegalArgumentException("Hmmm. Catch it boy!")
                    }

                    if (parsed.error == LOGIN_FAILED) {
                        // Do something to reneew the login credentials
                        progressDialog?.dismiss()
                        val intent = Intent(this@GPAActivity, LoginActivity::class.java)
                        intent.putExtra("reauth", true)
                        startActivity(intent)
                    }
                    else {
                        // Just show the error message
                        runOnUiThread {
                            helper.showToast(this@GPAActivity, parsed.error)
                        }
                    }
                }
                catch (e: Exception) {
                    // Now try to decode as GPA model
                    try {
                        val parsedGPA = json.fromJson(respBody, GPAModel::class.java)

                        // If we are here means the JSON was parsed sucessfully, save in the DB too
                        helper.setGPAInDB(respBody!!)

                        // Set the adapter and dismiss the dialog
                        runOnUiThread {
                            progressDialog?.dismiss()
                            gpaRecyclerView.adapter = GPAAdapter(parsedGPA)
                        }
                    }

                    catch (e: Exception) {
                        // Oh boy, this is something serious
                        runOnUiThread {
                            helper.showToast(this@GPAActivity, e.message ?: "Unable to show expcetion")
                        }
                    }
                }
            }

        })
    }
}
