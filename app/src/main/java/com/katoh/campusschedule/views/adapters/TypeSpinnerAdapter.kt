package com.katoh.campusschedule.views.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.setPadding
import com.katoh.campusschedule.R
import com.katoh.campusschedule.models.entity.TypeContent
import com.katoh.campusschedule.utils.getTextColorFromBg

class TypeSpinnerAdapter: ArrayAdapter<TypeContent> {
    constructor(context: Context) :
            super(context, android.R.layout.simple_spinner_item) {
        setDropDownViewResource(android.R.layout.simple_spinner_item)
    }

    constructor(context: Context, list: List<TypeContent>) :
            super(context, android.R.layout.simple_spinner_item, list) {
        setDropDownViewResource(android.R.layout.simple_spinner_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        (super.getView(position, convertView, parent) as TextView).apply {
            getItem(position)?.let {type ->
                text = type.label
                setBackgroundColor(type.color)
                setTextColor(context.getTextColorFromBg(type.color))
            }
        }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
        (super.getDropDownView(position, convertView, parent) as TextView).apply {
            getItem(position)?.let {type ->
                text = type.label
            }
            setPadding(resources.getDimensionPixelSize(R.dimen.spacing_mid))
        }
}