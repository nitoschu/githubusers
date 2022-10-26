package com.example.githubusers.repository.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import java.util.*

@Entity(tableName = "users")
data class StorableGithubUser(
    @PrimaryKey(autoGenerate = false) val id: Long,
    @ColumnInfo(name = "login") val login: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String,
    @ColumnInfo(name = "persisted_at") var persistedAt: Long? = null
)

@Dao
interface UserDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertAll(users: List<StorableGithubUser>)

    @Insert(onConflict = REPLACE)
    suspend fun insert(user: StorableGithubUser)

    @Query("SELECT * FROM users ORDER BY persisted_at DESC")
    fun getAll(): List<StorableGithubUser>

    @Query("DELETE FROM users")
    suspend fun clearAll()
}

@Database(entities = [StorableGithubUser::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}