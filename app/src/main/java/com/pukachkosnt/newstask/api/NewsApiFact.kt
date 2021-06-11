package com.pukachkosnt.newstask.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = ""

class NewsApiFact {
    class Builder {

        var searchQuery = ""
        var baseUrl = ""

        fun addSearchQuery(query: String): Builder {
            searchQuery = query
            return this
        }

        fun addBaseUrl(url: String): Builder {
            baseUrl = url
            return this
        }

        fun build(): NewsApi {
            return buildNewsApi(this)
        }
    }

    companion object {
        private fun buildNewsApi(builder: Builder): NewsApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(NewsQueryInterceptor(builder.searchQuery))
                .build()
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(builder.baseUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(client)
                .build()
            return retrofit.create(NewsApi::class.java)
        }
    }

}