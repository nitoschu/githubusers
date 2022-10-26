package com.example.githubusers

import com.example.githubusers.repository.NaiveUserPersistence
import com.example.githubusers.repository.room.StorableGithubUser

internal class FakeUserPersistence : NaiveUserPersistence {

    val persistedUsers = mutableListOf<StorableGithubUser>()

    override suspend fun persist(users: List<StorableGithubUser>) {
        persistedUsers.addAll(users)
    }

    override suspend fun restore() = persistedUsers.toList()

    override suspend fun clearAll() {
        persistedUsers.clear()
    }
}