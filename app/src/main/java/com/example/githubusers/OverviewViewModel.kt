package com.example.githubusers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.usersloader.GithubUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val pagination: UsersPager
) : ViewModel() {

    private var collectUsersJob: Job? = null
    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState

    val users: Flow<PagingData<GithubUser>> = Pager(PagingConfig(pageSize = 10)) { pagination }
        .flow
        .cachedIn(viewModelScope)

    fun startCollectingUsers() {
        if (collectUsersJob != null) return
        collectUsersJob = viewModelScope.launch { requestNewUsersFromRepo() }
    }

    private suspend fun requestNewUsersFromRepo() {
        setNewUiState(isLoading = true)
        pagination.requestUsers(uiState.value.page, perPage = 10)
        setNewUiState(isLoading = false)
    }

    private suspend fun handleFailure(error: Throwable?) {
        setNewUiState(isLoading = false, error = error)
    }

    private suspend fun setNewUiState(
        isLoading: Boolean = uiState.value.isLoading,
        error: Throwable? = uiState.value.error,
        page: Int = uiState.value.page
    ) = _uiState.emit(OverviewUiState(isLoading, error, page))
}

data class OverviewUiState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val page: Int = 0
)