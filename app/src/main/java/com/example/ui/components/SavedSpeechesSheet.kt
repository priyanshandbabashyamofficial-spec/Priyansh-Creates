package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SavedSpeech
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedSpeechesSheet(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  savedSpeeches: List<SavedSpeech>,
  onLoadSpeech: (SavedSpeech) -> Unit,
  onDeleteSpeech: (SavedSpeech) -> Unit,
  modifier: Modifier = Modifier
) {
  if (!isOpen) return

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    modifier = modifier.testTag("saved_speeches_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.75f)
        .padding(horizontal = 16.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "Saved Speeches & History",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Close",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
        modifier = Modifier.padding(bottom = 12.dp)
      )

      if (savedSpeeches.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(text = "🎙️", fontSize = 42.sp)
            Text(
              text = "No saved speeches yet",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Tap 'Save' on any speech to store it here",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 24.dp)
        ) {
          items(savedSpeeches, key = { it.id }) { speech ->
            SavedSpeechCard(
              speech = speech,
              onLoad = {
                onLoadSpeech(speech)
                onDismiss()
              },
              onDelete = { onDeleteSpeech(speech) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun SavedSpeechCard(
  speech: SavedSpeech,
  onLoad: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
  val dateStr = dateFormat.format(Date(speech.timestamp))

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("saved_speech_${speech.id}")
      .clickable(onClick = onLoad),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.weight(1f)
      ) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier.size(42.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(text = speech.voiceAvatar, fontSize = 20.sp)
          }
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = speech.title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${speech.voiceName} • ${speech.pitch}x pitch • ${speech.speed}x speed",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = dateStr,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onLoad,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.EditNote,
            contentDescription = "Load into editor",
            tint = MaterialTheme.colorScheme.primary
          )
        }

        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete speech",
            tint = MaterialTheme.colorScheme.error
          )
        }
      }
    }
  }
}
