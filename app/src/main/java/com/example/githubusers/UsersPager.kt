package com.example.githubusers

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.usersloader.GithubUser
import com.example.usersloader.UsersRepository
import javax.inject.Inject

class UsersPager @Inject constructor(
    private val userSource: UsersRepository
) : PagingSource<Int, GithubUser>() {

    override fun getRefreshKey(state: PagingState<Int, GithubUser>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GithubUser> {
        val pageNumber = params.key ?: 0
        val users = requestUsers(pageNumber, params.loadSize).toUsers()

        val prevKey = if (pageNumber == 0) null else pageNumber
        val nextKey = if (users.isEmpty()) null else pageNumber + 1

        return LoadResult.Page(users, prevKey, nextKey)
    }

    private fun Result<List<GithubUser>>.toUsers() = if (isSuccess) {
        getOrNull() ?: emptyList()
    } else {
        emptyList()
    }

    suspend fun requestUsers(page: Int, perPage: Int) = userSource.requestUsers(page, perPage)
}