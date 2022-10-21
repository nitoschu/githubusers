package com.example.usersloader

import kotlinx.coroutines.flow.*


interface UsersLoader {
    suspend fun requestUsers(): Flow<String>
}

class DefaultUsersLoader : UsersLoader {

    override suspend fun requestUsers() = flow {
        emit("Gerd")
    }
}