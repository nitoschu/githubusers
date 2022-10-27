@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.githubusers

import com.example.githubusers.view.OverviewViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
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
        val viewModel = testMe(repo)
        viewModel.collectUserResultsFromRepo()
        advanceUntilIdle()
        viewModel.retryCollectingUsers()
        advanceUntilIdle()
        Assert.assertFalse(repo.hasTriedToReloadWhileStillBusy)
    }

    @Test
    fun `should set loading state when loading`() {
        val viewModel = testMe()
        Assert.assertTrue(viewModel.uiState.value.isLoading)
        viewModel.collectUserResultsFromRepo()
        Assert.assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `should set loading state to false when users received`() = runTest {
        val repo = FakeUsersRepo()
        val viewModel = testMe(repo)
        launch { viewModel.collectUserResultsFromRepo() }
        launch { repo.requestUsers() }
        advanceUntilIdle()
        Assert.assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `should set loading state to false when error received`() = runTest {
        val repo = FakeUsersRepo().apply { returnResultFailure = true }
        val viewModel = testMe(repo)
        launch { viewModel.collectUserResultsFromRepo() }
        launch { repo.requestUsers() }
        advanceUntilIdle()
        Assert.assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `ViewModel should not manually trigger the paging datasource`() = runTest {
        val repo = FakeUsersRepo()
        val viewModel = testMe(repo)
        Assert.assertEquals(repo.requestUsersCalledNumber, 0)
        viewModel.collectUserResultsFromRepo()
        Assert.assertEquals(repo.requestUsersCalledNumber, 0)

        viewModel.retryCollectingUsers()
        Assert.assertEquals(repo.requestUsersCalledNumber, 0)
        viewModel.retryCollectingUsers()
        Assert.assertEquals(repo.requestUsersCalledNumber, 0)
    }

    @Test
    fun `new users should be persisted`() = runTest {
        val persist = FakeUserPersistence()
        val repo = FakeUsersRepo()
        val viewModel = testMe(repo, persist)
        Assert.assertEquals(persist.persistedUsers.size, 0)
        launch { viewModel.collectUserResultsFromRepo() }
        launch { repo.requestUsers() }
        advanceUntilIdle()
        Assert.assertEquals(persist.persistedUsers.size, 1)
    }
}

internal fun testMe(
    repo: FakeUsersRepo = FakeUsersRepo(),
    persistence: FakeUserPersistence = FakeUserPersistence()
) = OverviewViewModel(repo, persistence)

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
