package com.pukachkosnt.domain

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.BaseDBRepository
import retrofit2.HttpException
import java.io.IOException

class FavoritesDataSource(
    private val dbRepository: BaseDBRepository,
    private val pageSize: Int
) : PagingSource<Int, ArticleModel>() {

    private val _dataList: MutableList<ArticleModel> = mutableListOf()
    val dataList: List<ArticleModel> = _dataList

    override fun getRefreshKey(state: PagingState<Int, ArticleModel>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleModel> {
        return try {
            val pageNumber = params.key ?: 0
            Log.d(TAG, "page number: $pageNumber")
            val data: List<ArticleModel> = dbRepository.getRangeFavoriteArticles(
                pageNumber * pageSize,
                pageSize
            )
            val prevKey = if (pageNumber > 0) pageNumber - 1 else null
            val nextKey = if (data.isNotEmpty()) pageNumber + 1 else null

            if (data.isEmpty()) {
                Log.d(TAG, "End loading: $pageNumber")
                return LoadResult.Page(
                    data = listOf(),
                    prevKey = pageNumber - 1,
                    nextKey = null
                )
            }

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
        private const val TAG = "FavoritesDataSource"
    }
}