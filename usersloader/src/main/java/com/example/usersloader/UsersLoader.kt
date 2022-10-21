package com.example.usersloader

import kotlinx.coroutines.flow.MutableStateFlow

class UsersLoader {

    val users = MutableStateFlow("")

    suspend fun requestUsers() {
        users.emit("Gerd")
    }
}