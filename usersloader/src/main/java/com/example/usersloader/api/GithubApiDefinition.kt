package com.example.usersloader.api

import retrofit2.http.GET
import retrofit2.http.Query

internal interface GithubApiDefinition {

    @GET("search/users")
    suspend fun getUserSearchResponse(
        @Query("q") filter: String = "sort:followers",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): retrofit2.Response<GithubUserSearchResponse>
}