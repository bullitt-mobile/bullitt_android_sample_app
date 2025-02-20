package com.bullitt.sampleapp.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bullitt.sampleapp.store.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageItem(message: Message) {
  val isOutgoing = message.isOutgoing
  val backgroundColor =
    if (isOutgoing) {
      Color(0xFF2196F3) // Blue for outgoing messages
    } else {
      Color(0xFFE0E0E0) // Light gray for incoming messages
    }

  val textColor =
    if (isOutgoing) {
      Color.White
    } else {
      Color.Black
    }

  val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
  val formattedTime = dateFormat.format(Date(message.timestamp))

  Box(
    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    contentAlignment = if (isOutgoing) Alignment.CenterEnd else Alignment.CenterStart,
  ) {
    Column(horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start) {
      Card(
        shape =
          RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp,
            bottomStart = if (isOutgoing) 12.dp else 0.dp,
            bottomEnd = if (isOutgoing) 0.dp else 12.dp,
          ),
        colors = CardDefaults.cardColors().copy(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(),
        modifier = Modifier.widthIn(max = 280.dp),
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = message.content,
            color = textColor,
            style = MaterialTheme.typography.bodyLarge,
          )
        }
      }

      Row(modifier = Modifier.padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = formattedTime, style = MaterialTheme.typography.labelSmall, color = Color.Gray)

        if (isOutgoing) {
          Spacer(modifier = Modifier.width(4.dp))
          StatusIndicator(message.status)
        }
      }
    }
  }
}

@Composable
fun StatusIndicator(status: Message.Status) {
  val statusIcon =
    when (status) {
      Message.Status.SENT -> "✓"
      Message.Status.DELIVERED -> "✓✓"
      Message.Status.READ -> "✓✓" // Blue color will be applied
      Message.Status.FAILED -> "!"
      Message.Status.SENDING -> "…"
    }

  val statusColor =
    when (status) {
      Message.Status.READ -> Color(0xFF2196F3)
      Message.Status.FAILED -> Color.Red
      else -> Color.Gray
    }

  Text(
    text = statusIcon,
    color = statusColor,
    style = MaterialTheme.typography.labelSmall,
    textAlign = TextAlign.End,
  )
}

@Preview
@Composable
fun MessageItemPreview() {
  val incomingMessage =
    Message(
      isOutgoing = false,
      partnerNumber = 11234567890,
      content = "Hey there! How's it going?",
      timestamp = System.currentTimeMillis(),
      status = Message.Status.DELIVERED
    )

  val outgoingMessage =
    Message(
      isOutgoing = true,
      partnerNumber = 11234567890,
      content = "I'm doing good, how are you?",
      timestamp = System.currentTimeMillis(),
      status = Message.Status.READ
    )

  Column(modifier = Modifier.fillMaxWidth()) {
    MessageItem(incomingMessage)
    MessageItem(outgoingMessage)
  }
}
