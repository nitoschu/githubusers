package com.example.githubusers

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.MutableLiveData
import org.junit.Rule
import org.junit.Test

class OverviewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun should_show_username() {
        composeTestRule.setContent {
            MaterialTheme() {
                OverviewScreen(MutableLiveData("Hans Dampf"))
            }
        }

        composeTestRule.onNodeWithText("Hans Dampf").assertIsDisplayed()
    }
}