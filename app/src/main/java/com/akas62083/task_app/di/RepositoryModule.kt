package com.akas62083.task_app.di

import com.akas62083.task_app.repository.DataRepository
import com.akas62083.task_app.repository.DataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRepository(
        dataRepositoryImpl: DataRepositoryImpl
    ): DataRepository
}