package com.pukachkosnt.newstask.ui.listnews

import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.newstask.NewsNavGraphDirections
import com.pukachkosnt.newstask.R
import com.pukachkosnt.newstask.databinding.FragmentListNewsBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class BaseListNewsFragment : Fragment(), ArticleHolder.Callbacks {
    protected abstract val viewModel: BaseNewsViewModel
    protected lateinit var binding: FragmentListNewsBinding
    protected lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.addFavoritesOnError.collect {
                Toast.makeText(
                    context,
                    R.string.toast_article_not_added,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onFavoriteClicked(article: ArticleModel) {
        viewModel.onFavoriteClicked(article)
    }

    override fun onItemArticleClicked(article: ArticleModel) {
        val destination = NewsNavGraphDirections.actionToWebActivity(article.url)
        findNavController().navigate(destination)
    }

    protected open fun setupRecyclerView() {
        newsAdapter = NewsAdapter(layoutInflater, this)
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
            itemAnimator = DefaultItemAnimator().apply {
                supportsChangeAnimations = false
            }
            layoutManager = GridLayoutManager(
                context,
                resources.getInteger(R.integer.news_list_columns_count)
            )
            setHasFixedSize(true)
            adapter = newsAdapter
        }
    }
}