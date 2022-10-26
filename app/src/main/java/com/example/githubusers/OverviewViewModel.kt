package com.example.githubusers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.usersloader.GithubUser
import com.example.usersloader.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val repo: UsersRepository
) : ViewModel() {

    private var collectUsersJob: Job? = null
    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState

    private var usersSource = UsersPagingSource(repo)
    private val pager = Pager(PagingConfig(pageSize = 10)) { usersSource }

    var users: Flow<PagingData<GithubUser>> = pager
        .flow
        .cachedIn(viewModelScope)

    fun startCollectingUsers() {
        if (collectUsersJob != null) return
        collectUsersJob = viewModelScope.launch {
            setNewUiState(isLoading = true)
            collectUsersFlow()
        }
    }

    fun retryCollectingUsers() {
        viewModelScope.launch { setNewUiState(error = null, isLoading = true) }
        usersSource = UsersPagingSource(repo)
    }

    private suspend fun collectUsersFlow() {
        repo.users.distinctUntilChanged().collect {
            if (it.isFailure) {
                setNewUiState(isLoading = false, error = it.exceptionOrNull())
            } else {
                setNewUiState(isLoading = false, error = null)
            }
        }
    }

    private suspend fun setNewUiState(
        isLoading: Boolean = uiState.value.isLoading,
        error: Throwable? = uiState.value.error,
        page: Int = uiState.value.page
    ) = _uiState.emit(OverviewUiState(isLoading, error, page))

    fun onErrorShown() = viewModelScope.launch { setNewUiState(isLoading = false, error = null) }
}

data class OverviewUiState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val page: Int = 0
)