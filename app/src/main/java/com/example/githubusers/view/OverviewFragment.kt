package com.example.githubusers.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.* // ktlint-disable no-wildcard-imports
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.githubusers.repository.room.StorableGithubUser
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow

@AndroidEntryPoint
class OverviewFragment : Fragment() {

    private val viewModel: OverviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            MaterialTheme {
                OverviewScreen(
                    uiState = viewModel.uiState.collectAsState(),
                    pagingUsersData = viewModel.users,
                    retryLoadingUsers = { viewModel.retryCollectingUsers() },
                    onErrorShown = { viewModel.onErrorShown() }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.startCollectingUsers()
    }
}

@Composable
fun OverviewScreen(
    uiState: State<OverviewUiState>,
    pagingUsersData: Flow<PagingData<StorableGithubUser>>,
    retryLoadingUsers: () -> Unit,
    onErrorShown: () -> Unit
) {

    val users: LazyPagingItems<StorableGithubUser> = pagingUsersData.collectAsLazyPagingItems()
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    Scaffold(scaffoldState = scaffoldState, content = { padding ->
        Column(Modifier.padding(padding)) {
            GithubUsersList(
                users = users,
                persistedUsers = uiState.value.persistedUsers,
                isRefreshing = uiState.value.isLoading,
                onRefresh = {
                    retryLoadingUsers()
                    users.refresh()
                }
            )
            if (uiState.value.error != null) {
                LaunchedEffect(uiState.value.error) {
                    val action = scaffoldState.snackbarHostState.showSnackbar(
                        uiState.value.error.toString(),
                        "Retry",
                        duration = SnackbarDuration.Short
                    )
                    when (action) {
                        SnackbarResult.Dismissed -> onErrorShown()
                        SnackbarResult.ActionPerformed -> {
                            retryLoadingUsers()
                            users.refresh()
                        }
                    }
                }
            }
        }
    }
    )
}

@Composable
fun GithubUsersList(
    users: LazyPagingItems<StorableGithubUser>,
    persistedUsers: List<StorableGithubUser>?,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { onRefresh() }
    ) {
        if (users.itemCount == 0 && !isRefreshing && persistedUsers?.isEmpty() == true) {
            EmptyScreen { onRefresh() }
        } else {
            if (persistedUsers != null) {
                LazyColumn() {
                    items(persistedUsers) { User(it) }
                }
            } else {
                LazyColumn() {
                    items(users) { it?.let { it1 -> User(it1) } }
                }
            }
        }
    }
}

@Composable
fun User(user: StorableGithubUser) {
    Column {
        Text(user.login)
    }
}

@Composable
fun EmptyScreen(onRefresh: () -> Unit) {
    Column() {
        Text("Github currently has no users :c")
        Text("Just kidding! Click the button to manually refresh!")
        Button(onClick = { onRefresh() }) {
            Text("Refresh")
        }
    }
}