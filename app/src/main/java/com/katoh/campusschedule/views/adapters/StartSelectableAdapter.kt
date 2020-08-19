package com.katoh.campusschedule.views.adapters

import android.content.Context
import android.view.*
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.katoh.campusschedule.R
import com.katoh.campusschedule.models.entity.TermRealmObject
import io.realm.RealmResults
import kotlinx.android.synthetic.main.row_term_list.view.*

class StartSelectableAdapter(
    private val context: Context,
    private val terms: RealmResults<TermRealmObject>
) : SelectableAdapter<StartSelectableAdapter.StartViewHolder>() {
    private lateinit var itemClickListener: OnItemClickListener
    private lateinit var menuItemClickListener: OnMenuItemClickListener

    class StartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Sync view holder & layout view property
        var termName: TextView = view.term_name
        var termTerm: TextView = view.term_start_end
        var popupButton: ImageButton = view.button_popup
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_term_list, parent, false)

        return StartViewHolder(view)
    }

    override fun getItemCount(): Int = terms.size

    override fun onBindViewHolder(holder: StartViewHolder, position: Int) {
        val term = terms[position]
        // View property <- Realm
        holder.termName.text = term?.termLabel.toString()
        holder.termTerm.text = "%s %s - %s %s".format(
            term?.startYear.toString(), term?.startMonth.toString(),
            term?.endYear.toString(), term?.endMonth.toString()
        )

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
                inflate(R.menu.menu_start_popup)
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
}