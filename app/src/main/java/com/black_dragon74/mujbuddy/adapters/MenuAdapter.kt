package com.black_dragon74.mujbuddy.adapters

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.black_dragon74.mujbuddy.*
import com.black_dragon74.mujbuddy.models.MenuModel
import kotlinx.android.synthetic.main.menu_row.view.*

class MenuAdapter: RecyclerView.Adapter<MenuViewHolder>() {
    // The array of menu items
    private val item1 = MenuModel("Attendance", "Your Attendance")
    private val item2 = MenuModel("Internals", "Assessment Marks")
    private val item3 = MenuModel("Results", "Semester Results")
    private val item4 = MenuModel("GPA", "Your CGPA")
//    private val item5 = MenuModel("Events", "At University")
    private val item6 = MenuModel("Mess Menu", "For Hostelers")
//    private val item7 = MenuModel("Fee Details", "Paid / Unpaid")
    private val item8 = MenuModel("Faculty Contacts", "At University")
    private val menuItems = listOf(item1, item2, item3, item4, item6, item8)

    private val colors = listOf(
        R.color.menu1,
        R.color.menu2,
        R.color.menu3,
        R.color.menu4,
        R.color.menu6,
        R.color.menu8
        )

    // This is cellForItemAtIndexPath
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        // We have to inflate the layout for the cell
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.menu_row, parent, false)
        return MenuViewHolder(cellForRow)
    }

    // this is numberOfItemsInSection
    override fun getItemCount(): Int {
        return menuItems.count()
    }

    override fun onBindViewHolder(parent: MenuViewHolder, position: Int) {
        val currentMenuItem = menuItems[position]
        parent.view.textView2.text = currentMenuItem.title
        parent.view.textView.text = currentMenuItem.subtitle
        parent.view.imageView3.setImageResource(setImageUsing(currentMenuItem.title))
        parent.view.menuCardView.setCardBackgroundColor(parent.view.context.resources.getColor(colors[position]))
    }

    private fun setImageUsing(itemID: String): Int {
        when (itemID) {
            "Attendance" -> {
                return R.drawable.attendance
            }
            "Internals" -> {
                return R.drawable.assessment
            }
            "Results" -> {
                return R.drawable.results
            }
            "GPA" -> {
                return R.drawable.gpa
            }
            "Events" -> {
                return R.drawable.events
            }
            "Mess Menu" -> {
                return R.drawable.mess_menu
            }
            "Fee Details" -> {
                return R.drawable.fees
            }
            "Faculty Contacts" -> {
                return R.drawable.contacts
            }
        }
        return -1
    }

}

class MenuViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    // Launches the activity for the specified class
    private fun launchActivityFor(destinationClass: Class<*>) {
        val intent = Intent(view.context, destinationClass)
        startActivity(view.context, intent, null)
    }

    init {
        view.setOnClickListener {
            when (view.textView2.text) {
                "Attendance" -> {
                    launchActivityFor(AttendanceActivity::class.java)
                }
                "Internals" -> {
                    launchActivityFor(InternalsActivity::class.java)
                }
                "Results" -> {
                    launchActivityFor(ResultsActivity::class.java)
                }
                "GPA" -> {
                    launchActivityFor(GPAActivity::class.java)
                }
                "Events" -> {
                    launchActivityFor(EventsActivity::class.java)
                }
                "Mess Menu" -> {
                    launchActivityFor(MessMenuActivity::class.java)
                }
                "Fee Details" -> {
                    launchActivityFor(FeeActivity::class.java)
                }
                "Faculty Contacts" -> {
                    launchActivityFor(ContactsActivity::class.java)
                }
            }
        }
    }
}
