package com.sadaqaworks.yorubaquran.di

import com.sadaqaworks.yorubaquran.dua.data.repository.DuaRepositoryImpl
import com.sadaqaworks.yorubaquran.dua.domain.repository.DuaRepositoryInterface
import com.sadaqaworks.yorubaquran.quran.data.remote.RemoteQuranImpl
import com.sadaqaworks.yorubaquran.quran.data.repository.QuranRepositoryImpl
import com.sadaqaworks.yorubaquran.quran.data.remote.RemoteQuran
import com.sadaqaworks.yorubaquran.quran.domain.repository.quranRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class quranModule {
    @Binds
    @Singleton
    abstract fun providesQuranRepository(
        quranRepositoryImpl: QuranRepositoryImpl
    ): quranRepositoryInterface
    @Binds
    @Singleton
    abstract  fun providesRemoteQuran(
        remoteQuranImpl: RemoteQuranImpl
    ): RemoteQuran

    @Binds
    @Singleton
    abstract  fun provideDuaRepo(
        dusRepositoryImpl: DuaRepositoryImpl
    ): DuaRepositoryInterface
}