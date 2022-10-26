package com.example.githubusers.di

import android.content.Context
import androidx.room.Room
import com.example.githubusers.repository.DefaultNaiveUserPersistence
import com.example.githubusers.repository.NaiveUserPersistence
import com.example.githubusers.repository.room.UserDatabase
import com.example.usersloader.DefaultUsersRepo
import com.example.usersloader.UsersRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module(includes = [RepositoryModule.Declarations::class])
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Module
    @InstallIn(ViewModelComponent::class)
    abstract class Declarations {
        @Binds
        abstract fun bindUsersLoader(impl: DefaultUsersRepo): UsersRepository

        @Binds
        abstract fun bindUserPersistence(impl: DefaultNaiveUserPersistence): NaiveUserPersistence
    }

    @Provides
    fun provideUsersLoader() = DefaultUsersRepo()

    @Provides
    fun provideRoomDb(@ApplicationContext appContext: Context) = Room.databaseBuilder(
        appContext,
        UserDatabase::class.java, "user-database"
    ).build()
}