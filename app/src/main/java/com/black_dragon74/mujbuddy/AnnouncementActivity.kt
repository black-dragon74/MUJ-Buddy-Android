package com.black_dragon74.mujbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AnnouncementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcement)

        supportActionBar?.title = "Notifications"
    }
}
