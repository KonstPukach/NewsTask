package com.pukachkosnt.newstask.ui.listnews

import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.newstask.R
import com.pukachkosnt.newstask.databinding.FragmentListNewsBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect


abstract class BaseNewsFragment : Fragment(), ArticleHolder.Callbacks {
    protected abstract val viewModel: BaseNewsViewModel
    protected lateinit var binding: FragmentListNewsBinding
    protected lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.addFavoritesState.collect {
                if (it.isFailure) {
                    Toast.makeText(
                        context,
                        R.string.toast_article_not_added,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onFavoriteClickedAsync(article: ArticleModel) {
        viewModel.onFavoriteClicked(article)
    }

    protected open fun setupRecyclerView() {
        newsAdapter = NewsAdapter(layoutInflater)
        newsAdapter.addLoadStateListener {
            val isLoading = it.refresh is LoadState.Loading
            binding.textViewNothingFound.isVisible = if (isLoading) {
                false
            } else {
                newsAdapter.itemCount == 0
            }
            binding.recyclerViewListNews.isVisible = !isLoading
            binding.progressBarLoad.isVisible = isLoading
        }

        binding.recyclerViewListNews.apply {
            layoutManager = GridLayoutManager(
                context,
                resources.getInteger(R.integer.news_list_columns_count)
            )
            setHasFixedSize(true)
            adapter = newsAdapter
        }
    }
}