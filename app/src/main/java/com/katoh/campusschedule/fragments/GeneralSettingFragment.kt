package com.katoh.campusschedule.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.katoh.campusschedule.R
import com.katoh.campusschedule.models.entity.TypeContent
import com.katoh.campusschedule.models.prefs.CustomSharedPreferences
import com.katoh.campusschedule.models.prefs.PreferenceNames
import com.katoh.campusschedule.utils.settingDao
import com.katoh.campusschedule.views.adapters.TypeSettingSelectableAdapter
import kotlinx.android.synthetic.main.fragment_general_setting.view.*
import kotlinx.android.synthetic.main.row_type_list.view.*

class GeneralSettingFragment : CustomFragment() {
    // Activity
    private val activity: AppCompatActivity by lazy {
        getActivity() as AppCompatActivity
    }

    // Views
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TypeSettingSelectableAdapter

    // Shared Preferences
    private val sp: CustomSharedPreferences by lazy {
        CustomSharedPreferences(activity, PreferenceNames.DEFAULT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_general_setting, container, false)

        // Action Bar
        activity.supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.general_setting)
        }

        // Recycler View
        recyclerView = view.findViewById(R.id.recyclerview_type)
        recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false)
        recyclerView.itemAnimator = DefaultItemAnimator()

        adapter = TypeSettingSelectableAdapter(requireContext(),
            sp.settingDao().savedTypeContents)

        setViewAdapter()

        // Other views
        view.switch_sat.isChecked = sp.settingDao().satVisible
        view.spinner_order.setSelection(sp.settingDao().timeOrderMax - 1)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_course_setting, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
                true
            }

            R.id.save -> {
                val typeContents = mutableListOf<TypeContent>()
                for (position in 0 until adapter.itemCount){
                    recyclerView.findViewHolderForAdapterPosition(position)
                        ?.let { holder ->
                            typeContents.add(TypeContent(
                            holder.itemView.type_name.text.toString(),
                            Color.parseColor(holder.itemView.color_selected.text.toString())
                        ))

                    }
                }

                requireView().let { view ->
                    sp.settingDao().savedTypeContents = typeContents
                    sp.settingDao().satVisible = view.switch_sat.isChecked
                    sp.settingDao().timeOrderMax =
                        view.spinner_order.selectedItemPosition + 1
                }

                parentFragmentManager.popBackStack()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Set recycler view adapter, and show the view
     * to regenerate recycler view
     */
    private fun setViewAdapter() {
        recyclerView.adapter = adapter
    }

}