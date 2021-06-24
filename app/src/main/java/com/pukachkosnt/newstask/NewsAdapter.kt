package com.pukachkosnt.newstask

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.pukachkosnt.domain.models.ArticleEntity

class NewsAdapter(private val layoutInflater: LayoutInflater) :
    PagingDataAdapter<ArticleEntity, ArticleHolder>(ARTICLE_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder {
        val view = layoutInflater.inflate(R.layout.news_item, parent, false)
        return ArticleHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    companion object {
        private val ARTICLE_COMPARATOR = object : DiffUtil.ItemCallback<ArticleEntity>() {
            override fun areItemsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }
}