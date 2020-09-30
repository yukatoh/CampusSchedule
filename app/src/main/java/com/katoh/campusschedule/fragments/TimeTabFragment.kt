package com.katoh.campusschedule.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.katoh.campusschedule.R
import kotlinx.android.synthetic.main.fragment_time_tab.view.*

class TimeTabFragment : CustomFragment() {
    // Activity
    private val activity: AppCompatActivity by lazy {
        getActivity() as AppCompatActivity
    }

    /**
     * Whether the device is tablet or not
     */
    private val isTablet: Boolean by lazy {
        resources.getBoolean(R.bool.is_tablet)
    }

    private enum class IndexItem(val tabTextId: Int) {
        // View paper items with each fragment instance
        // and string id of the tab text
        TABLE(R.string.table) {
            override fun newInstance() = TimeTableFragment()
        },
        LIST(R.string.list) {
            override fun newInstance() = TimeListFragment()
        };

        abstract fun newInstance() : Fragment
    }

    /**
     * A list of view paper items to define orders
     */
    private val indexItems = listOf(
        IndexItem.TABLE, IndexItem.LIST
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_time_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isTablet) return
        setViewPaperItems(view)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_tab, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
                true
            }
            R.id.book_list -> {
                // Replace fragment
                replaceFragment(R.id.container_main, BookListFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    /**
     * Set a view paper adapter and each tab layout
     */
    @Suppress("PLUGIN_WARNING")
    private fun setViewPaperItems(view: View) {
        if (!isTablet) {
            view.view_pager_time.adapter =
                object : FragmentStateAdapter(this) {
                    override fun getItemCount(): Int = indexItems.size

                    override fun createFragment(position: Int): Fragment {
                        return indexItems[position].newInstance()
                    }
                }
            view.view_pager_time.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            TabLayoutMediator(view.time_tab, view.view_pager_time) { tab, position ->
                tab.text = getString(indexItems[position].tabTextId)
            }.attach()
        }
    }

}