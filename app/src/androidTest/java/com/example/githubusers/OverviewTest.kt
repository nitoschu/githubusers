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
import com.example.githubusers.repository.room.StorableGithubUser
import com.example.githubusers.view.OverviewScreen
import com.example.githubusers.view.OverviewUiState
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
    users: Flow<PagingData<StorableGithubUser>> = flowOf(PagingData.from(listOf(mockGithubUser)))
) = OverviewScreen(uiState = uiState, pagingUsersData = users, retryLoadingUsers = {}, onErrorShown = {}, onUserClick = {})

internal val mockGithubUser = StorableGithubUser(
    id = 1,
    login = "Bert",
    avatarUrl = "abcd",
    htmlUrl = "wwww",
    score = 1f,
    page = 0
)