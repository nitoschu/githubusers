package com.example.githubusers

import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import com.example.usersloader.DefaultUsersLoader
import com.example.usersloader.GithubUser
import com.example.usersloader.UsersLoader
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val usersRepo: UsersLoader
) : ViewModel() {

    private val _users = MutableStateFlow<List<GithubUser>>(listOf())
    val users: StateFlow<List<GithubUser>> = _users

    fun loadUser() {
        viewModelScope.launch {
            usersRepo.requestUsers().collect {
                if (it.isSuccess) {
                    handleSuccess(it.getOrThrow())
                } else {
                    handleFailure(it.exceptionOrNull())
                }
            }
        }
    }

    private suspend fun handleSuccess(users: List<GithubUser>) {
        _users.emit(users)
    }

    private suspend fun handleFailure(error: Throwable?) {}
}

@Module(includes = [RepositoryModule.Declarations::class])
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Module
    @InstallIn(ViewModelComponent::class)
    abstract class Declarations {
        @Binds abstract fun bindUsersLoader(impl: DefaultUsersLoader): UsersLoader
    }

    @Provides
    fun provideUsersLoader() = DefaultUsersLoader()
}