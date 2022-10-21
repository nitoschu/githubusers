@file:OptIn(ExperimentalCoroutinesApi::class)

import com.example.usersloader.DefaultUsersLoader
import com.example.usersloader.UsersLoader
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
            loader.users.toList(users)
        }
        loader.requestUsers()


        runCurrent()
        advanceUntilIdle()
        assertEquals("", users[0])
        assertEquals("Gerd", users[1])
        collectJob.cancel()
    }
}