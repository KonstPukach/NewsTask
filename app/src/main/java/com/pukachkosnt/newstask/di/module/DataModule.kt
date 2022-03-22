package com.pukachkosnt.newstask.di.module

import androidx.room.Room
import com.pukachkosnt.data.api.NewsApi
import com.pukachkosnt.data.api.NewsQueryInterceptor
import com.pukachkosnt.data.db.NewsDatabase
import com.pukachkosnt.data.repository.NewsApiRepository
import com.pukachkosnt.data.repository.NewsDBRepository
import com.pukachkosnt.data.repository.NewsPrefsRepository
import com.pukachkosnt.data.repository.SourcesApiRepository
import com.pukachkosnt.domain.repository.*
import com.pukachkosnt.newstask.App
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

val dataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            NewsDatabase::class.java,
            NewsDatabase.DB_NAME
        ).build()
    }

    single<FavoritesRepository> {
        NewsDBRepository(get())
    }

    single {
        NewsPrefsRepository(
            androidContext(),
            App.PREFS_PATH
        )
    }

    single<Converter.Factory> {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        MoshiConverterFactory.create(moshi)
    }

    single {
        val client = OkHttpClient.Builder()
            .addInterceptor(get())
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(get())
            .client(client)
            .build()
            .create(NewsApi::class.java)
    }

    single<Interceptor> {
        NewsQueryInterceptor()
    }

    single<NewsByTimeIntervalRepository> {
        NewsApiRepository(get())
    }

    single<NewsByAmountRepository> {
        NewsApiRepository(get())
    }

    single<LastViewedArticleRepository> {
        NewsPrefsRepository(androidContext(), App.PREFS_PATH)
    }

    single<SourcesRepository> {
        SourcesApiRepository(get())
    }

    single<SourcesIdsRepository> {
        NewsPrefsRepository(androidContext(), App.PREFS_PATH)
    }
}

