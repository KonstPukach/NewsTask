package com.pukachkosnt.newstask

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pukachkosnt.newstask.animations.hideMoreTextViewAnimation
import com.pukachkosnt.newstask.animations.showMoreTextViewAnimation
import com.pukachkosnt.newstask.models.Article
import com.squareup.picasso.Picasso


private const val MAX_ITEM_WIDTH = 600

class ListNewsFragment : Fragment() {
    private lateinit var recyclerViewNews: RecyclerView
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBarLoad: ProgressBar
    private var pageCounter = 0
    private var currentSearchQuery = ""

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

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_list_news)
        swipeRefreshLayout.setOnRefreshListener {
            pageCounter = 0
            newsViewModel = NewsViewModel(currentSearchQuery)
            isUpdated = true
            recyclerViewNews.adapter?.notifyDataSetChanged()
        }

        recyclerViewNews = view.findViewById(R.id.recycler_view_list_news)
        setupRecyclerView()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_news_list, menu)

        val searchItem = menu.findItem(R.id.menu_item_search_news)
        val searchView = searchItem.actionView as SearchView

        fun onSearch(query: String): Boolean {
            pageCounter = 0
            currentSearchQuery = query
            newsViewModel = NewsViewModel(currentSearchQuery)
            isUpdated = true
            recyclerViewNews.adapter?.notifyDataSetChanged()
            hideKeyboard()
            return true
        }

        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return onSearch(query ?: "")
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
            setOnCloseListener {
                onSearch("")
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

    private var isUpdated = false       // true - if adapter has been updated
    private fun setupRecyclerView() {
        recyclerViewNews.viewTreeObserver.addOnGlobalLayoutListener {
            if (recyclerViewNews.layoutManager == null || isUpdated) {
                progressBarLoad.visibility = View.VISIBLE
                isUpdated = false
                val columns: Int = recyclerViewNews.width / MAX_ITEM_WIDTH // count columns number
                recyclerViewNews.apply {
                    layoutManager = if (columns < 2) {  // set layout type
                        LinearLayoutManager(context)
                    } else {
                        GridLayoutManager(context, columns)
                    }
                    setHasFixedSize(true)
                    newsAdapter = NewsAdapter()
                    adapter = newsAdapter
                    newsViewModel.newsItemsLiveData.observe(
                        viewLifecycleOwner,
                        {
                            pageCounter++
                            if (pageCounter < 7) {
                                it?.let {
                                    newsAdapter.submitData(lifecycle, it)
                                }
                            }
                        }
                    )
                }
            }
        }
    }



    // ####### VIEW HOLDER #########
    private inner class ArticleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var showMoreState: Boolean = false  // false - not pressed, true - pressed
        val textViewTitle: TextView = itemView.findViewById(R.id.text_view_article_title)
        val imageView: ImageView = itemView.findViewById(R.id.image_view_article_img)
        val textViewDescription: TextView = itemView.findViewById(R.id.text_view_article_description)
        val textViewShowMore: TextView = itemView.findViewById(R.id.text_view_show_more)

        fun bind(article: Article) {
            initialSetupTranslation()
            showMoreState = false
            textViewTitle.text = article.title
            textViewDescription.text = article.description

            // set "show more" when layout parameters are known
            textViewDescription.viewTreeObserver.addOnGlobalLayoutListener {
                if (textViewDescription.lineCount > 3) {    // show more if lines > 3
                    textViewShowMore.visibility = View.VISIBLE
                    textViewShowMore.setOnClickListener {
                        if (showMoreState) {
                            hideMore()
                        } else {
                            showMore()
                        }
                        showMoreState = !showMoreState
                    }
                }
            }

            Picasso.get()
                .load(article.urlToImage)
                .placeholder(R.drawable.background_article)
                .error(R.drawable.background_article)
                .resize(370, 160)
                .centerCrop()
                .into(imageView)
        }

        private fun initialSetupTranslation() {     // every item need to be set up in onBind()
            textViewDescription.translationY = 0f   // because animation changes item's Y coordinate
            textViewDescription.maxLines = 3
            textViewShowMore.translationY = 0f
            textViewShowMore.setText(R.string.show_more)
            textViewShowMore.setBackgroundColor(Color.WHITE)
        }

        private fun showMore() {
            textViewDescription.maxLines = 10
            textViewShowMore.setBackgroundColor(Color.TRANSPARENT)
            showMoreTextViewAnimation(textViewDescription, textViewShowMore)
            textViewTitle.visibility = View.INVISIBLE
            textViewShowMore.setText(R.string.hide_more)
        }

        private fun hideMore() {
            textViewDescription.maxLines = 3
            textViewShowMore.setBackgroundColor(Color.WHITE)
            hideMoreTextViewAnimation(textViewDescription, textViewShowMore)
            textViewTitle.visibility = View.VISIBLE
            textViewShowMore.setText(R.string.show_more)
        }
    }

    // ####### ADAPTER ##########
    private inner class NewsAdapter :
        PagingDataAdapter<Article, ArticleHolder>(REPO_COMPARATOR) {

        init {
            swipeRefreshLayout.isRefreshing = false  // stop refresh pBar
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder {
            val view = layoutInflater.inflate(R.layout.news_item, parent, false)
            return ArticleHolder(view)
        }

        override fun onBindViewHolder(holder: ArticleHolder, position: Int) {
            if (progressBarLoad.visibility == View.VISIBLE) {   // hide pBar
                recyclerViewNews.visibility = View.VISIBLE
                progressBarLoad.visibility = View.GONE
            }
            holder.bind(getItem(position)!!)
        }
    }

    companion object {
        fun newInstance() = ListNewsFragment()

        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }
}