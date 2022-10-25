package com.example.githubusers

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

    private val _users = MutableSharedFlow<Result<List<GithubUser>>>()
    override val users: Flow<Result<List<GithubUser>>> = _users

    override suspend fun requestUsers(page: Int, perPage: Int): Result<List<GithubUser>> {
        requestUsersCalledNumber++
        if (isBusyLoading) hasTriedToReloadWhileStillBusy = true
        if (loadForever) {
            isBusyLoading = true
            return Result.success(listOf(mockGithubUser))
        }

        if (returnResultFailure) {
            _users.emit(Result.failure(UnknownHostException()))
            return Result.failure(UnknownHostException())
        }

        return Result.success(listOf(mockGithubUser))
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
    score = 1f,
    name = "Bert",
    company = "Bert Company",
    blog = "abcd",
    location = "abcd",
    email = "abcd",
    hireable = "false",
    bio = "abcd",
    publicRepos = 0,
    publicGists = 0,
    followers = 1,
    following = 1
)

internal fun fakeUserPager() = UsersPager(FakeUsersRepo())
internal fun fakeUsePager(fakeRepo: FakeUsersRepo) = UsersPager(fakeRepo)