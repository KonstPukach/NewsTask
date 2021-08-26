package com.pukachkosnt.newstask.ui.listnews.all

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.pukachkosnt.newstask.R
import com.pukachkosnt.newstask.databinding.FragmentListNewsBinding
import com.pukachkosnt.newstask.extensions.convertToPx
import com.pukachkosnt.newstask.ui.dialog.choosesource.BottomSheetChooseSourceDialogFragment
import com.pukachkosnt.newstask.ui.dialog.choosesource.BottomSheetChooseSourceDialogFragment.Companion.KEY_CLOSE_TYPE
import com.pukachkosnt.newstask.ui.dialog.chooseoption.BottomSheetChooseFromListDialogFragment.Companion.CHOOSE_FROM_LIST_DIALOG_TAG
import com.pukachkosnt.newstask.ui.listnews.BaseListNewsFragment
import com.pukachkosnt.newstask.ui.listnews.ListState
import org.koin.androidx.viewmodel.ext.android.viewModel


class ListNewsFragment : BaseListNewsFragment() {
    private lateinit var searchView: SearchView

    override val viewModel: ListNewsViewModel by viewModel()

    private lateinit var searchViewState: SearchViewState

    private var callbacks: Callbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(F_RESULT_DELETED_ITEMS) { _, bundle ->
            val deletedItemsSet: HashSet<String> =
                bundle.getStringArrayList(KEY_DELETED_ITEMS)?.toHashSet() ?: hashSetOf()
            viewModel.refreshFavoriteArticles(deletedItemsSet)
        }

        setFragmentResultListener(F_RESULT_SOURCES) { _, bundle ->
            val closeType = bundle.getSerializable(KEY_CLOSE_TYPE)
                    as BottomSheetChooseSourceDialogFragment.CloseType
            if (closeType == BottomSheetChooseSourceDialogFragment.CloseType.SAVE) {
                viewModel.refreshSources()
                viewModel.fetchNews()
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
        binding.btnSource.isVisible = true
        binding.swipeRefreshListNews.setOnRefreshListener {       //  setup refreshing
            viewModel.fetchNews()
            searchView.apply {
                setQuery("", false)
                isIconified = true
            }
            binding.swipeRefreshListNews.isRefreshing = false
            scrollToTop()
        }

        binding.btnSource.setOnClickListener {
            findNavController().navigate(R.id.action_to_choose_source_dialog)
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

        val settingsItem = menu.findItem(R.id.menu_item_settings)
        settingsItem.setOnMenuItemClickListener {
            callbacks?.onSettingsItemActionBarClicked()
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
                    scrollToTop()
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
                if (viewModel.newsItemsLiveData.value is ListState.Filtered) {
                    viewModel.clearFilter()
                    scrollToTop()
                }
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
                    } else {
                        binding.textViewNothingFound.isVisible = false
                    }
                    newsAdapter.submitData(lifecycle, it.data)
                }
            }
        )
    }

    private fun updateSearchViewState(
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

    private fun scrollToTop() {
        // scroll to the top when recyclerView has received data
        binding.recyclerViewListNews.doOnPreDraw {
            binding.recyclerViewListNews.scrollToPosition(0)
        }
    }

    interface Callbacks {
        fun onFavoriteItemActionBarClicked()

        fun onSettingsItemActionBarClicked()
    }

    companion object {
        private const val TAG = "ListNewsFragment"
        private const val SAVED_SEARCH_STATE_KEY = "SEARCH_VIEW_STATE"
        private const val SAVED_SEARCH_QUERY_KEY = "SEARCH_VIEW_QUERY"

        const val F_RESULT_DELETED_ITEMS = "F_RESULT_DELETED_ITEMS"
        const val F_RESULT_SOURCES = "F_RESULT_SOURCES"
        const val KEY_DELETED_ITEMS = "KEY_DELETED_ITEMS"

        private const val MAX_SEARCH_VIEW_WIDTH_DP = 250f // dp
    }
}