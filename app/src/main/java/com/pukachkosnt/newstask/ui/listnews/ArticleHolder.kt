package com.pukachkosnt.newstask.ui.listnews

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pukachkosnt.newstask.R
import com.pukachkosnt.newstask.animations.moveViewPosition
import com.pukachkosnt.newstask.animations.scaleViewFromZero
import com.pukachkosnt.newstask.databinding.NewsItemBinding
import com.pukachkosnt.newstask.extensions.convertToPx
import com.pukachkosnt.newstask.extensions.toBeautifulLocalizedFormat
import com.pukachkosnt.newstask.models.ArticleUiModel
import com.pukachkosnt.newstask.utils.doEither
import com.pukachkosnt.newstask.utils.either
import com.squareup.picasso.Picasso
import java.util.*

class ArticleHolder(
    itemView: View,
    private val callbacks: Callbacks
) : RecyclerView.ViewHolder(itemView) {
    private val binding: NewsItemBinding = NewsItemBinding.bind(itemView)

    private var articleModel: ArticleUiModel? = null

    private val heartRedDrawable         = ResourcesCompat.getDrawable(itemView.resources, R.drawable.heart_red, null)
    private val heartTransparentDrawable = ResourcesCompat.getDrawable(itemView.resources, R.drawable.heart_transparent, null)

    private val translationToExpanded       get() = -binding.textViewArticleDescription.totalLinesInExpandedState * SINGLE_LINE_HEIGHT
    private val translationToExpandedMiddle get() = -(binding.textViewArticleDescription.totalLinesInExpandedState + 1) * SINGLE_LINE_HEIGHT

    init {
        binding.textViewArticleDescription.addOnClickListener {
            articleModel?.toggleCollapsed()?.doEither(t = ::hideMore, f = ::showMore)
        }

        binding.imageBtnFavorite.setOnClickListener {
            articleModel?.let(callbacks::onFavoriteClicked)
            scaleViewFromZero(it)
        }
    }

    fun bind(article: ArticleUiModel) {
        initializeArticleModel(article)

        with(binding) {
            textViewArticleTitle.text              = article.title
            textViewArticleDescription.initialText = article.description
            textViewArticleSource.text             = article.sourceName
            imageBtnFavorite.background            = article.isFavorite.either(t = heartRedDrawable, f = heartTransparentDrawable)
            textViewArticleDescription.isCollapsed = article.collapsed
            textViewArticlePublishedAt.text        =
                article.publishedAt.toBeautifulLocalizedFormat(
                    Locale.getDefault().language,
                    itemView.resources.getStringArray(R.array.months)
                )

            imageViewArticleImg.setOnClickListener { callbacks.onItemArticleClicked(article) }
        }

        initialSetupTranslation()

        Picasso.get()
            .load(article.urlToImage)
            .placeholder(R.drawable.background_article)
            .error(R.drawable.background_article)
            .resize(TARGET_WIDTH, TARGET_HEIGHT)
            .centerCrop()
            .into(binding.imageViewArticleImg)
    }

    private fun initializeArticleModel(article: ArticleUiModel) {
        articleModel = article.apply {
            if (url == articleModel?.url) {
                collapsed = articleModel?.collapsed ?: true
            }
        }
    }

    private fun initialSetupTranslation() {
        with(binding) {
            articleModel?.let { article ->
                textViewArticleDescription.translationY = article.collapsed.either(
                    t = START_Y_POSITION,
                    f = convertToPx(translationToExpanded, itemView.context.resources)
                )
                textViewArticleTitle.isVisible = article.collapsed

            }
        }
    }

    private fun showMore() {
        moveViewPosition(
            view                 = binding.textViewArticleDescription,
            positionFinish       = convertToPx(translationToExpanded, itemView.context.resources),
            positionIntermediate = convertToPx(translationToExpandedMiddle, itemView.context.resources)
        )
        binding.textViewArticleTitle.isVisible = false
    }

    private fun hideMore() {
        moveViewPosition(
            view                 = binding.textViewArticleDescription,
            positionFinish       = START_Y_POSITION,
            positionIntermediate = convertToPx(translationToExpandedMiddle, itemView.context.resources)
        )
        binding.textViewArticleTitle.isVisible = true
    }

    interface Callbacks {
        fun onFavoriteClicked(article: ArticleUiModel)

        fun onItemArticleClicked(article: ArticleUiModel)
    }

    companion object {
        private const val TARGET_WIDTH = 370
        private const val TARGET_HEIGHT = 160

        private const val START_Y_POSITION = 0f
        private const val SINGLE_LINE_HEIGHT = 14.8f
    }
}
