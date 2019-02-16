package com.black_dragon74.mujbuddy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.black_dragon74.mujbuddy.adapters.GPAAdapter
import kotlinx.android.synthetic.main.activity_gpa.*

class GPAActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gpa)

        supportActionBar?.title = "CGPA Details"

        // Bind the recycler view
        gpaRecyclerView.layoutManager = LinearLayoutManager(this)
        gpaRecyclerView.adapter = GPAAdapter()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.generic_refresh_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.generic_refresh -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
}
