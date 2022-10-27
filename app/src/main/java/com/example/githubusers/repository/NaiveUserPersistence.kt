package com.example.githubusers.repository

import androidx.room.withTransaction
import com.example.githubusers.repository.room.StorableGithubUser
import com.example.githubusers.repository.room.UserDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface NaiveUserPersistence {
    suspend fun persist(users: List<StorableGithubUser>)
    suspend fun restore(): List<StorableGithubUser>
    suspend fun clearAll()
    suspend fun latestPage(): Int
}

/**
 * A naive implementation so that the app can be used while offline.
 * I originally tried to achieve this with Jetpack RemoteMediator, but it wouldn't react to
 * overscroll of the users list. I wasted more than 6 hours on that. Kind of my own fault
 * though, RemoteMediator is still in alpha.
 */
class DefaultNaiveUserPersistence @Inject constructor(
    private val database: UserDatabase,
    private val dispatcher: CoroutineDispatcher
) : NaiveUserPersistence {
    private val userDao = database.userDao()

    override suspend fun persist(users: List<StorableGithubUser>) {
        withContext(dispatcher) {
            database.withTransaction {
                userDao.insertAll(users)
            }
        }
    }

    override suspend fun latestPage() = withContext(dispatcher) {
        return@withContext userDao.getLatestPage()
    }

    override suspend fun restore() = withContext(dispatcher) {
        return@withContext userDao.getAll()
    }

    override suspend fun clearAll() {
        withContext(Dispatchers.IO) { userDao.clearAll() }
    }
}