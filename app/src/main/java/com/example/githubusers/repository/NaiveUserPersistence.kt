package com.example.githubusers.repository

import androidx.room.withTransaction
import com.example.githubusers.repository.room.StorableGithubUser
import com.example.githubusers.repository.room.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

interface NaiveUserPersistence {
    suspend fun persist(users: List<StorableGithubUser>)
    suspend fun restore(): List<StorableGithubUser>
    suspend fun clearAll()
}

class DefaultNaiveUserPersistence @Inject constructor(
    private val database: UserDatabase
) : NaiveUserPersistence {
    private val userDao = database.userDao()


    override suspend fun persist(users: List<StorableGithubUser>) {
        users.forEach { it.persistedAt = System.nanoTime() }
        withContext(Dispatchers.IO) {
            database.withTransaction {
                userDao.insertAll(users)
            }
        }
    }

    override suspend fun restore() = withContext(Dispatchers.IO) {
        return@withContext userDao.getAll()
    }

    override suspend fun clearAll() {
        withContext(Dispatchers.IO) { userDao.clearAll() }
    }

}