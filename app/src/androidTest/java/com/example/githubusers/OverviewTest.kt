package com.example.githubusers

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.PagingData
import com.example.usersloader.GithubUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class OverviewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun should_show_username() {
        composeTestRule.setContent {
            MaterialTheme {
                OverviewScreenTestInstance()
            }
        }
        composeTestRule.onNodeWithText(mockGithubUser.login).assertIsDisplayed()
    }

    @Test
    fun after_error_resolved_should_show_users() {

        val uiState = mutableStateOf(OverviewUiState(error = UnknownError()))

        composeTestRule.setContent {
            MaterialTheme {
                OverviewScreenTestInstance(uiState = remember { uiState })
            }
        }

        uiState.value = OverviewUiState(error = null)
        composeTestRule.onNodeWithText(mockGithubUser.login).assertIsDisplayed()
    }
}

@Composable
private fun OverviewScreenTestInstance(
    uiState: State<OverviewUiState> = mutableStateOf(OverviewUiState()),
    users: Flow<PagingData<GithubUser>> = flowOf(PagingData.from(listOf(mockGithubUser)))
) = OverviewScreen(uiState = uiState, users = users, retryLoadingUsers = {}, onErrorShown = {})

val mockGithubUser = GithubUser(
    id = 1,
    login = "Bert",
    nodeId = "abcd",
    avatarUrl = "abcd",
    gravatarId = "abcd",
    url = "abcd",
    htmlUrl = "abcd",
    followersUrl = "abcd",
    followingUrl = "abcd",
    gistsUrl = "abcd",
    starredUrl = "abcd",
    subscriptionsUrl = "abcd",
    organizationsUrl = "abcd",
    reposUrl = "abcd",
    eventsUrl = "abcd",
    receivedEventsUrl = "abcd",
    type = "user",
    siteAdmin = true,
    score = 1f,
    name = "Bert",
    company = "Bert Company",
    blog = "abcd",
    location = "abcd",
    email = "abcd",
    hireable = "false",
    bio = "abcd",
    publicRepos = 0,
    publicGists = 0,
    followers = 1,
    following = 1
)