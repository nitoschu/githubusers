package com.example.githubusers

import com.example.githubusers.repository.UsersPagingSource
import com.example.usersloader.GithubUser
import com.example.usersloader.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.net.UnknownHostException

internal class FakeUsersRepo : UsersRepository {

    var loadForever = false
    var hasTriedToReloadWhileStillBusy = false
    private var isBusyLoading = false
    var returnResultFailure = false
    var requestUsersCalledNumber = 0

    private val success = Result.success(listOf(mockGithubUser))
    private val failure: Result<List<GithubUser>> = Result.failure(UnknownHostException())

    private val _users = MutableSharedFlow<Result<List<GithubUser>>>()
    override val usersResults: Flow<Result<List<GithubUser>>> = _users

    override suspend fun requestUsers(page: Int, perPage: Int): Result<List<GithubUser>> {
        requestUsersCalledNumber++
        if (isBusyLoading) hasTriedToReloadWhileStillBusy = true
        if (loadForever) {
            isBusyLoading = true
            return success
        }

        if (returnResultFailure) {
            _users.emit(failure)
            return failure
        }

        _users.emit(success)
        return success
    }
}

val mockGithubUser = GithubUser(
    id = 1,
    login = "Bert",
    nodeId = "abcd",
    avatarUrl = "abcd",
    gravatarId = "abcd",
    url = "abcd",
    htmlUrl = "abcd",
    followersUrl = "abcd",
    followingUrl = "abcd",
    gistsUrl = "abcd",
    starredUrl = "abcd",
    subscriptionsUrl = "abcd",
    organizationsUrl = "abcd",
    reposUrl = "abcd",
    eventsUrl = "abcd",
    receivedEventsUrl = "abcd",
    type = "user",
    siteAdmin = true,
    score = 1f
)

internal fun fakeUserPager() = UsersPagingSource(FakeUsersRepo())
internal fun fakeUsePager(fakeRepo: FakeUsersRepo) = UsersPagingSource(fakeRepo)