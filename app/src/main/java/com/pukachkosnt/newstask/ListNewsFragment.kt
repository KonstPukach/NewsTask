package com.pukachkosnt.newstask

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.pukachkosnt.newstask.databinding.FragmentListNewsBinding
import com.pukachkosnt.newstask.extensions.hideKeyboard
import org.koin.androidx.viewmodel.ext.android.viewModel


class ListNewsFragment : Fragment() {
    companion object {
        private const val MAX_ITEM_WIDTH = 600
        private const val TAG = "ListNewsFragment"

        fun newInstance() = ListNewsFragment()
    }

    private lateinit var binding: FragmentListNewsBinding

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var searchView: SearchView

    private val newsViewModel: NewsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsViewModel.rotate()
        setHasOptionsMenu(true)
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
            binding.recyclerViewListNews.visibility = View.GONE
        }

        setupRecyclerView()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_news_list, menu)

        val searchItem = menu.findItem(R.id.menu_item_search_news)
        searchView = searchItem.actionView as SearchView


        fun onSearch(query: String) {
            binding.textViewNothingFound.visibility = View.GONE
            newsViewModel.filterNews(query)
            activity?.hideKeyboard()
        }

        searchView.apply {      // setting up searchView
            setQuery(newsViewModel.searchViewState.searchQuery, false)
            when (newsViewModel.searchViewState.state) {
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
                    newsViewModel.updateSearchViewState(searchQuery = newText ?: "")
                    return false
                }
            })


            setOnCloseListener {
                Log.i(TAG, "SearchView state: CLOSED")
                if (newsViewModel.recyclerViewState.state != NewsRecyclerViewState.State.FULL)
                    newsViewModel.filterNews("")
                false
            }

            setOnQueryTextFocusChangeListener { _, hasFocus ->
                newsViewModel.updateSearchViewState(hasFocus = hasFocus)
            }
        }
    }


    private fun setupRecyclerView() {
        binding.recyclerViewListNews.viewTreeObserver.addOnGlobalLayoutListener {
            if (binding.recyclerViewListNews.layoutManager == null) {
                val columns: Int = binding.recyclerViewListNews.width / MAX_ITEM_WIDTH // count columns number
                binding.recyclerViewListNews.apply {
                    layoutManager =  GridLayoutManager(context, columns)
                    setHasFixedSize(true)
                    adapter = newsAdapter
                }
            }
        }

        newsAdapter = NewsAdapter(layoutInflater)
        newsAdapter.addLoadStateListener {
            if (it.refresh == LoadState.Loading) {
                binding.progressBarLoad.visibility = View.VISIBLE       // show pBar
                binding.textViewNothingFound.visibility = View.GONE     // hide others
            } else {
                binding.textViewNothingFound.isVisible = newsAdapter.itemCount == 0
                binding.recyclerViewListNews.visibility = View.VISIBLE
                binding.progressBarLoad.visibility = View.GONE
            }
        }

        newsViewModel.newsItemsLiveData.observe(    // submit data to an adapter
            viewLifecycleOwner,
            {
                it?.let {
                    if (newsViewModel.recyclerViewState.state == NewsRecyclerViewState.State.FILTERED) {
                        binding.textViewNothingFound.isVisible = newsViewModel.recyclerViewState.isEmpty
                    }
                    newsAdapter.submitData(lifecycle, it)
                    if (!newsViewModel.rotated) {
                        binding.recyclerViewListNews.post {
                            binding.recyclerViewListNews.scrollToPosition(0)
                        }
                    }
                    binding.swipeRefreshListNews.isRefreshing = false  // stop refresh pBar
                }
            }
        )
    }
}