package com.pukachkosnt.newstask.di.module

import com.pukachkosnt.newstask.NewsRecyclerViewState
import com.pukachkosnt.newstask.SearchViewState
import com.pukachkosnt.newstask.api.NewsApi
import com.pukachkosnt.newstask.api.NewsQueryInterceptor
import com.pukachkosnt.newstask.repository.BaseRepository
import com.pukachkosnt.newstask.repository.NewsFetchRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


private const val BASE_URL = "https://newsapi.org/"

val appModule = module {
    single { provideConverterFactory() }
    single { provideInterceptor() }
    single { provideRetrofit(get(), get()) }
    single { provideNewsApi(get()) }
    single { provideNewsRepository(get()) }
    factory { provideRecyclerViewState() }
    factory { provideSearchViewState() }
}

fun provideConverterFactory(): Converter.Factory {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    return MoshiConverterFactory.create(moshi)
}

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

fun provideInterceptor(): Interceptor = NewsQueryInterceptor()

fun provideNewsApi(retrofit: Retrofit): NewsApi {
    return retrofit.create(NewsApi::class.java)
}

fun provideNewsRepository(api: NewsApi): BaseRepository {
    return NewsFetchRepository(api)
}

fun provideSearchViewState() = SearchViewState()

fun provideRecyclerViewState() = NewsRecyclerViewState()