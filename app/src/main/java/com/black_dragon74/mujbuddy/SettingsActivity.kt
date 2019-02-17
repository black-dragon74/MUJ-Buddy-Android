package com.black_dragon74.mujbuddy

import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.view.MenuItem
import android.support.v4.app.NavUtils
import com.black_dragon74.mujbuddy.utils.HelperFunctions
import com.black_dragon74.mujbuddy.utils.SHARED_PREF
import com.black_dragon74.mujbuddy.utils.SEMESTER_DATA


class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val helper = HelperFunctions(this)
        val prefMgr = preferenceManager
        prefMgr.sharedPreferencesName = SHARED_PREF
        prefMgr.sharedPreferencesMode = 0
        setupActionBar()
        addPreferencesFromResource(R.xml.pref_general)
        val pref = findPreference("current_semester")
        bindPreferenceSummaryToValue(pref)
        pref.summary = helper.getCurrentSemester()
    }


    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this)
            }
            return true
        }
        return super.onMenuItemSelected(featureId, item)
    }

    companion object {

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val listPreference = preference
                val index = listPreference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                    if (index >= 0)
                        listPreference.entries[index]
                    else
                        null
                )

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.summary = stringValue
            }
            true
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.

         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                preference.context.getSharedPreferences(SHARED_PREF, 0)?.getString(preference.key, "")
            )
        }
    }
}
