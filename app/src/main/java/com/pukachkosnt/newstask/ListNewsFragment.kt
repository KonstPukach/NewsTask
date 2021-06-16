package com.pukachkosnt.newstask

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnPreDraw
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pukachkosnt.newstask.animations.*
import com.pukachkosnt.newstask.models.ArticleEntity
import com.squareup.picasso.Picasso


class ListNewsFragment : Fragment() {
    private lateinit var recyclerViewNews: RecyclerView
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBarLoad: ProgressBar
    private lateinit var textViewNothingFound: TextView
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        newsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
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
            // If data is filtered show full list and hide searchView
            if (newsViewModel.recyclerViewState.state == NewsRecyclerViewState.State.FILTERED) {
                newsViewModel.recyclerViewState.apply {
                    state = NewsRecyclerViewState.State.FULL
                    data = PagingData.empty()
                }
                newsViewModel.searchViewState.state = SearchViewState.State.CLOSED
                searchView.apply {
                    setQuery("", false)
                    isIconified = true
                }
            } else {
                newsAdapter.refresh()
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
            newsViewModel.searchViewState.searchQuery = query
            newsViewModel.recyclerViewState.apply {
                state = NewsRecyclerViewState.State.FILTERED
                data = PagingData.from(newsViewModel.recyclerViewItems.filter {
                    it.title.contains(query, true)
                })
            }
            newsAdapter.submitData(
                lifecycle,
                newsViewModel.recyclerViewState.data
            )
            hideKeyboard()
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
                    newsViewModel.searchViewState.state = SearchViewState.State.UNFOCUSED
                    Log.i(TAG, "SearchView state: UNFOCUSED")
                    onSearch(query ?: "")
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    newsViewModel.searchViewState.searchQuery = newText ?: ""
                    return false
                }
            })

            setOnCloseListener {
                // Submits empty data to scroll to the top of the list when user clicks close button.
                // If don't do this it stays on the last filter search position.
                newsAdapter.submitData(lifecycle, PagingData.empty())
                newsViewModel.searchViewState.state = SearchViewState.State.CLOSED
                newsViewModel.recyclerViewState.apply {
                    state = NewsRecyclerViewState.State.FULL
                    data = PagingData.empty()   // reset the list of news
                }
                Log.i(TAG, "SearchView state: CLOSED")
                newsViewModel.fetchNews()
                false
            }

            setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    newsViewModel.searchViewState.state = SearchViewState.State.FOCUSED_WITH_KEYBOARD
                    Log.i(TAG, "SearchView state: FOCUSED WITH KEYBOARD")
                } else if (newsViewModel.searchViewState.state != SearchViewState.State.CLOSED) {
                    newsViewModel.searchViewState.state = SearchViewState.State.UNFOCUSED
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

    private fun setupRecyclerView() {
        recyclerViewNews.viewTreeObserver.addOnGlobalLayoutListener {
            if (recyclerViewNews.layoutManager == null) {
                val columns: Int = recyclerViewNews.width / MAX_ITEM_WIDTH // count columns number
                recyclerViewNews.apply {
                    layoutManager =  GridLayoutManager(context, columns)

                    setHasFixedSize(true)

                    newsAdapter = NewsAdapter()
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
                            if (newsViewModel.recyclerViewState.state == NewsRecyclerViewState.State.FULL)
                                newsViewModel.recyclerViewItems = newsAdapter.snapshot().items
                            progressBarLoad.visibility = View.GONE
                        }
                    }

                    adapter = newsAdapter
                    newsViewModel.newsItemsLiveData.observe(    // submit data to an adapter
                        viewLifecycleOwner,
                        {
                            it?.let {
                                Log.i(TAG, "Submit data to the adapter")

                                when (newsViewModel.recyclerViewState.state) {
                                    NewsRecyclerViewState.State.FULL -> {       // submit data from
                                        newsAdapter.submitData(lifecycle, it)   // livedata
                                    }
                                    NewsRecyclerViewState.State.FILTERED -> {   // submit data from
                                        newsAdapter.submitData(                 // saved filtered state
                                            lifecycle,
                                            newsViewModel.recyclerViewState.data
                                        )
                                    }
                                }

                                swipeRefreshLayout.isRefreshing = false  // stop refresh pBar
                                recyclerViewNews.visibility = View.VISIBLE
                            }
                        }
                    )
                }
            }
        }
    }

    // ####### VIEW HOLDER #########
    private class ArticleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            private const val MAX_LINES_COLLAPSED = 3
            private const val MAX_LINES_EXPANDED = 10
            private const val TARGET_WIDTH = 370
            private const val TARGET_HEIGHT = 160
        }

        private var showMoreState: Boolean = false  // false - not pressed, true - pressed
        private val textViewTitle: TextView = itemView.findViewById(R.id.text_view_article_title)
        private val imageView: ImageView = itemView.findViewById(R.id.image_view_article_img)
        private val textViewDescription: TextView = itemView.findViewById(R.id.text_view_article_description)
        private val textViewShowMore: TextView = itemView.findViewById(R.id.text_view_show_more)
        private val linLayout: LinearLayout = itemView.findViewById(R.id.lin_layout_description)

        init {
            // set "show more" when layout parameters are known
            // measures view, when the text changes
            textViewDescription.doAfterTextChanged {
                setupShowMore()
            }
            // measures the view, when the view was drawn
            textViewDescription.doOnPreDraw {
                setupShowMore()
            }

            textViewShowMore.setOnClickListener {
                if (showMoreState) {
                    hideMore()
                } else {
                    showMore()
                }
                showMoreState = !showMoreState
            }
        }

        fun bind(article: ArticleEntity) {
            initialSetupTranslation()
            showMoreState = false
            textViewTitle.text = article.title
            textViewDescription.text = article.description

            Picasso.get()
                .load(article.urlToImage)
                .placeholder(R.drawable.background_article)
                .error(R.drawable.background_article)
                .resize(TARGET_WIDTH, TARGET_HEIGHT)
                .centerCrop()
                .into(imageView)
        }

        private fun setupShowMore() {
            if (textViewDescription.width > 0) {
                if (textViewDescription.lineCount > MAX_LINES_COLLAPSED) {
                    textViewShowMore.visibility = View.VISIBLE
                    Log.i(TAG, "Drawn textView: ${ textViewTitle.text }")
                }
            }
        }

        private fun initialSetupTranslation() {     // every item need to be set up in onBind()
            linLayout.translationY = START_Y_POSITION   // because animation changes item's Y coordinate
            textViewDescription.maxLines = MAX_LINES_COLLAPSED
            textViewTitle.visibility = View.VISIBLE
            textViewShowMore.setText(R.string.show_more)
        }

        private fun showMore() {
            textViewDescription.maxLines = MAX_LINES_EXPANDED
            showMoreTextViewAnimation(linLayout, textViewDescription.lineCount)
            textViewTitle.visibility = View.INVISIBLE
            textViewShowMore.setText(R.string.hide_more)
        }

        private fun hideMore() {
            textViewDescription.maxLines = MAX_LINES_COLLAPSED
            hideMoreTextViewAnimation(linLayout)
            textViewTitle.visibility = View.VISIBLE
            textViewShowMore.setText(R.string.show_more)
        }
    }

    // ####### ADAPTER ##########
    private inner class NewsAdapter :
        PagingDataAdapter<ArticleEntity, ArticleHolder>(REPO_COMPARATOR) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder {
            val view = layoutInflater.inflate(R.layout.news_item, parent, false)
            return ArticleHolder(view)
        }

        override fun onBindViewHolder(holder: ArticleHolder, position: Int) {
            holder.bind(getItem(position)!!)
        }
    }


    companion object {
        private const val MAX_ITEM_WIDTH = 600
        private const val TAG = "ListNewsFragment"

        fun newInstance() = ListNewsFragment()

        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<ArticleEntity>() {
            override fun areItemsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }
}