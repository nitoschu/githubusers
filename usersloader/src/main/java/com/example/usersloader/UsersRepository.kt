package com.example.usersloader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext

interface UsersRepository {
    val users: Flow<Result<List<GithubUser>>>
    suspend fun requestUsers(page: Int = 0, perPage: Int = 10)
}

class DefaultUsersRepo(
    private val githubSource: GithubDataSource = DefaultGithubDataSource
) : UsersRepository {

    private val _users = MutableSharedFlow<Result<List<GithubUser>>>()
    override val users: Flow<Result<List<GithubUser>>> = _users

    override suspend fun requestUsers(page: Int, perPage: Int) {
        val result = withContext(Dispatchers.IO) {
            return@withContext githubSource.queryUsers(page, perPage)
        }
        _users.emit(result)
    }
}