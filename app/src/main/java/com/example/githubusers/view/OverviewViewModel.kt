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

/**
 * This ViewModel is responsible for delegating requests to fetch Github users from
 * the remote source and persisting them.
 *
 * On a refresh, all currently loaded users will be invalidated and reloaded. That means that
 * during a refresh, it is easy to hit the rate limitation of the API. However, this way it is
 * easier for other developers to check this code out and compile it without having to worry
 * about including project-external files with authorization information.
 *
 * If an error occurs while no users have been loaded yet, persisted users are restored. Otherwise
 * the app user will be able to scroll through the current users as they are loaded in RAM.
 */
@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val repo: UsersRepository,
    private val userPersistence: NaiveUserPersistence
) : ViewModel() {

    // Jetpack Pager setup, the Flow of PagingData is collected by the Composables.
    private var usersSource = UsersPagingSource(repo)
    private val pager = Pager(PagingConfig(pageSize = 10)) { usersSource }
    var users: Flow<PagingData<StorableGithubUser>> = pager
        .flow
        .cachedIn(viewModelScope)

    private var collectUsersJob: Job? = null
    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState

    private var isRefresh = false
    private var hasLoadedUsers = false

    /*
     * As handling PagingData manually is a hassle, we collect directly from
     * the repo so that we can persist users and handle errors.
     */
    fun collectUserResultsFromRepo() {
        if (collectUsersJob != null) return

        collectUsersJob = viewModelScope.launch {
            setNewUiState(isLoading = true)
            repo.users.distinctUntilChanged().collect {
                if (it.isFailure) {
                    restorePersistedUsersIfNecessary(it)
                } else {
                    persistUsersAndUpdateUi(it)
                }
            }
        }
    }

    fun retryCollectingUsers() {
        isRefresh = true
        viewModelScope.launch { setNewUiState(error = null, isLoading = true) }
        usersSource = UsersPagingSource(repo)
    }

    private suspend fun restorePersistedUsersIfNecessary(result: Result<List<GithubUser>>) {
        val persistedUsers = if (!hasLoadedUsers) userPersistence.restore() else null
        setNewUiState(
            isLoading = false,
            error = result.exceptionOrNull(),
            persistedUsers = persistedUsers
        )
    }

    private suspend fun persistUsersAndUpdateUi(result: Result<List<GithubUser>>) {
        if (isRefresh) { userPersistence.clearAll() }
        isRefresh = false
        hasLoadedUsers = true
        persistNewUsers(result)
        setNewUiState(isLoading = false, error = null, persistedUsers = null)
    }

    private suspend fun persistNewUsers(result: Result<List<GithubUser>>) {
        val newPage = userPersistence.latestPage() + 1
        userPersistence.persist(result.getOrThrow().toStorableGithubUsers(newPage))
    }

    private suspend fun setNewUiState(
        isLoading: Boolean = uiState.value.isLoading,
        error: Throwable? = uiState.value.error,
        persistedUsers: List<StorableGithubUser>? = uiState.value.persistedUsers
    ) = _uiState.emit(OverviewUiState(isLoading, error, persistedUsers))

    fun onErrorShown() = viewModelScope.launch { setNewUiState(isLoading = false, error = null) }
}

data class OverviewUiState(
    val isLoading: Boolean = true,
    val error: Throwable? = null,
    val persistedUsers: List<StorableGithubUser>? = null
)