package com.katoh.campusschedule.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.katoh.campusschedule.R
import com.katoh.campusschedule.viewmodels.BookViewModel
import com.katoh.campusschedule.viewmodels.RealmResultViewModel
import com.katoh.campusschedule.views.adapters.BookRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_book_list.view.*

class BookListFragment : CustomFragment() {
    // Activity
    private val activity: AppCompatActivity by lazy {
        getActivity() as AppCompatActivity
    }

    // Views
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookRecyclerAdapter

    // View Models
    private val model: RealmResultViewModel by activityViewModels()
    private val bookViewModel: BookViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        // Action Bar
        activity.supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.book_list)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_list, container, false)

        recyclerView = view.recyclerview_book.apply {
            layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
        }
        setViewAdapter()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookViewModel.bookList.observe(viewLifecycleOwner, Observer { response ->
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
        adapter = BookRecyclerAdapter(requireContext(), model.courseResults)
        recyclerView.adapter = adapter
    }
}