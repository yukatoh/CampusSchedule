package com.katoh.campusschedule.utils

import android.content.Context
import android.content.res.TypedArray
import com.katoh.campusschedule.R

fun Context.getTextColorFromBg(backgroundColor: Int) : Int {
    val hexColor = Integer.toHexString(backgroundColor)
    val red = Integer.parseInt(hexColor.substring(2..3), 16)
    val green = Integer.parseInt(hexColor.substring(4..5), 16)
    val blue = Integer.parseInt(hexColor.substring(6..7), 16)
    return if (((red * 299 + green * 587 + blue * 114) / 1000) < 128)
        this.getColor(R.color.white)
    else
        this.getColor(R.color.black)
}

fun TypedArray.toColorList(): List<Int> {
    val colors = mutableListOf<Int>()
    for (i in 0 until this.length()) {
        colors.add(this.getColor(i, 0))
    }
    return colors
}

fun TypedArray.toColorIntArray(): IntArray =
    this.toColorList().toIntArray()
