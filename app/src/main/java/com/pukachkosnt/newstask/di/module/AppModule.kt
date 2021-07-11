package com.pukachkosnt.newstask.di.module

import android.content.Context
import androidx.room.Room
import com.pukachkosnt.data.api.NewsApi
import com.pukachkosnt.data.api.NewsQueryInterceptor
import com.pukachkosnt.data.db.NewsDatabase
import com.pukachkosnt.data.repository.NewsApiRepository
import com.pukachkosnt.data.repository.NewsDBRepository
import com.pukachkosnt.domain.repository.BaseApiRepository
import com.pukachkosnt.domain.repository.BaseDBRepository
import com.pukachkosnt.newstask.ui.listnews.all.SearchViewState
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
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
    factory { provideSearchViewState() }
    single { provideNewsDatabase(androidContext()) }
    single { provideDBRepository(get()) }
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

fun provideNewsRepository(api: NewsApi): BaseApiRepository {
    return NewsApiRepository(api)
}

fun provideSearchViewState() = SearchViewState()

fun provideNewsDatabase(context: Context): NewsDatabase {
    return Room.databaseBuilder(
        context,
        NewsDatabase::class.java,
        NewsDatabase.DB_NAME
    ).build()
}

fun provideDBRepository(database: NewsDatabase): BaseDBRepository {
    return NewsDBRepository(database)
}