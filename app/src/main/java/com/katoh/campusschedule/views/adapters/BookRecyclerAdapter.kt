package com.katoh.campusschedule.views.adapters

import android.content.Context
import android.view.*
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.katoh.campusschedule.R
import com.katoh.campusschedule.models.entity.CourseRealmObject
import io.realm.RealmResults
import kotlinx.android.synthetic.main.row_my_book_list.view.*

class BookRecyclerAdapter(
    private val context: Context,
    private val courses: RealmResults<CourseRealmObject>
) : RecyclerView.Adapter<BookRecyclerAdapter.BookViewHolder>() {

    private lateinit var menuItemClickListener: OnMenuItemClickListener
    private lateinit var itemClickListener: OnItemClickListener

    class BookViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val dayOrder: TextView = view.day_order
        val courseName: TextView = view.course_name
        val bookName: TextView = view.book_name
        val popupButton: ImageButton = view.button_popup
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_my_book_list, parent, false)

        return BookViewHolder(view)
    }

    override fun getItemCount(): Int = courses.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val course = courses[position] ?: throw Exception()

        // View property <- Realm
        holder.dayOrder.text = "%s%s".format(
            translateWeekDay(course.day), course.order)

        holder.courseName.text = course.courseName

        holder.bookName.text =
            if (course.textbook.isBlank()) {
                context.getString(R.string.blank)
            } else {
                course.textbook
            }

        holder.itemView.setBackgroundResource(R.drawable.state_list)

        // Set Event Listener
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(position)
        }

        holder.popupButton.setOnClickListener { view ->
            PopupMenu(view.context, view).apply {
                inflate(R.menu.menu_book_list_popup)
                gravity = Gravity.END
                setOnMenuItemClickListener {
                    menuItemClickListener.onClick(it, position)
                }
                show()
            }
        }
    }

    /**
     * Translate the day index to the string resource
     */
    private fun translateWeekDay(day: Int) : String {
        val weekDays = context.resources.getStringArray(R.array.labels_weekday)
        return weekDays[day]
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
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

}