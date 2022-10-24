package com.example.githubusers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usersloader.GithubUser
import com.example.usersloader.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val repo: UsersRepository
) : ViewModel() {

    private var collectUsersJob: Job? = null
    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState


    fun onCreate() {
        if (collectUsersJob != null) return

        collectUsersJob = viewModelScope.launch {
            viewModelScope.launch { collectUsersFromRepoFlow() }
            viewModelScope.launch { requestNewUsersFromRepo() }
        }
    }

    private suspend fun collectUsersFromRepoFlow() {
        repo.users.collect { result ->
            if (result.isSuccess) {
                handleSuccess(result.getOrThrow())
            } else {
                handleFailure(result.exceptionOrNull())
            }
        }
    }

    suspend fun requestNewUsersFromRepo() {
        if (uiState.value.isLoading) return
        setNewUiState(isLoading = true)
        repo.requestUsers(uiState.value.page)
    }

    private suspend fun handleSuccess(nextUsers: List<GithubUser>) {
        with(uiState.value) {
            val newUsers = ArrayList<GithubUser>()
            newUsers.addAll(users)
            newUsers.addAll(nextUsers)
            val newPage = page + 1
            setNewUiState(isLoading = false, users = newUsers, page = newPage)
        }
    }

    private suspend fun handleFailure(error: Throwable?) {
        setNewUiState(isLoading = false, error = error)
    }

    private suspend fun setNewUiState(
        isLoading: Boolean = uiState.value.isLoading,
        error: Throwable? = uiState.value.error,
        users: List<GithubUser> = uiState.value.users,
        page: Int = uiState.value.page
    ) = _uiState.emit(OverviewUiState(isLoading, error, users, page))
}

data class OverviewUiState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val users: List<GithubUser> = emptyList(),
    val page: Int = 0
)