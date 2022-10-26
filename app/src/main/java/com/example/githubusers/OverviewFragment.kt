package com.example.githubusers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.usersloader.GithubUser
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
                    users = viewModel.users,
                    retryLoadingUsers = { viewModel.retryCollectingUsers() }
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
    users: Flow<PagingData<GithubUser>>,
    retryLoadingUsers: () -> Unit
) {

    val userItems: LazyPagingItems<GithubUser> = users.collectAsLazyPagingItems()
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    Scaffold(scaffoldState = scaffoldState, content = { padding ->
        Column(Modifier.padding(padding)) {
            GithubUsersList(users = userItems)
            if (uiState.value.error != null) {
                LaunchedEffect(uiState.value.error) {
                    scaffoldState.snackbarHostState.showSnackbar(
                        uiState.value.error.toString(),
                        "Retry",
                        duration = SnackbarDuration.Indefinite
                    )
                    retryLoadingUsers()
                    userItems.refresh()
                }
            }
        }
    }
    )
}

@Composable
fun GithubUsersList(users: LazyPagingItems<GithubUser>) {
    LazyColumn() {
        items(users) { user ->
            Text(user?.login ?: "")
        }
    }
}