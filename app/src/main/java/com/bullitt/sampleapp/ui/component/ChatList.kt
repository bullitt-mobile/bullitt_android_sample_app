package com.bullitt.sampleapp.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bullitt.sampleapp.state.ChatViewModel

/** @param chatWith The user number to chat with. +1 123 456 7890 will be written as 11234567890 */
@Composable
fun ChatList(chatWith: Long, viewModel: ChatViewModel = hiltViewModel()) {
  val messages by viewModel.getMessages(chatWith).collectAsState(listOf())

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
    reverseLayout = true,
  ) {
    items(messages.sortedByDescending { it.timestamp }) { message ->
      MessageItem(message)
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}
