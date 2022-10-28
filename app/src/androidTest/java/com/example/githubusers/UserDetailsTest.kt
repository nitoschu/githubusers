package com.example.githubusers

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.githubusers.view.UserDetails
import org.junit.Rule
import org.junit.Test

class UserDetailsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun should_show_username() {
        composeTestRule.setContent {
            MaterialTheme {
                UserDetailsTestInstance()
            }
        }
        composeTestRule.onNodeWithText(mockGithubUser.login).assertIsDisplayed()
    }

    @Test
    fun should_show_user_id() {
        composeTestRule.setContent {
            MaterialTheme {
                UserDetailsTestInstance()
            }
        }
        composeTestRule.onNodeWithText("ID: ${mockGithubUser.id}").assertIsDisplayed()
    }

    @Test
    fun should_show_user_score() {
        composeTestRule.setContent {
            MaterialTheme {
                UserDetailsTestInstance()
            }
        }
        composeTestRule.onNodeWithText("Score: ${mockGithubUser.score}").assertIsDisplayed()
    }
}

@Composable
private fun UserDetailsTestInstance() {
    val user = mockGithubUser
    UserDetails(
        login = user.login,
        id = user.id,
        avatarUrl = user.avatarUrl,
        htmlUrl = user.htmlUrl,
        score = user.score
    )
}