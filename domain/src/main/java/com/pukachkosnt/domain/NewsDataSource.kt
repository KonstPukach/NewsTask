package com.pukachkosnt.domain

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pukachkosnt.domain.models.ArticleEntity
import com.pukachkosnt.domain.repository.BaseRepository
import retrofit2.HttpException
import java.io.IOException
import java.util.*

// Domain layer

class NewsDataSource(
    private val newsFetchRepository: BaseRepository,
    private val searchQuery: String,
    private val maxPages: Int
) : PagingSource<Int, ArticleEntity>() {
    private val _dataList: MutableList<ArticleEntity> = mutableListOf()
    val dataList: List<ArticleEntity> = _dataList

    override fun getRefreshKey(state: PagingState<Int, ArticleEntity>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleEntity> {
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
            val data: List<ArticleEntity> = newsFetchRepository.fetchNewsWithTimeInterval(
                calendarStart.time,
                calendarFinish.time,
                searchQuery
            )
            val prevKey = if (pageNumber > 0) pageNumber - 1 else null
            val nextKey = if (data.isNotEmpty()) pageNumber + 1 else null

            if (pageNumber == 0)
                _dataList.clear()
            _dataList.addAll(data)

            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException)  {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val TAG = "NewsDataSource"
    }
}