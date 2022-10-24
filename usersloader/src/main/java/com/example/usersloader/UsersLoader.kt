package com.example.usersloader

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface UsersLoader {
    suspend fun requestUsers(): Flow<Result<List<GithubUser>>>
}

class DefaultUsersLoader(
    private val githubSource: GithubDataSource = DefaultGithubDataSource
) : UsersLoader {

    override suspend fun requestUsers(): Flow<Result<List<GithubUser>>> {
        return flow {
            emit(githubSource.request())
        }
    }
}