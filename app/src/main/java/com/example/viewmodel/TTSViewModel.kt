package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.SavedSpeech
import com.example.model.SpeechPreset
import com.example.model.VoiceAccent
import com.example.model.VoiceAge
import com.example.model.VoiceCatalog
import com.example.model.VoiceGender
import com.example.model.VoiceProfile
import com.example.service.TTSManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

class TTSViewModel(application: Application) : AndroidViewModel(application) {

  val ttsManager = TTSManager(application.applicationContext)

  // Current Text Input
  private val _inputText = MutableStateFlow(
    "Welcome to TTS God. Elevate your words with crystal-clear voices across diverse genders, accents, and age ranges."
  )
  val inputText: StateFlow<String> = _inputText.asStateFlow()

  // Selected Voice
  private val _selectedVoice = MutableStateFlow<VoiceProfile>(VoiceCatalog.builtInVoices.first())
  val selectedVoice: StateFlow<VoiceProfile> = _selectedVoice.asStateFlow()

  // Speech Modulation
  private val _pitch = MutableStateFlow(1.0f)
  val pitch: StateFlow<Float> = _pitch.asStateFlow()

  private val _speed = MutableStateFlow(1.0f)
  val speed: StateFlow<Float> = _speed.asStateFlow()

  // Active Preset ID
  private val _activePresetId = MutableStateFlow<String?>("preset_god")
  val activePresetId: StateFlow<String?> = _activePresetId.asStateFlow()

  // Voice Picker Filters
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedGenderFilter = MutableStateFlow(VoiceGender.ALL)
  val selectedGenderFilter: StateFlow<VoiceGender> = _selectedGenderFilter.asStateFlow()

  private val _selectedAccentFilter = MutableStateFlow(VoiceAccent.ALL)
  val selectedAccentFilter: StateFlow<VoiceAccent> = _selectedAccentFilter.asStateFlow()

  private val _selectedAgeFilter = MutableStateFlow(VoiceAge.ALL)
  val selectedAgeFilter: StateFlow<VoiceAge> = _selectedAgeFilter.asStateFlow()

  private val _voicePickerTab = MutableStateFlow(0) // 0: Curated Personas, 1: System Installed
  val voicePickerTab: StateFlow<Int> = _voicePickerTab.asStateFlow()

  private data class FilterCriteria(
    val query: String,
    val gender: VoiceGender,
    val accent: VoiceAccent,
    val age: VoiceAge,
    val tab: Int
  )

  private val filterCriteria = combine(
    _searchQuery,
    _selectedGenderFilter,
    _selectedAccentFilter,
    _selectedAgeFilter,
    _voicePickerTab
  ) { query, gender, accent, age, tab ->
    FilterCriteria(query, gender, accent, age, tab)
  }

