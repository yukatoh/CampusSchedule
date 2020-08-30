package com.katoh.campusschedule.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.katoh.campusschedule.R
import com.katoh.campusschedule.fragments.dialogs.CreateDialogFragment
import com.katoh.campusschedule.fragments.dialogs.DeleteDialogFragment
import com.katoh.campusschedule.fragments.dialogs.TermSettingDialogFragment
import com.katoh.campusschedule.viewmodels.RealmResultViewModel
import com.katoh.campusschedule.views.actionbar.StartActionModeCallback
import com.katoh.campusschedule.views.adapters.StartSelectableAdapter

class StartFragment : CustomFragment() {
    private val activity: AppCompatActivity by lazy {
        getActivity() as AppCompatActivity
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StartSelectableAdapter

    private var actionMode: ActionMode? = null
    private val actionModeCallback = StartActionModeCallback()

    private val createDialogFragment = CreateDialogFragment()
    private val deleteDialogFragment = DeleteDialogFragment()
    private val termSettingDialogFragment = TermSettingDialogFragment()

    private val model: RealmResultViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        // Set Event Listener
        actionModeCallback.setActionModeListener(
            object : StartActionModeCallback.ActionModeListener {
                override fun onDestroyActionMode() {
                    adapter.clearSelection()
                    actionMode = null
                    activity.supportActionBar?.show()
                }

                override fun onActionItemClicked(
                    mode: ActionMode?, item: MenuItem?): Boolean {
                    return when (item?.itemId) {
                        R.id.delete -> {
                            // Activate dialog fragment
                            deleteDialogFragment.show(
                                parentFragmentManager, DeleteDialogFragment.TAG_CAM)
                            true
                        }
                        else -> false
                    }
                }
            })

        createDialogFragment.setNoticeDialogListener(
            object : CreateDialogFragment.NoticeDialogListener {
                override fun onPositiveClick(dialog: DialogFragment) {
                    // Replace fragment
                    replaceFragment(R.id.container_main, TimeTabFragment())
                }
            })

        deleteDialogFragment.setNoticeDialogListener(
            object : DeleteDialogFragment.NoticeDialogListener {
                override fun onPositiveClick(dialog: DialogFragment) {
                    when (dialog.tag) {
                        DeleteDialogFragment.TAG_POPUP -> {
                            // Delete the term from DB
                            Log.d("delete-from-popup", model.selectedTerm.id.toString())
                            model.deleteSelectedTerm()
                        }
                        DeleteDialogFragment.TAG_CAM -> {
                            val selectedIds = adapter.getSelectedItemPositions()
                                .mapNotNull { position -> model.termResults[position]?.id }
                            selectedIds.forEach { id ->
                                model.chooseSelectedTerm(id)
                                Log.d("delete-from-cam", id.toString())
                                model.deleteSelectedTerm()
                            }
                            actionMode?.finish()
                        }
                    }
                    setViewAdapter()
                }
            })

        termSettingDialogFragment.setNoticeDialogListener(
            object : TermSettingDialogFragment.NoticeDialogListener {
                override fun onPositiveClick(dialog: DialogFragment) {
                }
            })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_start, container, false)

        // Action Bar
        activity.supportActionBar?.run {
            setTitle(R.string.app_name)
            setDisplayHomeAsUpEnabled(false)
        }

        // Recycler View
        recyclerView = view.findViewById(R.id.recyclerview_start)
        recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false)
        recyclerView.itemAnimator = DefaultItemAnimator()
        adapter = StartSelectableAdapter(requireContext(), model.termResults)
        setViewAdapter()

        // Set Event Listener
        adapter.setOnItemClickListener(
            object : StartSelectableAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    if (actionMode != null) {
                        toggleSelection(position)
                    } else {
                        actionMode?.finish()
                        // Update model
                        model.chooseSelectedTerm(model.termResults[position]?.id
                            ?: throw Exception("selectedId is invalid"))
                        // Replace fragment
                        replaceFragment(R.id.container_main, TimeTabFragment())
                    }
                }

                override fun onLongClick(position: Int): Boolean {
                    if (actionMode == null) {
                        // Activate Context Action Menu (CAM)
                        actionMode = activity.startSupportActionMode(actionModeCallback)
                        activity.supportActionBar?.hide()
                        toggleSelection(position)
                    }
                    return true
                }

        })

        adapter.setOnMenuItemClickListener(
            object : StartSelectableAdapter.OnMenuItemClickListener {
                override fun onClick(menuItem: MenuItem, position: Int): Boolean {
                    // Update model
                    model.chooseSelectedTerm(model.termResults[position]?.id
                        ?: throw Exception("selectedId is invalid"))
                    return when (menuItem.itemId) {
                        R.id.edit -> {
                            // Replace fragment
                            replaceFragment(R.id.container_main, TimeTabFragment())
                            true
                        }
                        R.id.delete -> {
                            // Activate dialog fragment
                            deleteDialogFragment.show(
                                parentFragmentManager, DeleteDialogFragment.TAG_POPUP)
                            true
                        }
                        R.id.property -> {
                            // Activate dialog
                            termSettingDialogFragment.show(
                                parentFragmentManager, TermSettingDialogFragment.TAG_DEFAULT)
                            true
                        }
                        else -> false
                    }
                }
            })

        val fab = view.findViewById<FloatingActionButton>(R.id.fab_start)
        fab.setOnClickListener {
            actionMode?.finish()
            // Activate dialog
            createDialogFragment.show(
                parentFragmentManager, TermSettingDialogFragment.TAG_DEFAULT)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.selectedTermData.observe(viewLifecycleOwner, Observer {
            setViewAdapter()
            actionMode?.finish()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_start, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.genaral_setting -> {
                // Replace fragment
                replaceFragment(R.id.container_main, GeneralSettingFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionMode?.finish()
    }

    /**
     * Set recycler view adapter, and show the view
     * to regenerate recycler view
     */
    private fun setViewAdapter() {
        recyclerView.adapter = adapter
    }

    /**
     * Toggle the selection status of the item at a given position
     */
    private fun toggleSelection(position: Int) {
        adapter.toggleSelection(position)
        val count = adapter.selectedItemCount
        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

}