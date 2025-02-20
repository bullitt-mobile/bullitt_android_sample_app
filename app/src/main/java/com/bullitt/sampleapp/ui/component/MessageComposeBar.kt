package com.bullitt.sampleapp.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bullitt.sampleapp.state.ChatViewModel
import com.bullitt.sampleapp.store.Message
import kotlinx.coroutines.launch

@Composable
fun MessageComposeBar(chattingWith: Long, viewModel: ChatViewModel = hiltViewModel()) {
  val coroutineScope = rememberCoroutineScope()
  var messageText by remember { mutableStateOf("") }

  val sendMessage: () -> Unit = {
    val message =
      Message(
        status = Message.Status.SENDING,
        isOutgoing = true,
        partnerNumber = chattingWith,
        content = messageText,
        timestamp = System.currentTimeMillis(),
      )
    coroutineScope.launch { viewModel.sendMessage(message = message) }
  }

  Surface(
    modifier = Modifier.imePadding(),
    tonalElevation = 4.dp,
    color = MaterialTheme.colorScheme.surface,
  ) {
    Row(
      modifier =
        Modifier.navigationBarsPadding()
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      OutlinedTextField(
        value = messageText,
        onValueChange = { messageText = it },
        modifier = Modifier.weight(1f),
        placeholder = { Text("Type a message...") },
        keyboardOptions =
          KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Send),
        keyboardActions =
          KeyboardActions(
            onSend = {
              if (messageText.isNotBlank()) {
                sendMessage()
                messageText = ""
              }
            }
          ),
        maxLines = 4,
        shape = RoundedCornerShape(24.dp),
        colors =
          TextFieldDefaults.colors()
            .copy(
              unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
      )

      IconButton(
        onClick = {
          if (messageText.isNotBlank()) {
            sendMessage()
            messageText = ""
          }
        },
        modifier = Modifier.padding(start = 12.dp),
        enabled = messageText.isNotBlank(),
      ) {
        Card(
          shape = CircleShape,
          colors =
            CardDefaults.cardColors(
              containerColor =
                if (messageText.isNotBlank()) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            ),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Default.Send,
            contentDescription = "Send message",
            tint =
              if (messageText.isNotBlank()) MaterialTheme.colorScheme.onPrimary
              else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(10.dp),
          )
        }
      }
    }
  }
}

@Composable
@Preview
fun MessageComposeBarPreview() {
  MaterialTheme { MessageComposeBar(chattingWith = 14045551001) }
}