  // Filtered Voices
  val filteredVoices: StateFlow<List<VoiceProfile>> = combine(
    ttsManager.allVoices,
    filterCriteria
  ) { allVoices, criteria ->
    val baseList = if (criteria.tab == 0) {
      allVoices.filter { !it.isSystemVoice }
    } else {
      allVoices.filter { it.isSystemVoice }
    }

    baseList.filter { voice ->
      val matchesQuery = criteria.query.isBlank() ||
          voice.name.contains(criteria.query, ignoreCase = true) ||
          voice.title.contains(criteria.query, ignoreCase = true) ||
          voice.accent.displayName.contains(criteria.query, ignoreCase = true) ||
          voice.description.contains(criteria.query, ignoreCase = true)

      val matchesGender = criteria.gender == VoiceGender.ALL || voice.gender == criteria.gender
      val matchesAccent = criteria.accent == VoiceAccent.ALL || voice.accent == criteria.accent
      val matchesAge = criteria.age == VoiceAge.ALL || voice.ageRange == criteria.age

      matchesQuery && matchesGender && matchesAccent && matchesAge
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VoiceCatalog.builtInVoices)

  // Saved Speeches
  private val _savedSpeeches = MutableStateFlow<List<SavedSpeech>>(emptyList())
  val savedSpeeches: StateFlow<List<SavedSpeech>> = _savedSpeeches.asStateFlow()

  // Export State
  private val _isExporting = MutableStateFlow(false)
  val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

  private val _exportSuccessFile = MutableStateFlow<File?>(null)
  val exportSuccessFile: StateFlow<File?> = _exportSuccessFile.asStateFlow()

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  init {
    loadSavedSpeeches()
  }

  fun onTextChanged(newText: String) {
    _inputText.value = newText
  }

  fun onVoiceSelected(voice: VoiceProfile) {
    _selectedVoice.value = voice
    // When changing voice, preview or apply its default modulation if preset is not custom
    ttsManager.previewVoice(voice, _pitch.value, _speed.value)
  }

  fun onPitchChanged(newPitch: Float) {
    _pitch.value = (Math.round(newPitch * 20) / 20f).coerceIn(0.5f, 2.0f)
    _activePresetId.value = null
  }

  fun onSpeedChanged(newSpeed: Float) {
    _speed.value = (Math.round(newSpeed * 20) / 20f).coerceIn(0.5f, 2.5f)
    _activePresetId.value = null
  }

  fun applyPreset(preset: SpeechPreset) {
    _pitch.value = preset.pitch
    _speed.value = preset.speed
    _activePresetId.value = preset.id
  }

  fun resetModulation() {
    _pitch.value = 1.0f
    _speed.value = 1.0f
    _activePresetId.value = "preset_natural"
  }

  fun speakCurrentText() {
    val text = _inputText.value.trim()
    if (text.isEmpty()) {
      _snackbarMessage.value = "Please enter some text to speak"
      return
    }
    ttsManager.speak(text, _selectedVoice.value, _pitch.value, _speed.value)
  }

  fun previewVoice(voice: VoiceProfile) {
    ttsManager.previewVoice(voice, _pitch.value, _speed.value)
  }

  fun stopPlayback() {
    ttsManager.stop()
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setGenderFilter(gender: VoiceGender) {
    _selectedGenderFilter.value = gender
  }

  fun setAccentFilter(accent: VoiceAccent) {
    _selectedAccentFilter.value = accent
  }

  fun setAgeFilter(age: VoiceAge) {
    _selectedAgeFilter.value = age
  }

  fun setVoicePickerTab(tab: Int) {
    _voicePickerTab.value = tab
  }

  fun clearFilters() {
    _searchQuery.value = ""
    _selectedGenderFilter.value = VoiceGender.ALL
    _selectedAccentFilter.value = VoiceAccent.ALL
    _selectedAgeFilter.value = VoiceAge.ALL
  }

  fun loadSampleText(sample: String) {
    _inputText.value = sample
  }

  fun clearText() {
    _inputText.value = ""
  }

  fun saveCurrentSpeech() {
    val text = _inputText.value.trim()
    if (text.isEmpty()) {
      _snackbarMessage.value = "Cannot save empty speech"
      return
    }

    val voice = _selectedVoice.value
    val title = text.take(35) + if (text.length > 35) "..." else ""
    val newSpeech = SavedSpeech(
      id = UUID.randomUUID().toString(),
      title = title,
      text = text,
      voiceName = voice.name,
      voiceAvatar = voice.avatarEmoji,
      pitch = _pitch.value,
      speed = _speed.value,
      timestamp = System.currentTimeMillis()
    )

    val updated = listOf(newSpeech) + _savedSpeeches.value
    _savedSpeeches.value = updated
    persistSavedSpeeches(updated)
    _snackbarMessage.value = "Saved to speech library"
  }

  fun deleteSavedSpeech(speech: SavedSpeech) {
    val updated = _savedSpeeches.value.filter { it.id != speech.id }
    _savedSpeeches.value = updated
    persistSavedSpeeches(updated)
    _snackbarMessage.value = "Deleted speech"
  }

  fun loadSavedSpeech(speech: SavedSpeech) {
    _inputText.value = speech.text
    _pitch.value = speech.pitch
    _speed.value = speech.speed
    // Find matching voice if possible
    val match = ttsManager.allVoices.value.find { it.name == speech.voiceName }
    if (match != null) {
      _selectedVoice.value = match
    }
    _snackbarMessage.value = "Loaded: ${speech.title}"
  }

  fun exportAudioFile(customTitle: String? = null) {
    val text = _inputText.value.trim()
    if (text.isEmpty()) {
      _snackbarMessage.value = "Please enter text before exporting"
      return
    }

    _isExporting.value = true
    val fileName = "TTS_God_${System.currentTimeMillis()}"

    ttsManager.synthesizeToFile(
      text = text,
      voiceProfile = _selectedVoice.value,
      pitchMultiplier = _pitch.value,
      speedMultiplier = _speed.value,
      fileName = fileName
    ) { success, file ->
      _isExporting.value = false
      if (success && file != null) {
        _exportSuccessFile.value = file
        _snackbarMessage.value = "Audio export generated successfully"
      } else {
        _snackbarMessage.value = "Failed to export audio file"
      }
    }
  }

  fun clearExportedFile() {
    _exportSuccessFile.value = null
  }

  fun shareAudioFile(context: Context, file: File) {
    try {
      val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )
      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "audio/wav"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "Speech audio created with TTS God")
        putExtra(Intent.EXTRA_TEXT, "Listen to this speech generated with TTS God:\n\n\"${_inputText.value.take(100)}\"")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      context.startActivity(Intent.createChooser(shareIntent, "Share Audio Clip"))
    } catch (e: Exception) {
      _snackbarMessage.value = "Could not share audio: ${e.message}"
    }
  }

  fun clearSnackbarMessage() {
    _snackbarMessage.value = null
  }

  private fun loadSavedSpeeches() {
    try {
      val prefs = getApplication<Application>().getSharedPreferences("tts_god_prefs", Context.MODE_PRIVATE)
      val jsonString = prefs.getString("saved_speeches_json", null) ?: return
      val jsonArray = JSONArray(jsonString)
      val list = mutableListOf<SavedSpeech>()
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          SavedSpeech(
            id = obj.getString("id"),
            title = obj.getString("title"),
            text = obj.getString("text"),
            voiceName = obj.getString("voiceName"),
            voiceAvatar = obj.optString("voiceAvatar", "🎙️"),
            pitch = obj.optDouble("pitch", 1.0).toFloat(),
            speed = obj.optDouble("speed", 1.0).toFloat(),
            timestamp = obj.getLong("timestamp")
          )
        )
      }
      _savedSpeeches.value = list
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun persistSavedSpeeches(list: List<SavedSpeech>) {
    try {
      val prefs = getApplication<Application>().getSharedPreferences("tts_god_prefs", Context.MODE_PRIVATE)
      val jsonArray = JSONArray()
      for (speech in list) {
        val obj = JSONObject().apply {
          put("id", speech.id)
          put("title", speech.title)
          put("text", speech.text)
          put("voiceName", speech.voiceName)
          put("voiceAvatar", speech.voiceAvatar)
          put("pitch", speech.pitch)
          put("speed", speech.speed)
          put("timestamp", speech.timestamp)
        }
        jsonArray.put(obj)
      }
      prefs.edit().putString("saved_speeches_json", jsonArray.toString()).apply()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  override fun onCleared() {
    super.onCleared()
    ttsManager.shutdown()
  }
}
