package com.bullitt.sampleapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.bullitt.sampleapp.state.ChatViewModel
import com.bullitt.sampleapp.ui.component.ChatList
import com.bullitt.sampleapp.ui.component.MessageComposeBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(chatViewModel: ChatViewModel = hiltViewModel()) {
  // Please modify this to the required number to send messages to
  val chattingWith = 14045551001

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(chattingWith.toString()) },
        actions = {
          // TODO: Plus icon to connect to bluetooth device
        },
        colors =
          TopAppBarDefaults.topAppBarColors()
            .copy(containerColor = MaterialTheme.colorScheme.primaryContainer),
      )
    },
    bottomBar = { MessageComposeBar(chattingWith = chattingWith, viewModel = chatViewModel) },
  ) { padding ->
    Column(modifier = Modifier.padding(padding)) {
      ChatList(chatWith = 14045551001, viewModel = chatViewModel)
    }
  }
}
