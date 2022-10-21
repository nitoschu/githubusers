package com.example.githubusers

import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import com.example.usersloader.UsersLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val usersRepo: UsersLoader
) : ViewModel() {

    val user: LiveData<String> = usersRepo.users.asLiveData()

    fun loadUser() {
        viewModelScope.launch { usersRepo.requestUsers() }
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @Provides
    fun provideUsersLoader() = UsersLoader()
}