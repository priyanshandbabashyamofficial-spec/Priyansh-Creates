package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SoundwaveVisualizer(
  isSpeaking: Boolean,
  voiceName: String,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "soundwave")

  val anim1 by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(420, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bar1"
  )

  val anim2 by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(310, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bar2"
  )

  val anim3 by infiniteTransition.animateFloat(
    initialValue = 0.15f,
    targetValue = 0.85f,
    animationSpec = infiniteRepeatable(
      animation = tween(530, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bar3"
  )

  val anim4 by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 0.98f,
    animationSpec = infiniteRepeatable(
      animation = tween(360, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bar4"
  )

  val anim5 by infiniteTransition.animateFloat(
    initialValue = 0.25f,
    targetValue = 0.75f,
    animationSpec = infiniteRepeatable(
      animation = tween(470, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bar5"
  )

  val barHeights = if (isSpeaking) {
    listOf(anim1, anim2, anim3, anim4, anim5, anim2, anim1, anim4, anim3, anim5, anim2, anim1)
  } else {
    List(12) { 0.12f }
  }

  val primaryColor = MaterialTheme.colorScheme.primary
  val tertiaryColor = MaterialTheme.colorScheme.tertiary
  val gradientBrush = Brush.horizontalGradient(
    colors = if (isSpeaking) {
      listOf(primaryColor, tertiaryColor, primaryColor)
    } else {
      listOf(
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
      )
    }
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(
        if (isSpeaking) {
          MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
        } else {
          MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        }
      )
      .padding(horizontal = 16.dp, vertical = 10.dp)
      .testTag("soundwave_visualizer"),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .width(8.dp)
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSpeaking) Color(0xFF10B981) else MaterialTheme.colorScheme.outline)
        )
        Text(
          text = if (isSpeaking) "Speaking with $voiceName..." else "Engine Ready",
          style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = if (isSpeaking) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 12.sp
          ),
          color = if (isSpeaking) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(28.dp)
      ) {
        barHeights.forEach { heightRatio ->
          Box(
            modifier = Modifier
              .width(3.5.dp)
              .height((24 * heightRatio).coerceAtLeast(3f).dp)
              .clip(RoundedCornerShape(2.dp))
              .background(gradientBrush)
          )
        }
      }
    }
  }
}
