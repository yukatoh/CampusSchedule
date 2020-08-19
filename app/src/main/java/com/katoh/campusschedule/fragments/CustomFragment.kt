package com.katoh.campusschedule.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.replace

abstract class CustomFragment : Fragment() {
    fun replaceFragment(resId: Int, fragment: Fragment, isBackStack: Boolean = true) {
        parentFragmentManager.beginTransaction().apply {
            if (isBackStack) { addToBackStack(null) }
            replace(resId, fragment)
            commit()
        }
    }

    fun replaceParentFragment(resId: Int, fragment: Fragment, isBackStack: Boolean = true) {
        parentFragment?.parentFragmentManager?.beginTransaction()?.apply {
            if (isBackStack) { addToBackStack(null) }
            replace(resId, fragment)
            commit()
        }
    }
}