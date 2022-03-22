package com.pukachkosnt.newstask.ui.listnews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.pukachkosnt.newstask.R
import com.pukachkosnt.newstask.models.ArticleUiModel

class NewsAdapter(
    private val layoutInflater: LayoutInflater,
    private val callbacks: ArticleHolder.Callbacks
) : PagingDataAdapter<ArticleUiModel, ArticleHolder>(ARTICLE_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder {
        val view = layoutInflater.inflate(R.layout.news_item, parent, false)
        return ArticleHolder(view, callbacks)
    }

    override fun onBindViewHolder(holder: ArticleHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    companion object {
        private val ARTICLE_COMPARATOR = object : DiffUtil.ItemCallback<ArticleUiModel>() {
            override fun areItemsTheSame(oldItem: ArticleUiModel, newItem: ArticleUiModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ArticleUiModel, newItem: ArticleUiModel): Boolean {
                return newItem == oldItem
            }
        }
    }
}