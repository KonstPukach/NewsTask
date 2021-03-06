package com.pukachkosnt.domain

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.NewsByTimeIntervalRepository
import com.pukachkosnt.domain.repository.FavoritesRepository
import com.pukachkosnt.domain.repository.LastViewedArticleRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import retrofit2.HttpException
import java.io.IOException
import java.util.*

// Domain layer

class NewsDataSource(
    private val newsFetchByTimeIntervalRepository: NewsByTimeIntervalRepository,
    private val dbRepository: FavoritesRepository,
    private val lastViewedArticleRepository: LastViewedArticleRepository,
    private val maxPages: Int,
    private val sources: Set<String>
) : PagingSource<Int, ArticleModel>() {
    private val _dataList: MutableList<ArticleModel> = mutableListOf()
    val dataList: List<ArticleModel> = _dataList

    private val favoriteArticlesJob = GlobalScope.async {
        dbRepository.getIds().toHashSet()
    }

    override fun getRefreshKey(state: PagingState<Int, ArticleModel>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleModel> {
        return try {
            val pageNumber = params.key ?: 0

            if (pageNumber >= maxPages - 1) {
                Log.d(TAG, "End loading: $pageNumber")
                return LoadResult.Page(
                    data = listOf(),
                    prevKey = pageNumber - 1,
                    nextKey = null
                )
            }
            Log.d(TAG, "page number: $pageNumber")
            val calendarStart = Calendar.getInstance().apply {
                add(Calendar.DATE, - pageNumber - 1)
            }
            val calendarFinish = Calendar.getInstance().apply {
                add(Calendar.DATE, - pageNumber)
            }
            val data: List<ArticleModel> = favoritizeArticles(
                favoriteArticlesJob.await(),
                newsFetchByTimeIntervalRepository.fetchNewsWithTimeInterval(
                    calendarStart.time,
                    calendarFinish.time,
                    sources
                )
            )
            val prevKey = if (pageNumber > 0) pageNumber - 1 else null
            val nextKey = if (data.isNotEmpty()) pageNumber + 1 else null

            if (pageNumber == 0) {
                _dataList.clear()
                if (data.isNotEmpty())
                    lastViewedArticleRepository.saveLastViewedArticleId(data.first().id)
            }
            _dataList.addAll(data)

            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    private fun favoritizeArticles(
        favArticles: HashSet<String>,
        allArticles: List<ArticleModel>
    ) : List<ArticleModel> {
        val resultList = mutableListOf<ArticleModel>()
        allArticles.forEach {
            if (favArticles.contains(it.id)) {
                resultList.add(it.copy(isFavorite = true))
            } else {
                resultList.add(it)
            }
        }
        return resultList
    }

    companion object {
        private const val TAG = "NewsDataSource"
    }
}