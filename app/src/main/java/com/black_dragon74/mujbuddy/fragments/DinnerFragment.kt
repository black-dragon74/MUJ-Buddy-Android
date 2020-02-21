package com.black_dragon74.mujbuddy.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.black_dragon74.mujbuddy.MessMenuActivity

import com.black_dragon74.mujbuddy.R
import com.black_dragon74.mujbuddy.adapters.MessMenuAdapter

class DinnerFragment : Fragment() {

    private var items: View? = null

    private val receiver: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            getMenuDetails(items!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val filter = IntentFilter("com.mujbuddy.MESS_MENU_FETCHED")
        context?.registerReceiver(receiver, filter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        items = inflater.inflate(R.layout.fragment_dinner, container, false)

        getMenuDetails(items!!)

        return items
    }

    private fun getMenuDetails(items: View) {
        val menu = items.findViewById<RecyclerView>(R.id.dinner_recycler_view)
        menu.layoutManager = LinearLayoutManager(context)
        val data = MessMenuActivity.MessMenuData?.dinner ?: arrayOf("Dinner menu not available")
        menu.adapter = MessMenuAdapter(data)
    }

    override fun onDestroy() {
        super.onDestroy()

        context?.unregisterReceiver(receiver)
    }
}
