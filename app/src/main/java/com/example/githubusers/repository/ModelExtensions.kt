package com.example.githubusers.repository

import com.example.githubusers.repository.room.StorableGithubUser
import com.example.usersloader.api.GithubUser

fun GithubUser.toStorableGithubUser(page: Int) = StorableGithubUser(
    id = id,
    login = login,
    avatarUrl = avatarUrl,
    htmlUrl = htmlUrl,
    score = score,
    page = page
)

fun List<GithubUser>.toStorableGithubUsers(firstPage: Int): List<StorableGithubUser> {
    val result = mutableListOf<StorableGithubUser>()
    var i = firstPage
    forEach { user -> result.add(user.toStorableGithubUser(++i)) }
    return result.toList()
}