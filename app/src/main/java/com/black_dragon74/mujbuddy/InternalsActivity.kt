package com.black_dragon74.mujbuddy

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.black_dragon74.mujbuddy.adapters.InternalsAdapter
import com.black_dragon74.mujbuddy.models.ErrorModel
import com.black_dragon74.mujbuddy.models.InternalsModel
import com.black_dragon74.mujbuddy.utils.API_URL
import com.black_dragon74.mujbuddy.utils.HelperFunctions
import com.black_dragon74.mujbuddy.utils.LOGIN_FAILED
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_internals.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class InternalsActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_internals)

        this.progressDialog = ProgressDialog(this, R.style.DarkProgressDialog)
        progressDialog?.setMessage("Loading...")
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()

        supportActionBar?.title = "Internal Marks"

        internalsRecyclerView.layoutManager =
            LinearLayoutManager(this)

        populateInternals()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.generic_refresh_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.generic_refresh -> {
                progressDialog?.setMessage("Refreshing...")
                progressDialog?.show()
                populateInternals(true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun populateInternals(isRefresh: Boolean = false) {
        // Init GSON and the Requests and the helper class
        val helper = HelperFunctions(this)
        val client = OkHttpClient()
        val json = GsonBuilder().create()

        // If not refresh call and data exists, return that and exit
        if (!isRefresh && helper.getInternalsFromDB() != null) {
            val internalsInDB = helper.getInternalsFromDB()
            val parsedData = json.fromJson(internalsInDB, Array<InternalsModel>::class.java)

            // Set the adapter for the recycler view on the main thread also dismiss the progress dialog
            runOnUiThread {
                progressDialog?.dismiss()
                internalsRecyclerView.adapter = InternalsAdapter(parsedData)
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

        val request = Request.Builder().url("${API_URL}internals?sessionid=$sessionID&semester=${helper.getCurrentSemester()}").build()

        // Dispatch the request
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressDialog?.dismiss()
                    helper.showToast(this@InternalsActivity, "Unable to fetch internals marks!")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val respBody = response.body?.string()
                // Try to decode using the GPA MODEL
                try {
                    val parsedInternals = json.fromJson(respBody, Array<InternalsModel>::class.java)

                    // If we are here means the JSON was parsed sucessfully, save in the DB too
                    helper.setInternalsInDB(respBody!!)

                    // Set the adapter and dismiss the dialog
                    runOnUiThread {
                        progressDialog?.dismiss()
                        internalsRecyclerView.adapter = InternalsAdapter(parsedInternals)
                    }
                }
                catch (e: Exception) {
                    // Try and see if the reponse conforms to error model type
                    try {
                        val parsed = json.fromJson(respBody, ErrorModel::class.java)
                        if (parsed.error == LOGIN_FAILED) {
                            // Do something to reneew the login credentials
                            progressDialog?.dismiss()
                            val intent = Intent(this@InternalsActivity, LoginActivity::class.java)
                            intent.putExtra("reauth", true)
                            startActivity(intent)
                        }
                        else {
                            // Just show the error message
                            runOnUiThread {
                                helper.showToast(this@InternalsActivity, parsed.error)
                                progressDialog?.dismiss()
                            }
                        }
                    }

                    catch (e: Exception) {
                        runOnUiThread {
                            helper.showToast(this@InternalsActivity, e.message ?: "Unable to show expcetion")
                        }
                    }
                }
            }

        })
    }
}
