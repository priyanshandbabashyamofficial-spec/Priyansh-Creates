package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

@Composable
fun ExportAudioDialog(
  isExporting: Boolean,
  exportedFile: File?,
  onDismiss: () -> Unit,
  onShare: (File) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  if (isExporting) {
    AlertDialog(
      onDismissRequest = {},
      title = { Text("Synthesizing Audio...") },
      text = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          modifier = Modifier.padding(vertical = 8.dp)
        ) {
          CircularProgressIndicator(modifier = Modifier.size(36.dp))
          Text(
            text = "Rendering high quality speech with selected voice parameters...",
            style = MaterialTheme.typography.bodyMedium
          )
        }
      },
      confirmButton = {},
      shape = RoundedCornerShape(20.dp),
      modifier = modifier.testTag("exporting_loading_dialog")
    )
  } else if (exportedFile != null) {
    AlertDialog(
      onDismissRequest = onDismiss,
      icon = {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier.size(52.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(30.dp)
            )
          }
        }
      },
      title = {
        Text(
          text = "Audio Ready!",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Your synthesized speech has been generated as a high-quality WAV audio file.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.AudioFile,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
              Text(
                text = exportedFile.name,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { onShare(exportedFile) },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("share_audio_button")
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Share Audio")
        }
      },
      dismissButton = {
        TextButton(
          onClick = onDismiss,
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Done")
        }
      },
      shape = RoundedCornerShape(20.dp),
      modifier = modifier.testTag("export_success_dialog")
    )
  }
}
