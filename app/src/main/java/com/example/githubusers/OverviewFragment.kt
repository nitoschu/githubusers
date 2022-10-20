package com.example.githubusers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData

class OverviewFragment : Fragment() {

    private val viewModel: OverviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            MaterialTheme {
                OverviewScreen(viewModel.user)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.loadUser()
    }
}

@Composable
fun OverviewScreen(userData: LiveData<String>) {
    Text(userData.value ?: "")
}