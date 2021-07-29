package com.pukachkosnt.newstask.ui.listnews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.newstask.R

class NewsAdapter(
    private val layoutInflater: LayoutInflater,
    private val callbacks: ArticleHolder.Callbacks
) :
    PagingDataAdapter<ArticleModel, ArticleHolder>(ARTICLE_COMPARATOR) {

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
        private val ARTICLE_COMPARATOR = object : DiffUtil.ItemCallback<ArticleModel>() {
            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }
}