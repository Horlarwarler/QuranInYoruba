package com.sadaqaworks.yorubaquran.di

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase


import com.sadaqaworks.yorubaquran.download.DownloaderInterface
import com.sadaqaworks.yorubaquran.download.KtorDownloaderImpl
import com.sadaqaworks.yorubaquran.internet.InternetConnectionMonitor
import com.sadaqaworks.yorubaquran.shared.QuranDatabase
import com.sadaqaworks.yorubaquran.shared.QuranPreference
import com.sadaqaworks.yorubaquran.shared.QuranPreferenceImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesClient(): HttpClient {
        return HttpClient(CIO) {
            install(HttpTimeout) {
                connectTimeoutMillis = 100000
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true

                    }
                )
            }
//            install(DefaultRequest){
//                url("local-host")
//            }
        }
    }

    @Provides
    @Singleton
    fun providesSharedPreference(
        @ApplicationContext context: Context
    ): QuranPreference {
        val preference = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return QuranPreferenceImp(preference)
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }


    @Provides
    @Singleton
    fun providesDatabase(
        @ApplicationContext context: Context
    ): QuranDatabase {

        return Room.databaseBuilder(
            context,
            QuranDatabase::class.java,
            "updated_quran"
        )
            .fallbackToDestructiveMigration()
            .createFromAsset("updated_quran.db")
            .build()
    }


    @Provides
    @Singleton
    fun providesKtorDownload(
        httpClient: HttpClient,
        @ApplicationContext context: Context
    ): DownloaderInterface {
        return KtorDownloaderImpl(httpClient, context)
    }

    @Provides
    @Singleton
    fun providesInternetConnectionMonitor(
        @ApplicationContext context: Context
    ): InternetConnectionMonitor {
        return InternetConnectionMonitor(context)
    }

}