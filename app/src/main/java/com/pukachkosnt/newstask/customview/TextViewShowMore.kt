package com.pukachkosnt.newstask.customview

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.StaticLayout
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.pukachkosnt.newstask.R

class TextViewShowMore(context: Context, attrs: AttributeSet) :
    AppCompatTextView(context, attrs),
    View.OnClickListener {

    private var state: State = Collapsed()

    private val clickListeners = mutableListOf<OnClickListener>()

    var initialText: String? = null
        set(value) {
            field = value
            refreshText()
        }

    private var _textShowMore: String = ""
    var textShowMore: String
        get() = _textShowMore
        set(value) {
            _textShowMore = value
            refreshText()
        }

    private var _textHideMore: String = ""
    var textHideMore: String
        get() = _textHideMore
        set(value) {
            _textHideMore = value
            refreshText()
        }

    var maxChars: Int = DEFAULT_COLLAPSED_MAX_CHARS
        set(value) {
            require(value >= 0) { "Max characters value must be >= 0" }
            field = value
            refreshText()
        }

    var isCollapsed: Boolean
        get() = state is Collapsed
        set(value) {
            state = if (value) Collapsed() else Expanded()
            refreshText()
        }

    val isCollapsable: Boolean
        get() {
            return maxChars < initialText?.length ?: 0
        }

    private var staticLayout: StaticLayout? = null
    private fun getStaticLayout(width: Int, initText: String): StaticLayout? {
        if (staticLayout == null) {
            staticLayout = StaticLayout.Builder
                .obtain(initText, 0, initText.length, paint, width)
                .build()
        }
        return staticLayout
    }

    var totalLinesInExpandedState: Int = 0     // total lines in an expanded state
        get() {
            val resultWidth = (width - paddingLeft - paddingRight).takeIf { it > 0 } ?: return 0
            val initText    = initialText ?: return 0

            return getStaticLayout(resultWidth, initText)?.lineCount ?: 0
        }
        private set


    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.TextViewShowMore, 0, 0).apply {
            try {
                _textShowMore = getString(R.styleable.TextViewShowMore_text_show_more) ?: ""
                _textHideMore = getString(R.styleable.TextViewShowMore_text_hide_more) ?: ""
            } finally {
                recycle()
            }
        }

        setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        clickListeners.forEach { it.onClick(v) }

        if (!isCollapsable) return

        state = if (state is Expanded) Collapsed() else Expanded()
        refreshText()
    }

    fun addOnClickListener(onClickListener: OnClickListener) {
        clickListeners.add(onClickListener)
    }

    private fun refreshText() {
        val initText = initialText ?: return

        if (isCollapsable) {
            val blackText = if (initText.length > state.maxChars) {
                initText.substring(0, state.maxChars)
            } else initText
            setSpannableText(blackText, state.textToAppend)
        } else {
            text = initText
        }
    }

    private fun setSpannableText(textBlack: String?, textColored: String) {
        val spannable = SpannableString(textBlack + textColored)
        val start = textBlack?.length ?: 0
        val end = start + textColored.length
        spannable.setSpan(ForegroundColorSpan(Color.BLUE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        setText(spannable, BufferType.SPANNABLE)
    }

    companion object {
        private const val DEFAULT_COLLAPSED_MAX_CHARS = 120
        private const val DEFAULT_EXPANDED_MAX_CHARS = 600
    }

    private sealed class State {
        abstract val maxChars: Int
        abstract val textToAppend: String
    }

    private inner class Collapsed : State() {
        override val maxChars: Int get() = this@TextViewShowMore.maxChars
        override val textToAppend: String get() = this@TextViewShowMore.textShowMore
    }

    private inner class Expanded : State() {
        override val maxChars: Int get() = DEFAULT_EXPANDED_MAX_CHARS
        override val textToAppend: String get() = this@TextViewShowMore.textHideMore
    }
}
