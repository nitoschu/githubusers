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
import org.testng.Assert

@ExtendWith(MainDispatcherExtension::class)
class OverviewViewModelTest {

    @Test
    fun `should emit users`() = runTest {
        val viewModel = OverviewViewModel(FakeUsersRepo())
        viewModel.onCreate()
        advanceUntilIdle()
        Assertions.assertEquals(mockGithubUser.login, viewModel.uiState.value.users[0].login)
    }

    @Test
    fun `should use pagination`() = runTest {
        val viewModel = OverviewViewModel(FakeUsersRepo())
        Assert.assertEquals(viewModel.uiState.value.page,0 )
        viewModel.onCreate()
        advanceUntilIdle()
        Assert.assertEquals(viewModel.uiState.value.page, 1 )
    }

    @Test
    fun `should not reload while still loading`() = runTest {
        val repo = FakeUsersRepo().apply { loadForever = true }
        val viewModel = OverviewViewModel(repo)
        viewModel.onCreate()
        advanceUntilIdle()
        viewModel.requestNewUsersFromRepo()
        advanceUntilIdle()
        Assert.assertFalse(repo.hasTriedToReloadWhileStillBusy)
    }

    @Test
    fun `should always set loading state to false when results have been received`() = runTest {
        // Happy case, no error
        var viewModel = OverviewViewModel(FakeUsersRepo())
        viewModel.onCreate()
        advanceUntilIdle()
        Assert.assertFalse(viewModel.uiState.value.isLoading)

        // Sad case with error
        viewModel = OverviewViewModel(FakeUsersRepo().apply { returnResultFailure = true })
        viewModel.onCreate()
        advanceUntilIdle()
        Assert.assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `should append the correct number of users when a new page is loaded`() = runTest {
        var viewModel = OverviewViewModel(FakeUsersRepo())
        Assert.assertEquals(viewModel.uiState.value.users.size, 0 )
        viewModel.onCreate()
        advanceUntilIdle()
        Assert.assertEquals(viewModel.uiState.value.users.size, 1 )
        viewModel.requestNewUsersFromRepo()
        advanceUntilIdle()
        Assert.assertEquals(viewModel.uiState.value.users.size, 2)
        viewModel.requestNewUsersFromRepo()
        advanceUntilIdle()
        Assert.assertEquals(viewModel.uiState.value.users.size, 3)
    }

    @Test
    fun `collecting users should only be initialized once`() = runTest {
        var viewModel = OverviewViewModel(FakeUsersRepo())
        Assert.assertEquals(viewModel.uiState.value.users.size, 0)
        viewModel.onCreate()
        advanceUntilIdle()
        Assert.assertEquals(viewModel.uiState.value.users.size, 1)
        viewModel.onCreate()
        advanceUntilIdle()
        Assert.assertEquals(viewModel.uiState.value.users.size, 1)
        viewModel.onCreate()
        advanceUntilIdle()
        Assert.assertEquals(viewModel.uiState.value.users.size, 1)
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
