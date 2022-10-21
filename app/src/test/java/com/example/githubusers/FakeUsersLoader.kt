package com.example.githubusers

import com.example.usersloader.UsersLoader
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

internal class FakeUsersLoader : UsersLoader {
    override suspend fun requestUsers() = flow {
        emit("Gerd")
    }

}