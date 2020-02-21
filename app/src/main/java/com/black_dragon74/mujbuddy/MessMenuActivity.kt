package com.black_dragon74.mujbuddy

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.black_dragon74.mujbuddy.adapters.ContactsAdapter
import com.black_dragon74.mujbuddy.adapters.MessMenuViewPagerAdapter
import com.black_dragon74.mujbuddy.fragments.BreakfastFragment
import com.black_dragon74.mujbuddy.fragments.DinnerFragment
import com.black_dragon74.mujbuddy.fragments.HighteaFragment
import com.black_dragon74.mujbuddy.fragments.LunchFragment
import com.black_dragon74.mujbuddy.models.ContactsModel
import com.black_dragon74.mujbuddy.models.ErrorModel
import com.black_dragon74.mujbuddy.models.MessMenuModel
import com.black_dragon74.mujbuddy.utils.API_URL
import com.black_dragon74.mujbuddy.utils.HelperFunctions
import com.black_dragon74.mujbuddy.utils.LOGIN_FAILED
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.activity_mess_menu.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class MessMenuActivity : AppCompatActivity() {

    companion object {
        var MessMenuData: MessMenuModel? = null
        var needsRefresh: Boolean = true
    }

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mess_menu)

        supportActionBar?.title = "Mess Menu"

        this.progressDialog = ProgressDialog(this, R.style.DarkProgressDialog)
        progressDialog?.setMessage("Loading...")
        progressDialog?.setCanceledOnTouchOutside(false)

        if (needsRefresh) {
            progressDialog?.show()
            getMenuFromTheAPI()
        }
        else {
            if (!MessMenuData?.last_updated_at.isNullOrEmpty()) {
                val updated = MessMenuData!!.last_updated_at!!.split(" ")[0]
                last_updated_on.text = "Last updated - $updated"
            }
        }

        // Setup view pager
        val viewPager = mess_view_pager
        setupViewPager(viewPager)

        val messsTabs = mess_tabs
        messsTabs.setupWithViewPager(viewPager)
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
                getMenuFromTheAPI()
            }
            else -> {
                // Do not do a thing
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = MessMenuViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(BreakfastFragment(), "Breakfast")
        adapter.addFragment(LunchFragment(), "Lunch")
        adapter.addFragment(HighteaFragment(), "High Tea")
        adapter.addFragment(DinnerFragment(), "Dinner")
        viewPager.adapter = adapter
    }

    private fun getMenuFromTheAPI() {
        val helper = HelperFunctions(this)
        val json = GsonBuilder().create()
        val client = OkHttpClient()

        val sessionID = helper.getSessionID()

        if (sessionID.isNullOrEmpty()) {
            helper.showToast(this, "Invalid request, access denied.")
            return
        }

        val request = Request.Builder().url("${API_URL}mess_menu?sessionid=$sessionID").build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressDialog?.dismiss()
                    helper.showToast(this@MessMenuActivity, "Failed to fetch contacts")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val respBody = response.body?.string()
                // Try to parse
                try {
                    val parsed = json.fromJson(respBody, MessMenuModel::class.java)

                    // Set the data
                    runOnUiThread {
                        progressDialog?.dismiss()
                        needsRefresh = false
                        MessMenuData = parsed
                        if (!parsed.last_updated_at.isNullOrEmpty()) {
                            val updated = parsed.last_updated_at.split(" ")[0]
                            last_updated_on.text = "Last updated - $updated"
                        }
                        val fetched = Intent("com.mujbuddy.MESS_MENU_FETCHED")
                        sendBroadcast(fetched)
                    }
                }
                catch (e: Exception) {
                    // Try and see if the reponse conforms to error model type
                    try {
                        val parsed = json.fromJson(respBody, ErrorModel::class.java)
                        if (parsed.error == LOGIN_FAILED) {
                            // Do something to reneew the login credentials
                            progressDialog?.dismiss()
                            val intent = Intent(this@MessMenuActivity, LoginActivity::class.java)
                            intent.putExtra("reauth", true)
                            startActivity(intent)
                        }
                        else {
                            // Just show the error message
                            runOnUiThread {
                                helper.showToast(this@MessMenuActivity, parsed.error)
                            }
                        }
                    }

                    catch (e: Exception) {
                        runOnUiThread {
                            helper.showToast(this@MessMenuActivity, e.message ?: "Unable to show expcetion")
                        }
                    }
                }
            }
        })
    }
}
