package com.black_dragon74.mujbuddy

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.black_dragon74.mujbuddy.adapters.MenuAdapter
import com.black_dragon74.mujbuddy.models.DashboardModel
import com.black_dragon74.mujbuddy.utils.*
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    // Just to carry a few globally required vars
    companion object {
        const val CALL_PERMISSION_CODE = 2974
    }

    // Will hold the application context
    private var context: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        this.context = applicationContext
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the adapter to the recycler view
        menuRecyclerView.layoutManager =
            GridLayoutManager(this, 2, 1, false)
        menuRecyclerView.adapter = MenuAdapter()

        populateDash()
        checkAndRequestRequiredPermissions()
        checkForUpdates()
    }

    // Inflate the menu layout file
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        this.context = applicationContext
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_logout -> {
                val aBuilder = AlertDialog.Builder(this, R.style.DarkProgressDialog)
                aBuilder.setTitle("Logout?")
                aBuilder.setMessage("Are you sure you want to logout")
                aBuilder.setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    HelperFunctions(this).doLogout()
                }
                aBuilder.setNegativeButton("No") {dialog, _ ->
                    dialog.dismiss()
                }
                aBuilder.show()
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.menu_info -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun populateDash() {
        val helper = HelperFunctions(this)
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        if (helper.getDashFromDB() != null) {

            val dbParsed = gson.fromJson(helper.getDashFromDB(), DashboardModel::class.java)
            runOnUiThread {
                menuUserName.text = dbParsed.admDetails.name
                menuUserAcadYear.text = dbParsed.admDetails.acadYear
                menuUserProgram.text = dbParsed.admDetails.program
            }
            return
        }

        val sessionID = helper.getSessionID()

        if (sessionID.isNullOrEmpty()) {
            helper.showToast(this, "Invalid request, access denied.")
            return
        }
        val client = OkHttpClient()
        val request = Request.Builder().url("${API_URL}dashboard?sessionid=$sessionID").build()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                println("Reuqest failed.")
            }

            override fun onResponse(call: Call, response: Response) {
                val resp = response.body?.string()

                // Update dash details in the db
                if (resp != null) {
                    try {
                        val parsed = gson.fromJson(resp, DashboardModel::class.java)
                        helper.updateDashInDB(resp)
                        runOnUiThread{
                            menuUserName.text = parsed.admDetails.name
                            menuUserAcadYear.text = parsed.admDetails.acadYear
                            menuUserProgram.text = parsed.admDetails.program
                        }
                    }
                    catch (e: Exception) {
                        runOnUiThread {
                            helper.showToast(this@MainActivity, "Unable to get dashboard ")
                        }
                    }
                }
            }

        })
    }

    private fun checkAndRequestRequiredPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // We need to ask for the permissions
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), CALL_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Check if the permission was granted or not here
        when (requestCode) {
            CALL_PERMISSION_CODE -> {
                val helper = HelperFunctions(this)
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Hooray! Go the permission
                    helper.showToast(this, "You can now directly call faculties from the app")
                }
                else {
                    helper.showToast(this, "You won't be able to directly call faculties from the app")
                }
            }
        }
    }

    private fun checkForUpdates() {
        // Init the updater
        AppUpdater(this)
            .setUpdateFrom(UpdateFrom.JSON)
            .setUpdateJSON("https://raw.githubusercontent.com/black-dragon74/MUJ-Buddy-Updates/master/update.json")
            .setTitleOnUpdateAvailable("Update available!")
            .setButtonDoNotShowAgain("")
            .setCancelable(false)
            .start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        MessMenuActivity.needsRefresh = true
    }
}
