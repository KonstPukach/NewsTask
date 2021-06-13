package com.pukachkosnt.newstask

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pukachkosnt.newstask.api.NewsFetchRepository
import com.pukachkosnt.newstask.models.Article
import retrofit2.HttpException
import java.io.IOException
import java.util.*

private const val TAG = "NewsDataSource"

class NewsDataSource(
    private val newsFetchRepository: NewsFetchRepository,
    private val searchQuery: String,
    private val maxPages: Int) :
    PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val pageNumber = params.key ?: 0
            if (pageNumber >= maxPages - 1) {
                Log.d(TAG, "End loading: $pageNumber")
                throw IOException()
            }
            Log.d(TAG, "page number: $pageNumber")
            val calendarStart = Calendar.getInstance().apply {
                add(Calendar.DATE, - pageNumber - 1)
            }
            val calendarFinish = Calendar.getInstance().apply {
                add(Calendar.DATE, - pageNumber)
            }
            val response = newsFetchRepository.fetchNewsWithTimeInterval(
                calendarStart.time,
                calendarFinish.time,
                searchQuery
            )
            if (response.isSuccessful) {
                val data = response.body()?.articlesList
                val prevKey = if (pageNumber > 0) pageNumber - 1 else null
                val nextKey = if (response.body()?.articlesList?.isNotEmpty()!!) pageNumber + 1
                                else null

                LoadResult.Page(
                    data = data ?: listOf(),
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException)  {
            LoadResult.Error(e)
        }
    }
}