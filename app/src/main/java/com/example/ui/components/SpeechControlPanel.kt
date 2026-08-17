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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SpeechPreset
import com.example.model.VoiceCatalog

@Composable
fun SpeechControlPanel(
  pitch: Float,
  onPitchChanged: (Float) -> Unit,
  speed: Float,
  onSpeedChanged: (Float) -> Unit,
  activePresetId: String?,
  onPresetSelected: (SpeechPreset) -> Unit,
  onResetModulation: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("speech_control_panel"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header with Title & Reset
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = "Voice Tone & Speed Presets",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        IconButton(
          onClick = onResetModulation,
          modifier = Modifier
            .size(32.dp)
            .testTag("reset_controls_button")
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reset pitch and speed to default",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Preset Carousel
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(VoiceCatalog.defaultPresets, key = { it.id }) { preset ->
          val isSelected = preset.id == activePresetId
          PresetChip(
            preset = preset,
            isSelected = isSelected,
            onClick = { onPresetSelected(preset) }
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Pitch Slider
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Pitch Resonance",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${String.format("%.2f", pitch)}x",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
        }

        Slider(
          value = pitch,
          onValueChange = onPitchChanged,
          valueRange = 0.5f..2.0f,
          steps = 29,
          colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("pitch_slider")
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Playback Speed Slider & Controls
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("playback_speed_section")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Playback Speed",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            val speedLabel = when {
              speed <= 0.6f -> "Slow"
              speed <= 0.85f -> "Relaxed"
              speed in 0.95f..1.05f -> "Normal (1.0x)"
              speed <= 1.35f -> "Brisk"
              speed <= 1.75f -> "Fast"
              else -> "Hyper Fast"
            }
            Text(
              text = speedLabel,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.testTag("speed_value_badge")
          ) {
            Text(
              text = "${String.format("%.2f", speed)}x",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Main Speed Slider with -/+ Stepper Controls
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier
              .size(32.dp)
              .clickable(enabled = speed > 0.5f) {
                val newSpeed = (speed - 0.1f).coerceIn(0.5f, 2.5f)
                onSpeedChanged(Math.round(newSpeed * 100f) / 100f)
              }
              .testTag("speed_decrease_button")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "−",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (speed > 0.5f) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
              )
            }
          }

          Slider(
            value = speed,
            onValueChange = { onSpeedChanged(Math.round(it * 100f) / 100f) },
            valueRange = 0.5f..2.5f,
            steps = 39,
            colors = SliderDefaults.colors(
              thumbColor = MaterialTheme.colorScheme.secondary,
              activeTrackColor = MaterialTheme.colorScheme.secondary,
              inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("speed_slider")
          )

          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier
              .size(32.dp)
              .clickable(enabled = speed < 2.5f) {
                val newSpeed = (speed + 0.1f).coerceIn(0.5f, 2.5f)
                onSpeedChanged(Math.round(newSpeed * 100f) / 100f)
              }
              .testTag("speed_increase_button")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "+",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (speed < 2.5f) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
              )
            }
          }
        }

        // Quick Speed Multiplier Preset Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          val quickSpeeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
          quickSpeeds.forEach { speedVal ->
            val isSelected = Math.abs(speed - speedVal) < 0.04f
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
              border = BorderStroke(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
              ),
              modifier = Modifier
                .clickable { onSpeedChanged(speedVal) }
                .testTag("quick_speed_${(speedVal * 100).toInt()}")
            ) {
              Text(
                text = "${speedVal}x",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun PresetChip(
  preset: SpeechPreset,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) {
      MaterialTheme.colorScheme.primary
    } else {
      MaterialTheme.colorScheme.surface
    },
    border = if (isSelected) {
      BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
    } else {
      BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    },
    modifier = modifier
      .clickable(onClick = onClick)
      .testTag("preset_${preset.id}")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
      Text(
        text = preset.emoji,
        fontSize = 14.sp
      )
      Column {
        Text(
          text = preset.name,
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
          ),
          color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
      }
    }
  }
}
