package com.example.usersloader

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import java.net.UnknownHostException

internal object DefaultGithubDataSource : GithubDataSource {

    private val api = getRetrofit().create(GithubApi::class.java)

    override suspend fun queryUsers(page: Int, perPage: Int) = try {
        val response = api.getUsers(page, perPage)
        handleResponse(response)
    } catch (e: UnknownHostException) {
        Result.failure(e)
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

    private const val GITHUB_API = "https://api.github.com/"
}

internal interface GithubApi {
    @GET("users")
    suspend fun getUsers(@Query("page") page: Int, @Query("per_page") perPage: Int): retrofit2.Response<List<GithubUser>>
}

interface GithubDataSource {
    suspend fun queryUsers(page: Int = 0, perPage: Int = 10): Result<List<GithubUser>>
}