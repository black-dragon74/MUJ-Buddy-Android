package com.black_dragon74.mujbuddy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class FeeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fee)

        supportActionBar?.title = "Fee Details"
    }
}
