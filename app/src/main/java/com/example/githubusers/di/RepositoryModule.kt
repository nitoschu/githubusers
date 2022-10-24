package com.example.githubusers.di

import com.example.usersloader.DefaultUsersRepo
import com.example.usersloader.UsersRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module(includes = [RepositoryModule.Declarations::class])
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Module
    @InstallIn(ViewModelComponent::class)
    abstract class Declarations {
        @Binds
        abstract fun bindUsersLoader(impl: DefaultUsersRepo): UsersRepository
    }

    @Provides
    fun provideUsersLoader() = DefaultUsersRepo()
}