package com.example.githubusers.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.githubusers.repository.room.StorableGithubUser
import com.example.usersloader.api.GithubUser
import com.example.usersloader.UsersRepository
import javax.inject.Inject

class UsersPagingSource @Inject constructor(
    private val userSource: UsersRepository
) : PagingSource<Int, StorableGithubUser>() {

    override fun getRefreshKey(state: PagingState<Int, StorableGithubUser>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StorableGithubUser> {
        val pageNumber = params.key ?: 0
        val response = userSource.requestUsers(pageNumber, params.loadSize)
        if (response.isFailure) return LoadResult.Error(
            response.exceptionOrNull() ?: UnknownError("Unknown error")
        )

        val users = response.toUsers().toStorableGithubUsers(pageNumber + 1)
        if (users.isEmpty()) return LoadResult.Error(Throwable())

        val prevKey = if (pageNumber > 0) pageNumber - 1 else null
        val nextKey = pageNumber + 1

        return LoadResult.Page(users, prevKey, nextKey)
    }

    private fun Result<List<GithubUser>>.toUsers() = if (isSuccess) {
        getOrNull() ?: emptyList()
    } else {
        emptyList() // The ViewModel decides how to handle errors.
    }
}