package com.example.usersloader

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class UsersLoader {

    val users = MutableSharedFlow<String>()

    fun requestUsers(scope: CoroutineScope) {
        scope.launch {
            users.emit("Gerd")
        }
    }
}