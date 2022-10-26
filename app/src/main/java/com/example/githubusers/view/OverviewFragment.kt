package com.example.githubusers.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberAsyncImagePainter
import com.example.githubusers.R
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
                    onErrorShown = { viewModel.onErrorShown() },
                    onUserClick = { navigate() }
                )
            }
        }
    }

    private fun navigate() {

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
    onErrorShown: () -> Unit,
    onUserClick: () -> Unit
) {

    val users: LazyPagingItems<StorableGithubUser> = pagingUsersData.collectAsLazyPagingItems()
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    Scaffold(scaffoldState = scaffoldState, content = { padding ->
        Column(Modifier.padding(padding)) {
            GithubUsersList(
                users = users,
                persistedUsers = uiState.value.persistedUsers,
                isRefreshing = uiState.value.isLoading,
                onUserClick = onUserClick,
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
    onRefresh: () -> Unit,
    onUserClick: () -> Unit,
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
                    items(persistedUsers) { User(it, onUserClick) }
                }
            } else {
                LazyColumn() {
                    items(users) { it?.let { it1 -> User(it1, onUserClick) } }
                }
            }
        }
    }
}

@Composable
fun User(
    user: StorableGithubUser,
    onClick: () -> Unit
) {

    val navController = rememberNavController()


    Column(Modifier.clickable { /*nav.navigate()*/ }) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(user.avatarUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .background(color = Color.Blue)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(user.login, fontSize = 20.sp)
        }
        Divider()
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

@Preview(widthDp = 400, heightDp = 100, showBackground = true)
@Composable
fun UserPreview() {
    User(StorableGithubUser(1234, "Gerhard", "---"), onClick = {})
}