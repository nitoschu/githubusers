@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.githubusers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.* // ktlint-disable no-wildcard-imports
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.testng.Assert

@ExtendWith(MainDispatcherExtension::class)
class OverviewViewModelTest {

    @Test
    fun `should not reload while still loading`() = runTest {
        val repo = FakeUsersRepo().apply { loadForever = true }
        val viewModel = OverviewViewModel(fakeUsePager(repo))
        viewModel.startCollectingUsers()
        advanceUntilIdle()
        viewModel.startCollectingUsers()
        advanceUntilIdle()
        Assert.assertFalse(repo.hasTriedToReloadWhileStillBusy)
    }

    @Test
    fun `should always set loading state to false when results have been received`() = runTest {
        // Happy case, no error
        var viewModel = OverviewViewModel(fakeUserPager())
        viewModel.startCollectingUsers()
        advanceUntilIdle()
        Assert.assertFalse(viewModel.uiState.value.isLoading)

        // Sad case with error
        val repo = FakeUsersRepo().apply { returnResultFailure = true }
        viewModel = OverviewViewModel(fakeUsePager(repo))
        viewModel.startCollectingUsers()
        advanceUntilIdle()
        Assert.assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `collection of users should only be initialized once`() = runTest {
        val repo = FakeUsersRepo()
        val viewModel = OverviewViewModel(UsersPager(repo))
        Assert.assertEquals(repo.requestUsersCalledNumber, 0)
        viewModel.startCollectingUsers()
        advanceUntilIdle()
        Assert.assertEquals(repo.requestUsersCalledNumber, 1)
        viewModel.startCollectingUsers()
        advanceUntilIdle()
        Assert.assertEquals(repo.requestUsersCalledNumber, 1)
        viewModel.startCollectingUsers()
        advanceUntilIdle()
        Assert.assertEquals(repo.requestUsersCalledNumber, 1)
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
