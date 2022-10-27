package com.example.usersloader

import com.example.usersloader.api.GithubUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext

/**
 * An interface for obtaining [GithubUser]s from a remote source.
 * The default implementation will query users from the
 * <a href="https://docs.github.com/en/rest/search">Github search API</a>.
 *
 * Please note that this implementation is not authorizing to the remote source and therefore
 * subject to <a href="https://docs.github.com/en/rest/rate-limit"Github rate limitation</a>.
 *
 * The data in the backend is being updated very quickly, so users might be displayed several times,
 * especially when there are long pauses between fetches. This effect can be mitigated by
 * raising the page size.
 */
interface UsersRepository {
    val usersResults: Flow<Result<List<GithubUser>>>
    suspend fun requestUsers(page: Int = 0, perPage: Int = 10): Result<List<GithubUser>>
}

class DefaultUsersRepo(
    private val githubSource: GithubDataSource = DefaultGithubDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UsersRepository {

    private val _usersResults = MutableSharedFlow<Result<List<GithubUser>>>()
    override val usersResults: Flow<Result<List<GithubUser>>> = _usersResults

    override suspend fun requestUsers(page: Int, perPage: Int): Result<List<GithubUser>> {
        val result = withContext(dispatcher) {
            return@withContext githubSource.queryUsers(page, perPage)
        }
        _usersResults.emit(result)
        return result
    }
}