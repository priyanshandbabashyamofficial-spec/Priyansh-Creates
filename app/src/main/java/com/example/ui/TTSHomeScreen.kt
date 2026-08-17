package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VoiceCatalog
import com.example.ui.components.ExportAudioDialog
import com.example.ui.components.SavedSpeechesSheet
import com.example.ui.components.SelectedVoiceHeroCard
import com.example.ui.components.SoundwaveVisualizer
import com.example.ui.components.SpeechControlPanel
import com.example.ui.components.VoiceSelectionSheet
import com.example.viewmodel.TTSViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TTSHomeScreen(
  viewModel: TTSViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  // State collectors
  val inputText by viewModel.inputText.collectAsState()
  val selectedVoice by viewModel.selectedVoice.collectAsState()
  val pitch by viewModel.pitch.collectAsState()
  val speed by viewModel.speed.collectAsState()
  val activePresetId by viewModel.activePresetId.collectAsState()
  val filteredVoices by viewModel.filteredVoices.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val genderFilter by viewModel.selectedGenderFilter.collectAsState()
  val accentFilter by viewModel.selectedAccentFilter.collectAsState()
  val ageFilter by viewModel.selectedAgeFilter.collectAsState()
  val tabIndex by viewModel.voicePickerTab.collectAsState()
  val savedSpeeches by viewModel.savedSpeeches.collectAsState()
  val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()
  val isExporting by viewModel.isExporting.collectAsState()
  val exportSuccessFile by viewModel.exportSuccessFile.collectAsState()
  val snackbarMsg by viewModel.snackbarMessage.collectAsState()
  val statusMsg by viewModel.ttsManager.statusMessage.collectAsState()

  // Sheet / Dialog states
  var showVoicePicker by remember { mutableStateOf(false) }
  var showSavedSpeeches by remember { mutableStateOf(false) }
  var showInfoDialog by remember { mutableStateOf(false) }

  // Handle snackbar messages
  LaunchedEffect(snackbarMsg) {
    snackbarMsg?.let {
      snackbarHostState.showSnackbar(it)
      viewModel.clearSnackbarMessage()
    }
  }

  // Calculate text stats
  val wordCount = remember(inputText) {
    inputText.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
  }
  val charCount = inputText.length
  val estimatedSeconds = remember(wordCount, speed) {
    if (wordCount == 0) 0 else ((wordCount / (140f * speed)) * 60).toInt()
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(32.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text("⚡", fontSize = 18.sp)
              }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "TTS God",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Voice Synthesizer Studio",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        },
        actions = {
          // Saved speeches history button
          IconButton(
            onClick = { showSavedSpeeches = true },
            modifier = Modifier.testTag("saved_speeches_button")
          ) {
            BadgedBox(
              badge = {
                if (savedSpeeches.isNotEmpty()) {
                  Badge { Text("${savedSpeeches.size}") }
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Saved Speeches History",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Info / TTS Settings button
          IconButton(
            onClick = { showInfoDialog = true },
            modifier = Modifier.testTag("tts_info_button")
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = "TTS Information",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      // Primary Action Bottom Dock
      Surface(
        tonalElevation = 8.dp,
        shadowElevation = 12.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Save Speech Button
            FilledTonalButton(
              onClick = { viewModel.saveCurrentSpeech() },
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .height(54.dp)
                .testTag("save_speech_button")
            ) {
              Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = "Save speech",
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Save", fontWeight = FontWeight.SemiBold)
            }

            // Export Audio File Button
            FilledTonalButton(
              onClick = { viewModel.exportAudioFile() },
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .height(54.dp)
                .testTag("export_audio_button")
            ) {
              Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Export audio",
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Export", fontWeight = FontWeight.SemiBold)
            }

            // Main Speak / Stop Button
            val playButtonColor by animateColorAsState(
              targetValue = if (isSpeaking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
              animationSpec = tween(300),
              label = "playBtnColor"
            )

            Button(
              onClick = {
                if (isSpeaking) {
                  viewModel.stopPlayback()
                } else {
                  viewModel.speakCurrentText()
                }
              },
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(containerColor = playButtonColor),
              modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .testTag("main_speak_button")
            ) {
              Icon(
                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                contentDescription = if (isSpeaking) "Stop Speech" else "Speak Text",
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (isSpeaking) "STOP" else "SPEAK NOW",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.5.sp
                )
              )
            }
          }
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
    ) {
      // Soundwave visualizer bar
      item {
        SoundwaveVisualizer(
          isSpeaking = isSpeaking,
          voiceName = selectedVoice.name
        )
      }

      // Selected Voice Hero Card
      item {
        SelectedVoiceHeroCard(
          voice = selectedVoice,
          onOpenVoicePicker = { showVoicePicker = true },
          onPreviewVoice = { viewModel.previewVoice(selectedVoice) }
        )
      }

      // Text Input Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("text_input_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
          ),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp)
          ) {
            // Text Header & Actions (Clear, Paste)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Enter Text to Convert",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )

              Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Paste from clipboard
                IconButton(
                  onClick = {
                    try {
                      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      val clip = clipboard.primaryClip
                      if (clip != null && clip.itemCount > 0) {
                        val pasted = clip.getItemAt(0).text.toString()
                        if (pasted.isNotBlank()) {
                          viewModel.onTextChanged(pasted)
                        }
                      }
                    } catch (e: Exception) {
                      e.printStackTrace()
                    }
                  },
                  modifier = Modifier
                    .size(32.dp)
                    .testTag("paste_text_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = "Paste text",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                }

                // Clear text
                IconButton(
                  onClick = { viewModel.clearText() },
                  modifier = Modifier
                    .size(32.dp)
                    .testTag("clear_text_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear text",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Multi-line Text Field
            OutlinedTextField(
              value = inputText,
              onValueChange = { viewModel.onTextChanged(it) },
              placeholder = { Text("Type or paste any text, article, script, or phrase here...") },
              modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .testTag("tts_input_field"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
              )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Counters: Words, Characters, Estimated Speech Duration
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "$wordCount words • $charCount characters",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
              ) {
                Text(
                  text = "Est. duration: ~$estimatedSeconds sec",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                  ),
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Sample Phrases
            Text(
              text = "Quick Sample Texts:",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              items(VoiceCatalog.sampleTexts.withIndex().toList()) { (idx, sample) ->
                val sampleLabel = when (idx) {
                  0 -> "✨ Intro"
                  1 -> "🌌 Cosmos"
                  2 -> "📰 News"
                  3 -> "🏰 Story"
                  4 -> "👅 Twister"
                  else -> "💡 Quote"
                }
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = MaterialTheme.colorScheme.surface,
                  border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                  modifier = Modifier
                    .clickable { viewModel.loadSampleText(sample) }
                    .testTag("sample_text_$idx")
                ) {
                  Text(
                    text = sampleLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                  )
                }
              }
            }
          }
        }
      }

      // Voice Controls & Modulation Presets (Pitch, Speed)
      item {
        SpeechControlPanel(
          pitch = pitch,
          onPitchChanged = { viewModel.onPitchChanged(it) },
          speed = speed,
          onSpeedChanged = { viewModel.onSpeedChanged(it) },
          activePresetId = activePresetId,
          onPresetSelected = { viewModel.applyPreset(it) },
          onResetModulation = { viewModel.resetModulation() }
        )
      }
    }
  }

  // Voice Selection Modal Sheet
  VoiceSelectionSheet(
    isOpen = showVoicePicker,
    onDismiss = { showVoicePicker = false },
    voices = filteredVoices,
    selectedVoice = selectedVoice,
    onSelectVoice = { viewModel.onVoiceSelected(it) },
    onPreviewVoice = { viewModel.previewVoice(it) },
    searchQuery = searchQuery,
    onSearchQueryChanged = { viewModel.setSearchQuery(it) },
    genderFilter = genderFilter,
    onGenderFilterChanged = { viewModel.setGenderFilter(it) },
    accentFilter = accentFilter,
    onAccentFilterChanged = { viewModel.setAccentFilter(it) },
    ageFilter = ageFilter,
    onAgeFilterChanged = { viewModel.setAgeFilter(it) },
    tabIndex = tabIndex,
    onTabChanged = { viewModel.setVoicePickerTab(it) },
    onClearFilters = { viewModel.clearFilters() }
  )

  // Saved Speeches History Sheet
  SavedSpeechesSheet(
    isOpen = showSavedSpeeches,
    onDismiss = { showSavedSpeeches = false },
    savedSpeeches = savedSpeeches,
    onLoadSpeech = { viewModel.loadSavedSpeech(it) },
    onDeleteSpeech = { viewModel.deleteSavedSpeech(it) }
  )

  // Export Audio Dialog
  ExportAudioDialog(
    isExporting = isExporting,
    exportedFile = exportSuccessFile,
    onDismiss = { viewModel.clearExportedFile() },
    onShare = { file ->
      viewModel.shareAudioFile(context, file)
      viewModel.clearExportedFile()
    }
  )

  // Engine Information Dialog
  if (showInfoDialog) {
    AlertDialog(
      onDismissRequest = { showInfoDialog = false },
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
          )
          Text("About TTS God")
        }
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "TTS God gives you versatile text-to-speech synthesis with a diverse palette of voices across multiple genders, accents (US, UK, Australia, India, Canada, Ireland, Europe, Asia), and age ranges.",
            style = MaterialTheme.typography.bodyMedium
          )
          Spacer(modifier = Modifier.height(4.dp))
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "✨ Pro Voice Tips:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "• Try the 'TTS God' preset for deep thunderous voiceover.\n• Use 'Wise Elder' for peaceful storytelling.\n• Tap the speaker icon beside any voice to preview its sound before selecting.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            try {
              val intent = Intent("com.android.settings.TTS_SETTINGS")
              context.startActivity(intent)
            } catch (e: Exception) {
              try {
                val intent = Intent(Settings.ACTION_SETTINGS)
                context.startActivity(intent)
              } catch (e2: Exception) {
                // Ignore
              }
            }
            showInfoDialog = false
          },
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("TTS Engine Settings")
        }
      },
      dismissButton = {
        TextButton(onClick = { showInfoDialog = false }) {
          Text("Close")
        }
      },
      shape = RoundedCornerShape(20.dp),
      modifier = Modifier.testTag("info_dialog")
    )
  }
}
