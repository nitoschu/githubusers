package com.example.githubusers.repository

import com.example.githubusers.repository.room.StorableGithubUser
import com.example.usersloader.GithubUser

fun GithubUser.toStorableGithubUser() = StorableGithubUser(
    id = id,
    login = login,
    avatarUrl = avatarUrl
)

fun List<GithubUser>.toStorableGithubUsers(): List<StorableGithubUser> {
    val result = mutableListOf<StorableGithubUser>()
    forEach { user -> result.add(user.toStorableGithubUser()) }
    return result.toList()
}