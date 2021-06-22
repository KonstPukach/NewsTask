package com.pukachkosnt.domain

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pukachkosnt.data.repository.BaseRepository
import com.pukachkosnt.domain.mapper.ApiToEntityMapper
import com.pukachkosnt.domain.models.ArticleEntity
import retrofit2.HttpException
import java.io.IOException
import java.util.*

// Domain layer

class NewsDataSource(
    private val newsFetchRepository: BaseRepository,
    private val searchQuery: String,
    private val maxPages: Int,
    private val viewModel: ViewModel
    ) : PagingSource<Int, ArticleEntity>() {

    companion object {
        private const val TAG = "NewsDataSource"
    }

    private val dataList: MutableList<ArticleEntity> = mutableListOf()

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
            val response = newsFetchRepository.fetchNewsWithTimeInterval(
                calendarStart.time,
                calendarFinish.time,
                searchQuery
            )
            if (response.isSuccessful) {
                val data = response.body()?.articlesList?.map { ApiToEntityMapper.map(it) }
                val prevKey = if (pageNumber > 0) pageNumber - 1 else null
                val nextKey = if (response.body()?.articlesList?.isNotEmpty()!!) pageNumber + 1
                                else null
                if (pageNumber == 0)
                    dataList.clear()
                dataList.addAll(data ?: listOf())

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

    fun addDataList(it: (List<ArticleEntity>) -> Unit): NewsDataSource {
        it.invoke(dataList)
        return this
    }
}