package com.katoh.campusschedule.views.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.katoh.campusschedule.R
import com.katoh.campusschedule.models.entity.TypeContent
import com.katoh.campusschedule.utils.getTextColorFromBg
import com.katoh.campusschedule.utils.toColorIntArray
import kotlinx.android.synthetic.main.row_type_list.view.*
import petrov.kristiyan.colorpicker.ColorPicker

class TypeSettingSelectableAdapter(
    private val context: Context, private val types: List<TypeContent>
) : SelectableAdapter<TypeSettingSelectableAdapter.GeneralSettingViewHolder>() {

    class GeneralSettingViewHolder(view: View)
        : RecyclerView.ViewHolder(view) {
        var typeLabel: TextView = view.type_name
        var color: TextView = view.color_selected
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int
    ): GeneralSettingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_type_list, parent, false)

        return GeneralSettingViewHolder(view)
    }

    override fun getItemCount(): Int = types.size

    override fun onBindViewHolder(holder: GeneralSettingViewHolder, position: Int) {
        // View Property
        holder.typeLabel.text = types[position].label
        holder.color.text = "#%s".format(
            Integer.toHexString(types[position].color).substring(2))
        holder.color.setBackgroundColor(types[position].color)
        holder.color.setTextColor(context.getTextColorFromBg(types[position].color))

        // Event Listener
        holder.itemView.button_color_dialog.setOnClickListener {
            val colors = context.resources.obtainTypedArray(R.array.colors_picker)
            ColorPicker(context)
                .setTitle(context.getString(R.string.message_choose_color))
                .setColors(
                    *colors.toColorIntArray()
                )
                .setDefaultColorButton(
                    Color.parseColor(holder.color.text.toString()))
                .setColumns(7)
                .setOnFastChooseColorListener(object : ColorPicker.OnFastChooseColorListener {
                    override fun setOnFastChooseColorListener(colorPosition: Int, color: Int) {
                        Log.d("chosenColor", Integer.toHexString(color))
                        holder.color.text = "#%s".format(
                            Integer.toHexString(color).substring(2))
                        holder.color.setBackgroundColor(color)
                        holder.color.setTextColor(context.getTextColorFromBg(color))
                    }

                    override fun onCancel() = Unit
                })
                .setRoundColorButton(true)
                .show()
            colors.recycle()
        }
    }

}