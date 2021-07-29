package com.pukachkosnt.newstask.ui.listnews.all

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import com.pukachkosnt.newstask.R
import com.pukachkosnt.newstask.databinding.FragmentListNewsBinding
import com.pukachkosnt.newstask.extensions.convertToPx
import com.pukachkosnt.newstask.ui.listnews.BaseListNewsFragment
import com.pukachkosnt.newstask.ui.listnews.ListState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ListNewsFragment : BaseListNewsFragment() {
    private lateinit var searchView: SearchView

    override val viewModel: NewsViewModel by viewModel()

    private lateinit var searchViewState: SearchViewState

    private var callbacks: Callbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(F_RESULT_DELETED_ITEMS) { _, bundle ->
            val deletedItemsSet: HashSet<Long> = bundle.getSerializable(KEY_DELETED_ITEMS) as HashSet<Long>
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.refreshFavoriteArticles(deletedItemsSet)
                CoroutineScope(Dispatchers.Main).launch {
                    newsAdapter.notifyDataSetChanged()
                }
            }
        }

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
            viewModel.fetchNews()
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

        val favoritesItem = menu.findItem(R.id.menu_item_favorites)
        favoritesItem.setOnMenuItemClickListener {
            callbacks?.onFavoriteItemActionBarClicked()
            true
        }

        val searchItem = menu.findItem(R.id.menu_item_search_news)
        searchView = searchItem.actionView as SearchView
        searchView.maxWidth = convertToPx(MAX_SEARCH_VIEW_WIDTH_DP, context?.resources).toInt()

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
                    viewModel.filterNews(query ?: "")
                    clearFocus()
                    return false
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    updateSearchViewState(searchQuery = newText ?: "")
                    return false
                }
            })

            setOnCloseListener {
                updateSearchViewState(closed = true)
                if (viewModel.newsItemsLiveData.value is ListState.Filtered)
                    viewModel.clearFilter()
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun setupRecyclerView() {
        super.setupRecyclerView()
        viewModel.newsItemsLiveData.observe(    // submit data to an adapter
            viewLifecycleOwner,
            {
                it?.let {
                    if (viewModel.newsItemsLiveData.value is ListState.Filtered) {
                        binding.textViewNothingFound.isVisible =
                            (viewModel.newsItemsLiveData.value as ListState.Filtered).isEmpty
                    }
                    val isEmpty = newsAdapter.itemCount == 0
                    newsAdapter.submitData(lifecycle, it.data)

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
        searchQuery: String? = null,
        closed: Boolean? = null
    ) {
        var state = searchViewState.state
        var query = searchViewState.searchQuery

        closed?.let {
            state = SearchViewState.State.CLOSED
        }

        hasFocus?.let {
            state = if (it) {
                SearchViewState.State.FOCUSED_WITH_KEYBOARD
            } else {
                if (state != SearchViewState.State.CLOSED)
                    SearchViewState.State.UNFOCUSED
                else
                    SearchViewState.State.CLOSED
            }
        }
        searchQuery?.let { query = it }

        searchViewState = searchViewState.copy(state = state, searchQuery = query)
    }

    interface Callbacks {
        fun onFavoriteItemActionBarClicked()
    }

    companion object {
        private const val TAG = "ListNewsFragment"
        private const val SAVED_SEARCH_STATE_KEY = "SEARCH_VIEW_STATE"
        private const val SAVED_SEARCH_QUERY_KEY = "SEARCH_VIEW_QUERY"

        const val F_RESULT_DELETED_ITEMS = "F_RESULT_DELETED_ITEMS"
        const val KEY_DELETED_ITEMS = "KEY_DELETED_ITEMS"

        private const val MAX_SEARCH_VIEW_WIDTH_DP = 250f // dp
    }
}