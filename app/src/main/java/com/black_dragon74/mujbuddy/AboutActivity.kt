package com.black_dragon74.mujbuddy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        supportActionBar?.title = "About"

        aboutAppTF.text = getText(R.string.app_name)
        aboutVersionTF.text = "Ver " + BuildConfig.VERSION_NAME
    }
}
