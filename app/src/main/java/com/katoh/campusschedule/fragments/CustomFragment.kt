package com.katoh.campusschedule.fragments

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class CustomFragment : Fragment() {
    // Activity
    val activity: AppCompatActivity by lazy {
        requireActivity() as AppCompatActivity
    }

    /**
     * Replace a fragment of an activity to another one
     * @param resId a resource id of the fragment container
     * @param fragment a fragment instance
     * @param isBackStack whether stack function is valid when pressed a back button
     */
    fun replaceFragment(resId: Int, fragment: Fragment, isBackStack: Boolean = true) {
        parentFragmentManager.beginTransaction().apply {
            if (isBackStack) {
                addToBackStack(null)
            }
            replace(resId, fragment)
            commit()
        }
    }

    /**
     * Replace a fragment of a parent fragment to another one
     * @param resId a resource id of the fragment container
     * @param fragment a fragment instance
     * @param isBackStack whether stack function is valid when pressed a back button
     */
    fun replaceParentFragment(resId: Int, fragment: Fragment, isBackStack: Boolean = true) {
        parentFragment?.parentFragmentManager?.beginTransaction()?.apply {
            if (isBackStack) {
                addToBackStack(null)
            }
            replace(resId, fragment)
            commit()
        }
    }
}