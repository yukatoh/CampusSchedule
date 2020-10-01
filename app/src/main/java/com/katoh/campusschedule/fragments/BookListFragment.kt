package com.katoh.campusschedule.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.katoh.campusschedule.R
import com.katoh.campusschedule.fragments.dialogs.BookSettingDialogFragment
import com.katoh.campusschedule.models.entity.BookContent
import com.katoh.campusschedule.viewmodels.BookViewModel
import com.katoh.campusschedule.viewmodels.RealmResultViewModel
import com.katoh.campusschedule.views.adapters.BookRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_book_list.view.*

class BookListFragment : CustomFragment() {
    // Views
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookRecyclerAdapter

    // View Models
    private val realmViewModel: RealmResultViewModel by activityViewModels()
    private val bookViewModel: BookViewModel by activityViewModels()

    // Dialogs
    private val bookSettingDialogFragment = BookSettingDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_list, container, false)

        // Action Bar
        activity.supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.book_list)
        }

        recyclerView = view.recyclerview_book.apply {
            layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
        }
        setViewAdapter()

        bookSettingDialogFragment.setNoticeDialogListener(
            object : BookSettingDialogFragment.NoticeDialogListener {
                override fun onPositiveClick(dialog: DialogFragment, book: BookContent) {
                    realmViewModel.updateBookSetting(book)
                }
            })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        realmViewModel.selectedCourseData.observe(viewLifecycleOwner, Observer {
            setViewAdapter()
        })

        bookViewModel.bookLiveData.observe(viewLifecycleOwner, Observer { response ->
            Log.d("fetch-book", response.toString())
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    /**
     * Set recycler view adapter, and show the view
     * to generate recycler view
     */
    private fun setViewAdapter() {
        adapter = BookRecyclerAdapter(requireContext(), realmViewModel.courseResults)

        adapter.setOnItemClickListener(
            object : BookRecyclerAdapter.OnItemClickListener{
                override fun onClick(position: Int) {
                    // Update view model
                    val course = realmViewModel.courseResults[position]
                        ?: throw Exception("The course not found")
                    realmViewModel.chooseSelectedCourse(course.day, course.order)

                    // Show dialog
                    realmViewModel.initBook()
                    bookSettingDialogFragment.show(
                        parentFragmentManager, BookSettingDialogFragment.TAG_DEFAULT)
                }
            })

        adapter.setOnMenuItemClickListener(
            object : BookRecyclerAdapter.OnMenuItemClickListener{
                override fun onClick(menuItem: MenuItem, position: Int): Boolean {
                    // Update view model
                    val course = realmViewModel.courseResults[position]
                        ?: throw Exception("The course not found")
                    realmViewModel.chooseSelectedCourse(course.day, course.order)

                    return when (menuItem.itemId) {
                        R.id.edit -> {
                            // Show dialog
                            realmViewModel.initBook()
                            bookSettingDialogFragment.show(
                                parentFragmentManager, BookSettingDialogFragment.TAG_DEFAULT)
                            true
                        }
                        R.id.search -> {
                            // Replace fragment
                            realmViewModel.initBook()
                            bookViewModel.fetchBookData(realmViewModel.tempBook)
                            replaceFragment(R.id.container_main, BookSearchFragment())
                            true
                        }
                        else -> false
                    }
                }
            })

        // Set recycler view adapter
        recyclerView.adapter = adapter
    }
}