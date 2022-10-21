package com.example.usersloader

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface UsersLoader {
    suspend fun requestUsers(): Flow<String>
}

class DefaultUsersLoader : UsersLoader {

    override suspend fun requestUsers() = flow {
        emit("Gerd")
    }
}