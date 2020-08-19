package com.katoh.campusschedule.views.adapters

import android.content.Context
import android.view.*
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.katoh.campusschedule.R
import com.katoh.campusschedule.models.entity.CourseRealmObject
import com.katoh.campusschedule.models.entity.TypeContent
import com.katoh.campusschedule.utils.getTextColorFromBg
import io.realm.RealmResults
import kotlinx.android.synthetic.main.row_time_list.view.*

class TimeSelectableAdapter(
    private val context: Context,
    private val courses: RealmResults<CourseRealmObject>,
    private val savedTypeContents: List<TypeContent>
) : SelectableAdapter<TimeSelectableAdapter.TimeViewHolder>(){
    private lateinit var itemClickListener: OnItemClickListener
    private lateinit var menuItemClickListener: OnMenuItemClickListener

    class TimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Sync view holder & layout view property
        val dayOrder: TextView = view.day_order
        val courseName: TextView = view.course_name
        val type: TextView = view.type
        val point: TextView = view.point
        val popupButton: ImageButton = view.button_popup
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_time_list, parent, false)

        return TimeViewHolder(view)
    }

    override fun getItemCount(): Int = courses.size

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        val course = courses[position] ?: throw Exception()
        // View property <- Realm
        holder.dayOrder.text = "%s%s".format(
            translateWeekDay(course.day), course.order)

        holder.courseName.text = course.courseName

        holder.type.text = savedTypeContents[course.type].label
        val bgColor = savedTypeContents[course.type].color
        holder.type.setTextColor(context.getTextColorFromBg(bgColor))
        holder.type.setBackgroundColor(bgColor)

        holder.point.text = course.point.toString()

        // Set Holder View Property
        holder.itemView.isSelected = isSelected(position)
        holder.itemView.setBackgroundResource(R.drawable.state_list)

        // Set Event Listener
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(position)
        }

        holder.itemView.setOnLongClickListener {
            itemClickListener.onLongClick(position)
        }

        holder.popupButton.setOnClickListener { view ->
            PopupMenu(view.context, view).apply {
                inflate(R.menu.menu_time_list_popup)
                gravity = Gravity.END
                setOnMenuItemClickListener {
                    menuItemClickListener.onClick(it, position)
                }
                show()
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
        fun onLongClick(position: Int) : Boolean
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    interface OnMenuItemClickListener {
        fun onClick(menuItem: MenuItem, position: Int) : Boolean
    }

    fun setOnMenuItemClickListener(listener: OnMenuItemClickListener) {
        menuItemClickListener = listener
    }

    /**
     * Translate the day index to the string resource
     */
    private fun translateWeekDay(day: Int) : String {
        val weekDays = context.resources.getStringArray(R.array.labels_weekday)
        return weekDays[day]
    }
}