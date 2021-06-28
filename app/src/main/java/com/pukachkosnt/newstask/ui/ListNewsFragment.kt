package com.pukachkosnt.newstask.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.pukachkosnt.newstask.R
import com.pukachkosnt.newstask.databinding.FragmentListNewsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class ListNewsFragment : Fragment() {
    private lateinit var binding: FragmentListNewsBinding

    private lateinit var newsAdapter: NewsAdapter

    private lateinit var searchView: SearchView

    private val newsViewModel: NewsViewModel by viewModel()

    private lateinit var searchViewState: SearchViewState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        searchViewState = if (savedInstanceState == null) {
            SearchViewState()   // by default
        } else {
            SearchViewState(
                state = SearchViewState.State.valueOf(
                    savedInstanceState.getString(SAVED_SEARCH_STATE_KEY)
                        ?: SearchViewState.State.CLOSED.name
                ),
                searchQuery = savedInstanceState.getString(SAVED_SEARCH_QUERY_KEY) ?: ""
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentListNewsBinding.inflate(layoutInflater)

        binding.swipeRefreshListNews.setOnRefreshListener {       //  setup refreshing
            newsViewModel.fetchNews()
            searchView.apply {
                setQuery("", false)
                isIconified = true
            }
            binding.swipeRefreshListNews.isRefreshing = false
        }
        setupRecyclerView()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_news_list, menu)

        val searchItem = menu.findItem(R.id.menu_item_search_news)
        searchView = searchItem.actionView as SearchView

        searchView.apply {      // setting up searchView
            setQuery(searchViewState.searchQuery, false)
            when (searchViewState.state) {
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
                    newsViewModel.filterNews(query ?: "")
                    clearFocus()
                    return false
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    updateSearchViewState(searchQuery = newText ?: "")
                    return false
                }
            })

            setOnCloseListener {
                if (newsViewModel.listState is ListState.Filtered)
                    newsViewModel.clearFilter()
                false
            }

            setOnQueryTextFocusChangeListener { _, hasFocus ->
                updateSearchViewState(hasFocus = hasFocus)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save searchView state
        outState.putString(SAVED_SEARCH_STATE_KEY, searchViewState.state.name)
        outState.putString(SAVED_SEARCH_QUERY_KEY, searchViewState.searchQuery)
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(layoutInflater)
        newsAdapter.addLoadStateListener {
            if (it.refresh == LoadState.Loading) {
                binding.recyclerViewListNews.isVisible = false
                binding.textViewNothingFound.isVisible = false
                binding.progressBarLoad.isVisible = true       // show pBar
            } else {
                binding.textViewNothingFound.isVisible = newsAdapter.itemCount == 0
                binding.recyclerViewListNews.isVisible = true
                binding.progressBarLoad.isVisible = false
            }
        }

        binding.recyclerViewListNews.apply {
            layoutManager =  GridLayoutManager(
                context,
                resources.getInteger(R.integer.news_list_columns_count)
            )
            setHasFixedSize(true)
            adapter = newsAdapter
        }

        newsViewModel.newsItemsLiveData.observe(    // submit data to an adapter
            viewLifecycleOwner,
            {
                it?.let {
                    if (newsViewModel.listState is ListState.Filtered) {
                        binding.textViewNothingFound.isVisible =
                            (newsViewModel.listState as ListState.Filtered).isEmpty
                    }
                    val isEmpty = newsAdapter.itemCount == 0
                    newsAdapter.submitData(lifecycle, it)

                    // if it's not empty, scroll to top
                    if (!isEmpty) {
                        // Scroll to top on PreDraw event, because it is only way to detect
                        // if adapter has processed submitted data
                        binding.recyclerViewListNews.doOnPreDraw {
                            binding.recyclerViewListNews.scrollToPosition(0)
                        }
                    }
                }
            }
        )
    }

    fun updateSearchViewState(
        hasFocus: Boolean? = null,
        searchQuery: String? = null
    ) {
        var state = searchViewState.state
        var query = searchViewState.searchQuery

        hasFocus?.let {
            state = if (it) {
                SearchViewState.State.FOCUSED_WITH_KEYBOARD
            } else {
                SearchViewState.State.UNFOCUSED
            }
        }
        searchQuery?.let { query = it }

        searchViewState = searchViewState.copy(state = state, searchQuery = query)
    }

    companion object {
        private const val TAG = "ListNewsFragment"
        private const val SAVED_SEARCH_STATE_KEY: String = "SEARCH_VIEW_STATE"
        private const val SAVED_SEARCH_QUERY_KEY: String = "SEARCH_VIEW_QUERY"

        fun newInstance() = ListNewsFragment()
    }
}