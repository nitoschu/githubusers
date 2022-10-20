package com.example.githubusers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OverviewViewModel : ViewModel() {

    private val _user = MutableLiveData<String>()
    val user: LiveData<String> = _user

    fun loadUser() {
        viewModelScope.launch { _user.value = "Hans Dampf" }
    }
}