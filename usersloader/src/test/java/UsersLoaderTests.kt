@file:OptIn(ExperimentalCoroutinesApi::class)

import com.example.usersloader.DefaultUsersRepo
import com.example.usersloader.GithubUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
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
        val repo = DefaultUsersRepo(source, dispatcher = StandardTestDispatcher(testScheduler))
        val results = mutableListOf<Result<List<GithubUser>>>()

        val collectJob = launch() {
            repo.users.toList(results)
        }

        repo.requestUsers()
        advanceUntilIdle()

        val result = results[0]
        assertEquals(true, result.isSuccess)
        assertEquals(false, result.isFailure)

        val user = source.fakeResponse.getOrNull()!![0]
        val testObject = result.getOrNull()!![0]
        assertEquals(user.login, testObject.login)

        collectJob.cancel()
    }

    @Test
    fun `should provide an error result`() = runTest {
        val source = FakeGithubDataSource(fakeResponse = failure(UnknownHostException()))
        val repo = DefaultUsersRepo(source, dispatcher = StandardTestDispatcher(testScheduler))
        val results = mutableListOf<Result<List<GithubUser>>>()

        val collectJob = launch() {
            repo.users.toList(results)
        }

        repo.requestUsers()
        advanceUntilIdle()

        val result = results[0]
        assertEquals(false, result.isSuccess)
        assertEquals(true, result.isFailure)

        val exception = result.exceptionOrNull()!!
        assertTrue(exception is UnknownHostException)

        collectJob.cancel()
    }

    @Test
    fun `should provide an empty list of users`() = runTest {
        val testData = Result.success(emptyList<GithubUser>())
        val source = FakeGithubDataSource(testData)
        val repo = DefaultUsersRepo(source, dispatcher = StandardTestDispatcher(testScheduler))
        val results = mutableListOf<Result<List<GithubUser>>>()

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            repo.users.toList(results)
        }

        repo.requestUsers()
        advanceUntilIdle()

        val result = results[0]
        assertEquals(true, result.isSuccess)
        assertEquals(false, result.isFailure)
        val emptyUsersList = result.getOrThrow()
        assertTrue(emptyUsersList.isEmpty())

        collectJob.cancel()
    }

    @Test
    fun `should use pagination`() = runTest {
        val source = FakeGithubDataSource()
        val repo = DefaultUsersRepo(source, dispatcher = StandardTestDispatcher(testScheduler))
        val page = 4
        val perPage = 17
        repo.requestUsers(page = page, perPage = perPage)
        advanceUntilIdle()
        assertEquals(page, source.page)
        assertEquals(perPage, source.perPage)
    }
}