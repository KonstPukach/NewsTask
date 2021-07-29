package com.pukachkosnt.newstask.ui.listnews

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.newstask.R
import com.pukachkosnt.newstask.animations.moveViewPosition
import com.pukachkosnt.newstask.animations.scaleViewFromZero
import com.pukachkosnt.newstask.databinding.NewsItemBinding
import com.pukachkosnt.newstask.extensions.convertToPx
import com.pukachkosnt.newstask.extensions.toBeautifulLocalizedFormat
import com.squareup.picasso.Picasso
import java.util.*

class ArticleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding: NewsItemBinding = NewsItemBinding.bind(itemView)
    private var showMoreState: Boolean = false  // false - not pressed, true - pressed

    private val heartRedDrawable = ResourcesCompat.getDrawable(
        itemView.resources,
        R.drawable.heart_red, null
    )

    private val heartTransparentDrawable = ResourcesCompat.getDrawable(
        itemView.resources,
        R.drawable.heart_transparent, null
    )

    private val callbacks: Callbacks

    init {
        // set "show more" when layout parameters are known
        // measures view, when the text changes
        binding.textViewArticleDescription.doAfterTextChanged { setupShowMore() }
        // measures the view, when the view was drawn
        binding.textViewArticleDescription.doOnPreDraw { setupShowMore() }

        callbacks = ((itemView.context as NewsActivity)
            .supportFragmentManager
            .findFragmentById(R.id.nav_host_list_news_fragment_container) as NavHostFragment)
            .childFragmentManager
            .fragments[0] as Callbacks

        binding.textViewShowMore.setOnClickListener {
            if (showMoreState) hideMore()
            else showMore()
            showMoreState = !showMoreState
        }
    }

    fun bind(article: ArticleModel) {
        initialSetupTranslation()
        showMoreState = false
        with(binding) {
            textViewArticleTitle.text = article.title
            textViewArticleDescription.text = article.description
            textViewArticlePublishedAt.text = article.publishedAt.toBeautifulLocalizedFormat(
                Locale.getDefault().language,
                itemView.resources.getStringArray(R.array.months)
            )
            textViewArticleSource.text = article.sourceName

            imageBtnFavorite.background = if (article.isFavorite) {
                heartRedDrawable
            } else {
                heartTransparentDrawable
            }

            imageViewArticleImg.setOnClickListener {
                callbacks.onItemArticleClicked(article)
            }

            imageBtnFavorite.setOnClickListener {
                article.isFavorite = !article.isFavorite
                imageBtnFavorite.background = if (article.isFavorite) {
                    heartRedDrawable
                } else {
                    heartTransparentDrawable
                }
                scaleViewFromZero(it)
                callbacks.onFavoriteClicked(article)
            }
        }


        Picasso.get()
            .load(article.urlToImage)
            .placeholder(R.drawable.background_article)
            .error(R.drawable.background_article)
            .resize(TARGET_WIDTH, TARGET_HEIGHT)
            .centerCrop()
            .into(binding.imageViewArticleImg)
    }

    private fun setupShowMore() {
        with(binding) {
            if (textViewArticleDescription.width > 0) {
                textViewShowMore.isVisible = textViewArticleDescription.lineCount > MAX_LINES_COLLAPSED
            }
        }
    }

    private fun initialSetupTranslation() {     // every item need to be set up in onBind()
        with(binding) {
            linLayoutDescription.translationY = START_Y_POSITION   // because animation changes item's Y coordinate
            textViewArticleDescription.maxLines = MAX_LINES_COLLAPSED
            textViewArticleTitle.isVisible = true
            textViewShowMore.isVisible = false
            textViewShowMore.setText(R.string.show_more)
        }
    }

    private fun showMore() {
        with(binding) {
            textViewArticleDescription.maxLines = MAX_LINES_EXPANDED
            moveViewPosition(
                linLayoutDescription,
                convertToPx(-(textViewArticleDescription.lineCount + 1) * SINGLE_LINE_HEIGHT,
                    itemView.context.resources),
                convertToPx(-(textViewArticleDescription.lineCount + 2) * SINGLE_LINE_HEIGHT,
                    itemView.context.resources)
            )
            textViewArticleTitle.isVisible = false
            textViewShowMore.setText(R.string.hide_more)
        }
    }

    private fun hideMore() {
        with(binding) {
            textViewArticleDescription.maxLines = MAX_LINES_COLLAPSED
            moveViewPosition(
                linLayoutDescription,
                START_Y_POSITION,
                convertToPx( -(textViewArticleDescription.lineCount + 2) * SINGLE_LINE_HEIGHT,
                    itemView.context.resources)
            )
            textViewArticleTitle.isVisible = true
            textViewShowMore.setText(R.string.show_more)
        }
    }

    interface Callbacks {
        fun onFavoriteClicked(article: ArticleModel)

        fun onItemArticleClicked(article: ArticleModel)
    }

    companion object {
        private const val MAX_LINES_COLLAPSED = 3
        private const val MAX_LINES_EXPANDED = 10
        private const val TARGET_WIDTH = 370
        private const val TARGET_HEIGHT = 160

        private const val START_Y_POSITION = 0f
        private const val SINGLE_LINE_HEIGHT = 14.8f
    }
}