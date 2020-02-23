package com.black_dragon74.mujbuddy.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import android.view.View
import android.widget.Toast
import com.black_dragon74.mujbuddy.LoginActivity
import android.view.inputmethod.InputMethodManager
import com.black_dragon74.mujbuddy.BuildConfig
import com.black_dragon74.mujbuddy.models.User


class HelperFunctions(val context: Context) {
    // The shared pref instance
    private val sharedPref = context.getSharedPreferences(SHARED_PREF, 0)

    //
    //  General helper functions
    //
    // Shows a toast message for a small duaration of time on the provided context
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    // Hides the keyboard
    fun endEditing(view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //
    //  Login helper functions
    //
    fun isUserLoggedIn(): Boolean {
        return sharedPref.getBoolean(LOGIN_STATE, false)
    }

    fun getUserCredentials(): User? {
        val username = sharedPref.getString(USER_ID,  null)
        val password = sharedPref.getString(USER_PASS, null)

        return when (username != null && password != null) {
            true -> User(username, password)
            false -> null
        }
    }

    fun doLogout() {
        sharedPref.edit().clear().apply()
        context.getSharedPreferences("${BuildConfig.APPLICATION_ID}_preferences", 0).edit().clear().apply()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(context, intent, null)
    }

    fun getSessionID(): String? {
        return sharedPref.getString(SESSION_ID, null)
    }

    //
    //  Dashboard related functions
    //
    fun updateDashInDB(data: String) {
        sharedPref.edit().remove(USER_DATA).apply()
        sharedPref.edit().putString(USER_DATA, data).apply()
    }

    fun getDashFromDB(): String? {
        return sharedPref.getString(USER_DATA, null)
    }

    //
    //  Attendance related functions
    //
    fun updateAttendanceInDB(attendance: String) {
        sharedPref.edit().remove(ATTENDANCE_DATA).apply()
        sharedPref.edit().putString(ATTENDANCE_DATA, attendance).apply()
    }

    fun getAttendanceFromDB(): String? {
        return sharedPref.getString(ATTENDANCE_DATA, null)
    }

    //
    //  Current semester related functions
    //
    fun setCurrentSemester(semester: Int) {
        sharedPref.edit().remove(SEMESTER_DATA).apply()
        sharedPref.edit().putString(SEMESTER_DATA, semester.toString()).apply()
    }

    fun getCurrentSemester(): String? {
        return sharedPref.getString(SEMESTER_DATA, "1")
    }

    //
    //  Functions related to GPA data handling
    //
    fun getGPAFromDB(): String? {
        return sharedPref.getString(GPA_DATA, null)
    }

    fun setGPAInDB(gpaData: String) {
        sharedPref.edit().remove(GPA_DATA).apply()
        sharedPref.edit().putString(GPA_DATA, gpaData).apply()
    }

    //
    //  Functions related to the contacts
    //
    fun getContactsFromDB(): String? {
        return sharedPref.getString(CONTACTS_DATA, null)
    }

    fun setContactsInDB(contacts: String) {
        sharedPref.edit().remove(CONTACTS_DATA).apply()
        sharedPref.edit().putString(CONTACTS_DATA, contacts).apply()
    }

    //
    //  Functions related to the results
    //
    fun getResultsFromDB(): String? {
        return sharedPref.getString(RESULTS_DATA, null)
    }

    fun setResultInDB(results: String) {
        sharedPref.edit().remove(RESULTS_DATA).apply()
        sharedPref.edit().putString(RESULTS_DATA, results).apply()
    }

    //
    //  Functions related to the fees
    //
    fun getFeesFromDB(): String? {
        return sharedPref.getString(FEE_DATA, null)
    }

    fun setFeesInDB(fees: String) {
        sharedPref.edit().remove(FEE_DATA).apply()
        sharedPref.edit().putString(FEE_DATA, fees).apply()
    }

    //
    //  Functions related to the events
    //
    fun getEventsFromDB(): String? {
        return sharedPref.getString(EVENTS_DATA, null)
    }

    fun setEventsInDB(events: String) {
        sharedPref.edit().remove(EVENTS_DATA).apply()
        sharedPref.edit().putString(EVENTS_DATA, events).apply()
    }

    //
    //  Functions related to the internals marks
    //
    fun getInternalsFromDB(): String? {
        return sharedPref.getString(INTERNALS_DATA, null)
    }

    fun setInternalsInDB(internals: String) {
        sharedPref.edit().remove(INTERNALS_DATA).apply()
        sharedPref.edit().putString(INTERNALS_DATA, internals).apply()
    }
}