package com.katoh.campusschedule.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.katoh.campusschedule.views.adapters.BookSearchRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_book_search.view.*

class BookSearchFragment : CustomFragment() {
    // Activity
    private val activity: AppCompatActivity by lazy {
        getActivity() as AppCompatActivity
    }

    // Views
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookSearchRecyclerAdapter

    // View Models
    private val bookViewModel: BookViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        // Action Bar
        activity.supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.search_result)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_search,
            container, false)

        recyclerView = view.recyclerview_book_search.apply {
            layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookViewModel.bookLiveData.observe(viewLifecycleOwner, Observer { bookData ->
            view.num_search.text = bookData.items.size.toString()
            setViewAdapter()
        })

    }

    /**
     * Set recycler view adapter, and show the view
     * to generate recycler view
     */
    private fun setViewAdapter() {
        adapter = BookSearchRecyclerAdapter(requireContext(),
            bookViewModel.bookList)

        adapter.setOnItemClickListener(
            object : BookSearchRecyclerAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    // Intent
                    val uri = Uri.parse(bookViewModel.bookList.items[position].volumeInfo.infoLink)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    if (intent.resolveActivity(activity.packageManager) != null) {
                        startActivity(intent)
                    }
                }
            })

        // Set recycler view adapter
        recyclerView.adapter = adapter
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
}