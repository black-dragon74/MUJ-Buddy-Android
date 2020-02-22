package com.black_dragon74.mujbuddy

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.Nullable
import com.black_dragon74.mujbuddy.utils.HelperFunctions
import com.black_dragon74.mujbuddy.utils.SEMESTER_DATA
import com.codevscolor.materialpreference.activity.MaterialPreferenceActivity
import com.codevscolor.materialpreference.callback.MaterialPreferenceCallback
import com.codevscolor.materialpreference.util.MaterialPrefUtil

class SettingsActivity : MaterialPreferenceActivity(), MaterialPreferenceCallback {

    override fun init(@Nullable savedInstanceState: Bundle?) {

        setPreferenceChangedListener(this)

        useDarkTheme(true)

        setToolbarTitle("Settings")

        setPrimaryColor(MaterialPrefUtil.COLOR_RED)

        setDefaultSecondaryColor(this, MaterialPrefUtil.COLOR_AMBER)

        setAppPackageName("com.black_dragon74.mujbuddy")

        setXmlResourceName("pref_general")
    }

    override fun onPreferenceSettingsChanged(sharedPreferences: SharedPreferences, name: String) {
        // Init the helper
        val helper = HelperFunctions(this)

        // Get the semester value
        val currSem = sharedPreferences.getString(SEMESTER_DATA, "1")!!.toInt()

        helper.setCurrentSemester(currSem)
    }
}