package com.katoh.campusschedule.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.katoh.campusschedule.R
import com.katoh.campusschedule.models.entity.CourseRealmObject
import io.realm.RealmResults
import kotlinx.android.synthetic.main.row_book_list.view.*

class BookRecyclerAdapter(
    private val context: Context,
    private val courses: RealmResults<CourseRealmObject>
) : RecyclerView.Adapter<BookRecyclerAdapter.BookViewHolder>() {

    class BookViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val dayOrder: TextView = view.day_order
        val courseName: TextView = view.course_name
        val bookName: TextView = view.book_name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_book_list, parent, false)

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
    }

    /**
     * Translate the day index to the string resource
     */
    private fun translateWeekDay(day: Int) : String {
        val weekDays = context.resources.getStringArray(R.array.labels_weekday)
        return weekDays[day]
    }
}