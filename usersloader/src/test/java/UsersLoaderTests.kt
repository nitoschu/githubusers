@file:OptIn(ExperimentalCoroutinesApi::class)

import com.example.usersloader.DefaultUsersLoader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UsersLoaderTests {

    @Test
    fun `should provide a list of users`() = runTest {
        val loader = DefaultUsersLoader()
        val users = mutableListOf<String>()

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            loader.requestUsers().toList(users)

        }

        runCurrent()
        advanceUntilIdle()
        assertEquals("Gerd", users[0])
        collectJob.cancel()
    }
}