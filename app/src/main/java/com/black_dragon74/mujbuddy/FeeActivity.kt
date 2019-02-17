package com.black_dragon74.mujbuddy

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.black_dragon74.mujbuddy.models.FeeModel
import com.black_dragon74.mujbuddy.utils.API_URL
import com.black_dragon74.mujbuddy.utils.HelperFunctions
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_fee.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class FeeActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fee)

        this.progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Loading...")
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()

        supportActionBar?.title = "Fee Details"

        populateFee()
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
                populateFee(true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun populateFee(isRefresh: Boolean = false) {
        val helper = HelperFunctions(this)
        val json = GsonBuilder().setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        val client = OkHttpClient()

        if (!isRefresh && helper.getFeesFromDB() != null) {
            val dataFromDB = helper.getFeesFromDB()
            val parsed = json.fromJson(dataFromDB, FeeModel::class.java)
            runOnUiThread {
                progressDialog?.dismiss()
                feePaidTF.text = if  (parsed.paid?.total == null) "Rs. 0.00" else "Rs. " + parsed.paid.total
                feeUnpaidTF.text = if  (parsed.unpaid?.total == null) "Rs. 0.00" else "Rs. " + parsed.unpaid.total
            }
            return
        }

        // Else dispatch the request
        val request = Request.Builder().url("${API_URL}feedetails?token=${helper.getToken()}").build()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressDialog?.dismiss()
                    helper.showToast(this@FeeActivity, "Unable to get fee details")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val respBody = response.body()?.string()
                try {
                    val parsed = json.fromJson(respBody, FeeModel::class.java)
                    helper.setFeesInDB(respBody!!)
                    runOnUiThread {
                        progressDialog?.dismiss()
                        feePaidTF.text = if  (parsed.paid?.total == null) "Rs. 0.00" else "Rs. " + parsed.paid.total
                        feeUnpaidTF.text = if  (parsed.unpaid?.total == null) "Rs. 0.00" else "Rs. " + parsed.unpaid.total
                    }
                }
                catch (_: Exception) {
                    runOnUiThread {
                        progressDialog?.dismiss()
                        helper.showToast(this@FeeActivity, "Unable to parse fee details")
                    }
                }
            }

        })
    }
}
