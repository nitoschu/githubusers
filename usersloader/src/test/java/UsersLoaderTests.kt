@file:OptIn(ExperimentalCoroutinesApi::class)

import com.example.usersloader.DefaultUsersLoader
import com.example.usersloader.GithubUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.UnknownHostException
import kotlin.Result.Companion.failure

class UsersLoaderTests {

    @Test
    fun `should provide a list of users`() = runTest {
        val source = FakeGithubDataSource()
        val loader = DefaultUsersLoader(source)
        val results = mutableListOf<Result<List<GithubUser>>>()

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            loader.requestUsers().toList(results)
        }

        advanceUntilIdle()

        val result = results[0]
        assertEquals(true, result.isSuccess)
        assertEquals(false, result.isFailure)

        val user: GithubUser = source.fakeResponse.getOrNull()!![0]
        val testObject: GithubUser = result.getOrNull()!![0]
        assertEquals(user.login, testObject.login)
        collectJob.cancel()
    }

    @Test
    fun `should provide an error result`() = runTest {
        val source = FakeGithubDataSource(fakeResponse = failure(UnknownHostException()))
        val loader = DefaultUsersLoader(source)
        val results = mutableListOf<Result<List<GithubUser>>>()

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            loader.requestUsers().toList(results)
        }

        val result = results[0]
        assertEquals(false, result.isSuccess)
        assertEquals(true, result.isFailure)

        val exception = result.exceptionOrNull()!!
        assertTrue(exception is UnknownHostException)

        collectJob.cancel()
    }
}