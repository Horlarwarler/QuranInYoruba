package com.sadaqaworks.yorubaquran.dua.data.di

import com.sadaqaworks.yorubaquran.dua.data.repository.DuaRepositoryImpl
import com.sadaqaworks.yorubaquran.dua.domain.repository.DuaRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DuaModule {
    @Binds
    @Singleton
    abstract  fun provideDuaRepo(
        dusRepositoryImpl: DuaRepositoryImpl
    ): DuaRepositoryInterface
}