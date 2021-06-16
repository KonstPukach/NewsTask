package com.pukachkosnt.newstask.di

import com.pukachkosnt.newstask.api.NewsApi
import com.pukachkosnt.newstask.api.NewsQueryInterceptor
import com.pukachkosnt.newstask.repository.BaseRepository
import com.pukachkosnt.newstask.repository.NewsFetchRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class ApiModule {
    companion object {
        private const val BASE_URL = "https://newsapi.org/"
    }
    @Provides
    @Singleton
    fun provideConverterFactory(): Converter.Factory {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return MoshiConverterFactory.create(moshi)
    }

    @Provides
    @Singleton
    fun provideRetrofit(converterFactory: Converter.Factory, interceptor: Interceptor): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(converterFactory)
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideInterceptor(): Interceptor = NewsQueryInterceptor()

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }

    @Provides
    fun provideNewsRepository(api: NewsApi): BaseRepository {
        return NewsFetchRepository(api)
    }
}