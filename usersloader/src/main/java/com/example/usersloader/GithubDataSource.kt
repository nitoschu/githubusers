package com.example.usersloader

import com.example.usersloader.api.GithubApiDefinition
import com.example.usersloader.api.GithubUser
import com.example.usersloader.api.GithubUserSearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.UnknownHostException

internal object DefaultGithubDataSource : GithubDataSource {

    private val api = buildRetrofit().create(GithubApiDefinition::class.java)

    override suspend fun queryUsers(page: Int, perPage: Int) = try {
        api.getUserSearchResponse(page = page, perPage = perPage).toGithubUsers()
    } catch (e: UnknownHostException) {
        Result.failure(e)
    }

    private fun retrofit2.Response<GithubUserSearchResponse>.toGithubUsers() =
        if (isSuccessful) {
            Result.success(body()?.items ?: emptyList())
        } else {
            Result.failure(IOException(errorBody()?.string() ?: "An error occurred"))
        }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GITHUB_API)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildClient())
            .build()
    }

    private fun buildClient(allowLogging: Boolean = false): OkHttpClient {
        val level = if (allowLogging) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        val intercept = HttpLoggingInterceptor().setLevel(level)
        return OkHttpClient.Builder().addInterceptor(intercept).build()
    }

    private const val GITHUB_API = "https://api.github.com/"
}

interface GithubDataSource {
    suspend fun queryUsers(page: Int, perPage: Int): Result<List<GithubUser>>
}