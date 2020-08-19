package com.katoh.campusschedule.views.actionbar

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import com.katoh.campusschedule.R

abstract class CustomActionModeCallback : ActionMode.Callback {
    private lateinit var listener: ActionModeListener

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return listener.onActionItemClicked(mode, item)
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        listener.onDestroyActionMode()
    }

    interface ActionModeListener {
        fun onDestroyActionMode()
        fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) : Boolean
    }

    fun setActionModeListener(listener: ActionModeListener) {
        this.listener = listener
    }

}