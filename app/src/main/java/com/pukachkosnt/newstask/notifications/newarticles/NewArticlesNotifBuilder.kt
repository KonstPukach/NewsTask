package com.pukachkosnt.newstask.notifications.newarticles

import android.content.Context
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.usecases.GetUnreadArticlesUseCase
import com.pukachkosnt.newstask.notifications.NotificationContentBuilder
import com.pukachkosnt.notifications.R

class NewArticlesNotifBuilder(
    private val getUnreadArticlesUseCase: GetUnreadArticlesUseCase,
    private val context: Context
) : NotificationContentBuilder {

    override suspend fun makeContent(): String? {
        val unreadArticles = getUnreadArticlesUseCase.getLastUnreadArticles()
        if (unreadArticles.isNotEmpty()) {
            return buildDescription(unreadArticles, context)
        }
        return null
    }

    private fun buildDescription(
        unreadArticles: List<ArticleModel>,
        context: Context
    ): String {
        var unreadArticlesAmountStr = unreadArticles.size.toString()

        if (unreadArticles.size == MAX_CHECKED_ARTICLES)
            unreadArticlesAmountStr += "+"

        var msgStart = unreadArticles.last().title
        if (unreadArticles.size == 1)
            return msgStart

        val words = msgStart.split(" ")
        if (words.size > NOTIF_MAX_WORD_DESCR_AMOUNT) {    // cut the text if it's too long
            msgStart = words.subList(0, NOTIF_MAX_WORD_DESCR_AMOUNT).joinToString(" ") { it } + "..."
        }

        val and = context.getString(R.string.and)
        val articlesYouHaventRead = context.getString(R.string.articles_you_havent_read)

        return "$msgStart\n $and $unreadArticlesAmountStr $articlesYouHaventRead"
    }

    companion object {
        const val NOTIF_MAX_WORD_DESCR_AMOUNT = 15

        // Compares the last read article with new articles before the match or
        // before the [MAX_CHECKED_ARTICLES] loop iterations
        const val MAX_CHECKED_ARTICLES = 50
    }
}