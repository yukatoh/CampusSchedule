package com.katoh.campusschedule.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.katoh.campusschedule.R
import com.katoh.campusschedule.models.entity.CourseRealmObject
import com.katoh.campusschedule.models.entity.TypeContent
import com.katoh.campusschedule.utils.getTextColorFromBg
import io.realm.RealmResults
import kotlinx.android.synthetic.main.row_type_list2.view.*

class TypeInfoRecyclerAdapter(
    private val context: Context,
    private val courses: RealmResults<CourseRealmObject>,
    private val savedTypeContents: List<TypeContent>
): RecyclerView.Adapter<TypeInfoRecyclerAdapter.TypeInfoHolder>() {

    class TypeInfoHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Sync view holder & layout view property
        val typeName: TextView = view.text_type_name
        val courseCount: TextView = view.text_course_count
        val pointCount: TextView = view.text_point_count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeInfoHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_type_list2, parent, false)

        return TypeInfoHolder(view)
    }

    override fun getItemCount(): Int = savedTypeContents.size

    override fun onBindViewHolder(holder: TypeInfoHolder, position: Int) {
        // View property
        holder.typeName.text = savedTypeContents[position].label
        val bgColor = savedTypeContents[position].color
        holder.typeName.setTextColor(context.getTextColorFromBg(bgColor))
        holder.typeName.setBackgroundColor(bgColor)

        val coursesWithType = courses.where().equalTo("type", position).findAll()
        holder.courseCount.text = coursesWithType.size.toString()
        holder.pointCount.text = coursesWithType.sum("point").toString()
    }

}