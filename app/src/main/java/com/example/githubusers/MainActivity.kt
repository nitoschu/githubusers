package com.example.githubusers

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.HiltAndroidApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

@HiltAndroidApp
class GithubUsersApp : Application()