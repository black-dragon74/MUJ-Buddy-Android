package com.black_dragon74.mujbuddy

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.black_dragon74.mujbuddy.adapters.MenuAdapter
import com.black_dragon74.mujbuddy.models.DashboardModel
import com.black_dragon74.mujbuddy.utils.*
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    // Will hold the application context
    private var context: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        this.context = applicationContext
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the adapter to the recycler view
        menuRecyclerView.layoutManager = GridLayoutManager(this, 2, 1, false)
        menuRecyclerView.adapter = MenuAdapter()

        populateDash()
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
                HelperFunctions(this).doLogout()
            }
            R.id.menu_change_semester -> {
                Toast.makeText(this, "Changing semester....", Toast.LENGTH_SHORT).show()
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
                menuUserProgram.text = dbParsed.admDetails.program.replace("Computer Applications", "C. A.")
            }
            return
        }

        val token = helper.getToken() ?: return
        val client = OkHttpClient()
        val request = Request.Builder().url("${API_URL}dashboard?token=$token").build()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                println("Reuqest failed.")
            }

            override fun onResponse(call: Call, response: Response) {
                val resp = response.body()?.string()

                // Update dash details in the db
                if (resp != null) {
                    helper.updateDashInDB(resp)
                }

                val parsed = gson.fromJson(resp, DashboardModel::class.java)
                runOnUiThread{
                    menuUserName.text = parsed.admDetails.name
                    menuUserAcadYear.text = parsed.admDetails.acadYear
                    menuUserProgram.text = parsed.admDetails.program.replace("Computer Applications", "C. A.")
                }
            }

        })
    }
}
