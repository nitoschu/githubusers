package com.example.githubusers.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.githubusers.repository.NaiveUserPersistence
import com.example.githubusers.repository.UsersPagingSource
import com.example.githubusers.repository.room.StorableGithubUser
import com.example.githubusers.repository.toStorableGithubUsers
import com.example.usersloader.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This ViewModel is responsible for delegating requests to fetch Github users from
 * the remote source and persisting them.
 *
 * On a refresh, all currently loaded users will be invalidated and reloaded. That means that
 * during a refresh, it is easy to hit the rate limitation or the API. However, this way it is
 * easier for other developers to check this code out and compile it without having to worry
 * about including project-external Gradle files with authorization information.
 *
 * If an error occurs while no users have been loaded yet, persisted users are restored.
 */
@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val repo: UsersRepository,
    private val userPersistence: NaiveUserPersistence
) : ViewModel() {

    private var isRefresh = false
    private var hasLoadedUsers = false

    private var collectUsersJob: Job? = null
    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState

    private var usersSource = UsersPagingSource(repo)
    private val pager = Pager(PagingConfig(pageSize = 10)) { usersSource }
    var users: Flow<PagingData<StorableGithubUser>> = pager
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
        isRefresh = true
        viewModelScope.launch {
            setNewUiState(error = null, isLoading = true)
        }
        usersSource = UsersPagingSource(repo)
    }

    private suspend fun collectUsersFlow() {
        repo.users.distinctUntilChanged().collect {
            if (it.isFailure) {
                val persistedUsers = if (!hasLoadedUsers) userPersistence.restore() else null
                setNewUiState(
                    isLoading = false,
                    error = it.exceptionOrNull(),
                    persistedUsers = persistedUsers
                )
            } else {
                if (isRefresh) {
                    userPersistence.clearAll()
                }
                isRefresh = false
                hasLoadedUsers = true
                val newPage = userPersistence.latestPage()+1
                userPersistence.persist(it.getOrThrow().toStorableGithubUsers(newPage))
                setNewUiState(isLoading = false, error = null, persistedUsers = null)
            }
        }
    }

    private suspend fun setNewUiState(
        isLoading: Boolean = uiState.value.isLoading,
        error: Throwable? = uiState.value.error,
        page: Int = uiState.value.page,
        persistedUsers: List<StorableGithubUser>? = uiState.value.persistedUsers
    ) = _uiState.emit(OverviewUiState(isLoading, error, page, persistedUsers))

    fun onErrorShown() = viewModelScope.launch { setNewUiState(isLoading = false, error = null) }
}

data class OverviewUiState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val page: Int = 0,
    val persistedUsers: List<StorableGithubUser>? = null
)