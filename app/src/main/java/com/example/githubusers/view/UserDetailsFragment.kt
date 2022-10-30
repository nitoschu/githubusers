package com.example.githubusers.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.* // ktlint-disable no-wildcard-imports
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.compose.rememberAsyncImagePainter
import com.example.githubusers.view.designsystem.RoundImage

class UserDetailsFragment : Fragment() {

    private val args: UserDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            MaterialTheme {
                UserDetails(
                    args.login,
                    args.id,
                    args.avatarUrl,
                    args.htmlUrl,
                    args.score
                )
            }
        }
    }
}

@Composable
fun UserDetails(
    login: String,
    id: Long,
    avatarUrl: String,
    htmlUrl: String,
    score: Float
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RoundImage(
            painter = rememberAsyncImagePainter(avatarUrl),
            modifier = Modifier
                .size(300.dp)
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        UserLogin(login, fontSize = 20.sp)
        Text("ID: $id", fontSize = 20.sp)
        Text("Link: $htmlUrl")
        Text("Score: $score")
    }
}

@Composable
fun UserLogin(login: String, fontSize: TextUnit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (login == "JakeWharton") Text("  \uD83D\uDC51  ", fontSize = fontSize)
        Text(login, fontSize = fontSize)
        if (login == "JakeWharton") Text("  \uD83D\uDC51  ", fontSize = fontSize)
    }
}