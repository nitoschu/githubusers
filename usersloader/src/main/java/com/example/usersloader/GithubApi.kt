package com.example.usersloader

import retrofit2.http.GET
import retrofit2.http.Query

internal interface GithubApi {
    @GET("search/users")
    suspend fun getUsers(
        @Query("q") filter: String = "sort:followers",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): retrofit2.Response<GithubUserSearchResponse>
}