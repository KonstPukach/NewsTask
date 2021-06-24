package com.pukachkosnt.newstask

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.pukachkosnt.newstask.animations.moveViewPosition
import com.pukachkosnt.domain.models.ArticleEntity
import com.squareup.picasso.Picasso

class ArticleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        private const val MAX_LINES_COLLAPSED = 3
        private const val MAX_LINES_EXPANDED = 10
        private const val TARGET_WIDTH = 370
        private const val TARGET_HEIGHT = 160

        private const val START_Y_POSITION = 0f
        private const val SINGLE_LINE_HEIGHT = 29f
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
        textViewDescription.doAfterTextChanged { setupShowMore() }
        // measures the view, when the view was drawn
        textViewDescription.doOnPreDraw { setupShowMore() }

        textViewShowMore.setOnClickListener {
            if (showMoreState) hideMore()
            else showMore()
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
            textViewShowMore.isVisible = textViewDescription.lineCount > MAX_LINES_COLLAPSED
        }
    }

    private fun initialSetupTranslation() {     // every item need to be set up in onBind()
        linLayout.translationY = START_Y_POSITION   // because animation changes item's Y coordinate
        textViewDescription.maxLines = MAX_LINES_COLLAPSED
        textViewTitle.isVisible = true
        textViewShowMore.isVisible = false
        textViewShowMore.setText(R.string.show_more)
    }

    private fun showMore() {
        textViewDescription.maxLines = MAX_LINES_EXPANDED
        moveViewPosition(
            linLayout,
            -(textViewDescription.lineCount + 1) * SINGLE_LINE_HEIGHT,
            -(textViewDescription.lineCount + 2) * SINGLE_LINE_HEIGHT
        )
        textViewTitle.isVisible = false
        textViewShowMore.setText(R.string.hide_more)
    }

    private fun hideMore() {
        textViewDescription.maxLines = MAX_LINES_COLLAPSED
        moveViewPosition(
            linLayout,
            START_Y_POSITION,
            -(textViewDescription.lineCount + 2) * SINGLE_LINE_HEIGHT
        )
        textViewTitle.isVisible = true
        textViewShowMore.setText(R.string.show_more)
    }
}