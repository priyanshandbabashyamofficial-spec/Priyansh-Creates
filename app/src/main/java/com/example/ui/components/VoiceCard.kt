package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VoiceProfile

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectedVoiceHeroCard(
  voice: VoiceProfile,
  onOpenVoicePicker: () -> Unit,
  onPreviewVoice: () -> Unit,
  modifier: Modifier = Modifier
) {
  val cardGradient = Brush.linearGradient(
    colors = listOf(
      MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
      MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
    )
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("selected_voice_card")
      .clickable(onClick = onOpenVoicePicker),
    shape = RoundedCornerShape(22.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(cardGradient)
        .padding(18.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
          ) {
            // Voice Avatar with subtle glowing border
            Surface(
              modifier = Modifier.size(54.dp),
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
              border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = voice.avatarEmoji,
                  fontSize = 28.sp
                )
              }
            }

            Column {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = voice.name,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                  ),
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
              Text(
                text = voice.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }

          IconButton(
            onClick = onPreviewVoice,
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surface)
              .testTag("preview_selected_voice_button")
          ) {
            Icon(
              imageVector = Icons.Default.VolumeUp,
              contentDescription = "Preview voice sample",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(22.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Metadata badges: Accent, Gender, Age, Category
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Accent Badge
          VoiceTagChip(
            label = "${voice.accent.flag} ${voice.accent.displayName}",
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
          )

          // Gender Badge
          VoiceTagChip(
            label = voice.gender.displayName,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
          )

          // Age Badge
          VoiceTagChip(
            label = voice.ageRange.displayName,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Switch voice CTA bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = voice.description,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            maxLines = 1
          )

          Spacer(modifier = Modifier.width(8.dp))

          FilledTonalButton(
            onClick = onOpenVoicePicker,
            shape = RoundedCornerShape(12.dp),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            modifier = Modifier
              .height(34.dp)
              .testTag("switch_voice_button")
          ) {
            Icon(
              imageVector = Icons.Default.RecordVoiceOver,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Voices",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Icon(
              imageVector = Icons.Default.ChevronRight,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun VoiceTagChip(
  label: String,
  containerColor: Color,
  contentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = containerColor,
    modifier = modifier
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
      ),
      color = contentColor,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
  }
}
