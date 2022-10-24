package com.example.githubusers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usersloader.GithubUser
import com.example.usersloader.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val repo: UsersRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<GithubUser>>(listOf())
    val users: StateFlow<List<GithubUser>> = _users

    fun loadUser() {
        viewModelScope.launch() { collectUsersFromRepo() }
        viewModelScope.launch { repo.requestUsers() }
    }

    private suspend fun collectUsersFromRepo() {
        repo.users.collect { result ->
            if (result.isSuccess) {
                handleSuccess(result.getOrThrow())
            } else {
                handleFailure(result.exceptionOrNull())
            }
        }
    }

    private suspend fun handleSuccess(users: List<GithubUser>) {
        _users.emit(users)
    }

    private suspend fun handleFailure(error: Throwable?) {}
}