package com.black_dragon74.mujbuddy.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.black_dragon74.mujbuddy.interfaces.MessMenuInterface

class MessMenuReceiver : BroadcastReceiver() {

    var delegate: MessMenuInterface? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        delegate?.onMessMenuFetched()
    }
}