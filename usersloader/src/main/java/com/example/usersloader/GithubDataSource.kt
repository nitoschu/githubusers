package com.example.usersloader

import okhttp3.* // ktlint-disable no-wildcard-imports
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.IOException
import java.net.UnknownHostException

object DefaultGithubDataSource : GithubDataSource {

    override suspend fun request(): Result<List<GithubUser>> {

        val api = getRetrofit().create(GithubApi::class.java)

        return try {
            val response = api.getUsers()
            handleResponse(response)
        } catch (e: UnknownHostException) {
            Result.failure(e)
        }
    }

    private fun handleResponse(response: retrofit2.Response<List<GithubUser>>):
            Result<List<GithubUser>> {
        return if (response.isSuccessful) {
            Result.success(response.body() ?: emptyList())
        } else {
            Result.failure(IOException(response.errorBody()?.string() ?: "An error occurred"))
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GITHUB_API)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())
            .build()
    }

    private fun getClient(allowLogging: Boolean = false): OkHttpClient {
        val level = if (allowLogging) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        val intercept = HttpLoggingInterceptor().setLevel(level)
        return OkHttpClient.Builder().addInterceptor(intercept).build()
    }

    private val GITHUB_API = "https://api.github.com/"
}

interface GithubDataSource {
    suspend fun request(): Result<List<GithubUser>>
}

interface GithubApi {
    @GET("users")
    suspend fun getUsers(): retrofit2.Response<List<GithubUser>>
}