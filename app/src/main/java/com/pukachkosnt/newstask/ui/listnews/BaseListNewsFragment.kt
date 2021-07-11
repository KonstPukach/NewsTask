package com.pukachkosnt.newstask.ui.listnews

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.newstask.R
import com.pukachkosnt.newstask.databinding.FragmentListNewsBinding

abstract class BaseListNewsFragment : Fragment(), ArticleHolder.Callbacks {
    protected abstract val viewModel: BaseNewsViewModel
    protected lateinit var binding: FragmentListNewsBinding
    protected lateinit var newsAdapter: NewsAdapter

    override fun onFavoriteClicked(article: ArticleModel) {
        if (article.isFavorite) {
            viewModel.addFavoriteArticle(article)
        } else {
            viewModel.deleteFavoriteArticle(article)
        }
    }

    protected open fun setupRecyclerView() {
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
            layoutManager = GridLayoutManager(
                context,
                resources.getInteger(R.integer.news_list_columns_count)
            )
            setHasFixedSize(true)
            adapter = newsAdapter
        }
    }
}