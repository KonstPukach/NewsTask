package com.pukachkosnt.newstask

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ListNewsFragment : Fragment() {
    companion object {
        private const val MAX_ITEM_WIDTH = 600
        private const val TAG = "ListNewsFragment"

        fun newInstance() = ListNewsFragment()
    }

    private lateinit var recyclerViewNews: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBarLoad: ProgressBar
    private lateinit var textViewNothingFound: TextView
    private lateinit var searchView: SearchView

    private val newsViewModel: NewsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list_news, container, false)

        progressBarLoad = view.findViewById(R.id.progress_bar_load)
        textViewNothingFound = view.findViewById(R.id.text_view_nothing_found)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_list_news)

        swipeRefreshLayout.setOnRefreshListener {       //  setup refreshing
            newsViewModel.fetchNews()
            searchView.apply {
                setQuery("", false)
                isIconified = true
            }
            recyclerViewNews.visibility = View.GONE
        }

        recyclerViewNews = view.findViewById(R.id.recycler_view_list_news)
        setupRecyclerView()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_news_list, menu)

        val searchItem = menu.findItem(R.id.menu_item_search_news)
        searchView = searchItem.actionView as SearchView

        fun onSearch(query: String) {
            textViewNothingFound.visibility = View.GONE
            newsViewModel.filterNews(query)
            scrollToFirst = true
            hideKeyboard()
        }

        searchView.apply {      // setting up searchView
            setQuery(newsViewModel.getSearchViewState.searchQuery, false)
            when (newsViewModel.getSearchViewState.state) {
                SearchViewState.State.UNFOCUSED -> {
                    isIconified = false
                    clearFocus()
                }
                SearchViewState.State.FOCUSED_WITH_KEYBOARD -> {
                    isIconified = false
                }
                SearchViewState.State.CLOSED -> {
                    isIconified = true
                    clearFocus()
                }
            }

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.i(TAG, "SearchView state: UNFOCUSED")
                    onSearch(query ?: "")
                    return false
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    newsViewModel.getSearchViewState.searchQuery = newText ?: ""
                    return false
                }
            })


            setOnCloseListener {
                Log.i(TAG, "SearchView state: CLOSED")
                recyclerViewNews.visibility = View.GONE
                newsViewModel.fetchNews()
                scrollToFirst = true
                false
            }

            setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    newsViewModel.getSearchViewState.state = SearchViewState.State.FOCUSED_WITH_KEYBOARD
                    Log.i(TAG, "SearchView state: FOCUSED WITH KEYBOARD")
                } else if (newsViewModel.getSearchViewState.state != SearchViewState.State.CLOSED) {
                    newsViewModel.getSearchViewState.state = SearchViewState.State.UNFOCUSED
                    Log.i(TAG, "SearchView state: UNFOCUSED")
                }
            }
        }
    }

    private fun hideKeyboard() {
        val inputManager = activity
            ?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity?.currentFocus
        if (view == null) {
            view = View(activity)
        }
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private var scrollToFirst = false
    private fun setupRecyclerView() {
        recyclerViewNews.viewTreeObserver.addOnGlobalLayoutListener {
            if (recyclerViewNews.layoutManager == null) {
                val columns: Int = recyclerViewNews.width / MAX_ITEM_WIDTH // count columns number
                recyclerViewNews.apply {
                    layoutManager =  GridLayoutManager(context, columns)
                    setHasFixedSize(true)
                    adapter = newsAdapter
                }
            }
            if (scrollToFirst) {
                scrollToFirst = false
                recyclerViewNews.scrollToPosition(0)
            }
        }

        newsAdapter = NewsAdapter(layoutInflater)
        newsAdapter.addLoadStateListener {
            if (it.refresh == LoadState.Loading) {
                progressBarLoad.visibility = View.VISIBLE       // show pBar
                textViewNothingFound.visibility = View.GONE     // hide others
            } else {
                if (newsAdapter.itemCount == 0) {
                    textViewNothingFound.visibility = View.VISIBLE  // if list is empty
                } else {                                            // show "Nothing changed"
                    recyclerViewNews.visibility = View.VISIBLE      // if it's not empty
                    textViewNothingFound.visibility = View.GONE     // show a list, hide others
                }
                if (newsViewModel.getRecyclerViewState.state == NewsRecyclerViewState.State.FULL)
                    newsViewModel.recyclerViewItems = newsAdapter.snapshot().items
                progressBarLoad.visibility = View.GONE
            }
        }

        newsViewModel.newsItemsLiveData.observe(    // submit data to an adapter
            viewLifecycleOwner,
            {
                it?.let {
                    Log.i(TAG, "Submit data to the adapter")
                    if (newsViewModel.getRecyclerViewState.state == NewsRecyclerViewState.State.FILTERED) {
                        if (newsViewModel.getRecyclerViewState.isEmpty) {
                            textViewNothingFound.visibility = View.VISIBLE
                        } else {
                            textViewNothingFound.visibility = View.GONE
                        }
                    }
                    newsAdapter.submitData(lifecycle, it)
                    swipeRefreshLayout.isRefreshing = false  // stop refresh pBar
                }
            }
        )
    }
}