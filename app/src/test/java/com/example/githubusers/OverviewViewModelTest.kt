@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.githubusers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.* // ktlint-disable no-wildcard-imports
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext

@ExtendWith(MainDispatcherExtension::class)
class OverviewViewModelTest {

    @Test
    fun test() = runTest {
        val viewModel = OverviewViewModel(FakeUsersLoader())
        viewModel.loadUser()
        Assertions.assertEquals(mockGithubUser.login, viewModel.users.value[0].login)
    }
}

class MainDispatcherExtension(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : BeforeEachCallback, AfterEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}
