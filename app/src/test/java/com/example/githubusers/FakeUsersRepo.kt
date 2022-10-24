package com.example.githubusers

import com.example.usersloader.GithubUser
import com.example.usersloader.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

internal class FakeUsersRepo : UsersRepository {

    private val _users = MutableSharedFlow<Result<List<GithubUser>>>()
    override val users: Flow<Result<List<GithubUser>>> = _users

    override suspend fun requestUsers() {
        _users.emit(Result.success(listOf(mockGithubUser)))
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
