package com.example.githubusers.repository.room

import androidx.room.* // ktlint-disable no-wildcard-imports
import androidx.room.OnConflictStrategy.REPLACE
import java.util.* // ktlint-disable no-wildcard-imports

@Entity(tableName = "users")
data class StorableGithubUser(
    @PrimaryKey(autoGenerate = false) val id: Long,
    @ColumnInfo(name = "login") val login: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String,
    @ColumnInfo(name = "html_url") val htmlUrl: String,
    @ColumnInfo(name = "score") val score: Float,
    @ColumnInfo(name = "page") var page: Int
)

@Dao
interface UserDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(users: List<StorableGithubUser>)

    @Query("SELECT * FROM users ORDER BY page ASC")
    fun getAll(): List<StorableGithubUser>

    @Query("DELETE FROM users")
    suspend fun clearAll()

    @Query("SELECT MAX(page) FROM users")
    fun getLatestPage(): Int
}

@Database(entities = [StorableGithubUser::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}