package com.pukachkosnt.newstask.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class NewsApiFact {
    class Builder {
        private var _baseUrl = ""

        val baseUrl: String
            get() = _baseUrl

        fun addBaseUrl(url: String): Builder {
            _baseUrl = url
            return this
        }

        fun build(): NewsApi {
            return buildNewsApi(this)
        }
    }

    companion object {
        private fun buildNewsApi(builder: Builder): NewsApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(NewsQueryInterceptor())
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