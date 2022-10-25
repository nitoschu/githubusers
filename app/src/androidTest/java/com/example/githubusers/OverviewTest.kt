package com.example.githubusers

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.PagingData
import com.example.usersloader.GithubUser
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class OverviewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun should_show_username() {
        composeTestRule.setContent {
            MaterialTheme() {
                OverviewScreen(flowOf(PagingData.from(listOf(mockGithubUser))))
            }
        }

        composeTestRule.onNodeWithText(mockGithubUser.login).assertIsDisplayed()
    }
}

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