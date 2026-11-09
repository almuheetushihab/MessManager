package com.messmanager.app.di

import com.messmanager.app.data.repository.MessRepositoryImpl
import com.messmanager.app.domain.repository.MessRepository
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
    abstract fun bindMessRepository(impl: MessRepositoryImpl): MessRepository
}
