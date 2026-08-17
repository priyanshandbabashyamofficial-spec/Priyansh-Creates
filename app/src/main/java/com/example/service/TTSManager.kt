package com.example.service

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.example.model.VoiceAccent
import com.example.model.VoiceAge
import com.example.model.VoiceCatalog
import com.example.model.VoiceGender
import com.example.model.VoiceProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.Locale
import java.util.UUID

class TTSManager(private val context: Context) : TextToSpeech.OnInitListener {

  private var tts: TextToSpeech? = null

  private val _isInitialized = MutableStateFlow(false)
  val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

  private val _isSpeaking = MutableStateFlow(false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  private val _highlightedRange = MutableStateFlow<Pair<Int, Int>?>(null)
  val highlightedRange: StateFlow<Pair<Int, Int>?> = _highlightedRange.asStateFlow()

  private val _systemVoices = MutableStateFlow<List<VoiceProfile>>(emptyList())
  val systemVoices: StateFlow<List<VoiceProfile>> = _systemVoices.asStateFlow()

  private val _allVoices = MutableStateFlow<List<VoiceProfile>>(VoiceCatalog.builtInVoices)
  val allVoices: StateFlow<List<VoiceProfile>> = _allVoices.asStateFlow()

  private val _statusMessage = MutableStateFlow<String?>(null)
  val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

  private val mainHandler = Handler(Looper.getMainLooper())

  init {
    initTTS()
  }

  private fun initTTS() {
    try {
      tts = TextToSpeech(context.applicationContext, this)
    } catch (e: Exception) {
      Log.e("TTSManager", "Failed to construct TextToSpeech", e)
      _statusMessage.value = "Failed to initialize TTS Engine: ${e.message}"
    }
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      _isInitialized.value = true
      _statusMessage.value = null
      setupUtteranceListener()
      discoverSystemVoices()
    } else {
      _isInitialized.value = false
      _statusMessage.value = "TTS Engine initialization failed (Code $status). Please check TTS settings."
    }
  }

  private fun setupUtteranceListener() {
    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
      override fun onStart(utteranceId: String?) {
        mainHandler.post {
          _isSpeaking.value = true
        }
      }

      override fun onDone(utteranceId: String?) {
        mainHandler.post {
          _isSpeaking.value = false
          _highlightedRange.value = null
        }
      }

      override fun onError(utteranceId: String?) {
        mainHandler.post {
          _isSpeaking.value = false
          _highlightedRange.value = null
          _statusMessage.value = "Error during speech playback"
        }
      }

      override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
        mainHandler.post {
          _highlightedRange.value = Pair(start, end)
        }
      }
    })
  }

  private fun discoverSystemVoices() {
    try {
      val ttsEngine = tts ?: return
      val installedVoices = ttsEngine.voices ?: emptySet()
      val systemProfileList = mutableListOf<VoiceProfile>()

      for (voice in installedVoices) {
        val locale = voice.locale ?: Locale.US
        val name = voice.name
        val isLatency = voice.latency == Voice.LATENCY_VERY_LOW || voice.latency == Voice.LATENCY_LOW
        val qualityDesc = if (voice.quality == Voice.QUALITY_VERY_HIGH || voice.quality == Voice.QUALITY_HIGH) "HQ" else "Standard"

        val gender = when {
          name.contains("female", ignoreCase = true) || name.contains("fem", ignoreCase = true) || name.contains("f0", ignoreCase = true) -> VoiceGender.FEMALE
          name.contains("male", ignoreCase = true) || name.contains("masc", ignoreCase = true) || name.contains("m0", ignoreCase = true) -> VoiceGender.MALE
          else -> VoiceGender.NEUTRAL
        }

        val accent = when {
          locale.country.equals("GB", ignoreCase = true) || locale.language == "en" && locale.country == "GB" -> VoiceAccent.UK_ENGLISH
          locale.country.equals("AU", ignoreCase = true) -> VoiceAccent.AUSTRALIAN
          locale.country.equals("IN", ignoreCase = true) -> VoiceAccent.INDIAN_ENGLISH
          locale.country.equals("CA", ignoreCase = true) -> VoiceAccent.CANADIAN
          locale.country.equals("IE", ignoreCase = true) -> VoiceAccent.IRISH
          locale.language.equals("fr", ignoreCase = true) -> VoiceAccent.FRENCH
          locale.language.equals("de", ignoreCase = true) -> VoiceAccent.GERMAN
          locale.language.equals("es", ignoreCase = true) -> VoiceAccent.SPANISH
          locale.language.equals("ja", ignoreCase = true) -> VoiceAccent.JAPANESE
          locale.language.equals("it", ignoreCase = true) -> VoiceAccent.ITALIAN
          locale.language.equals("pt", ignoreCase = true) -> VoiceAccent.BRAZILIAN_PORTUGUESE
          else -> VoiceAccent.US_ENGLISH
        }

        val age = if (name.contains("child", ignoreCase = true)) {
          VoiceAge.CHILD
        } else if (name.contains("mature", ignoreCase = true) || name.contains("old", ignoreCase = true)) {
          VoiceAge.ELDERLY
        } else {
          VoiceAge.ADULT
        }

        val readableName = formatVoiceName(name, locale)

        val profile = VoiceProfile(
          id = "sys_${voice.name}",
          name = readableName,
          title = "System Voice ($qualityDesc)",
          gender = gender,
          ageRange = age,
          accent = accent,
          locale = locale,
          defaultPitch = 1.0f,
          defaultSpeed = 1.0f,
          description = "Engine voice (${locale.displayLanguage} - ${locale.displayCountry})",
          avatarEmoji = if (gender == VoiceGender.FEMALE) "👩" else if (gender == VoiceGender.MALE) "👨" else "🤖",
          previewText = "Sample voice test for $readableName.",
          isSystemVoice = true,
          systemVoiceName = voice.name
        )
        systemProfileList.add(profile)
      }

      _systemVoices.value = systemProfileList
      // Combine curated presets with system voices
      _allVoices.value = VoiceCatalog.builtInVoices + systemProfileList
    } catch (e: Exception) {
      Log.w("TTSManager", "Could not discover system voices: ${e.message}")
    }
  }

  private fun formatVoiceName(rawName: String, locale: Locale): String {
    val clean = rawName.substringAfterLast("#").substringAfterLast("/").replace("_", " ").replace("-", " ")
    return if (clean.length > 25) {
      "${locale.displayLanguage} (${locale.country}) ${clean.takeLast(10)}"
    } else {
      "${locale.displayLanguage}: $clean"
    }
  }

  fun speak(
    text: String,
    voiceProfile: VoiceProfile,
    pitchMultiplier: Float = 1.0f,
    speedMultiplier: Float = 1.0f
  ) {
    if (text.isBlank()) return
    val ttsEngine = tts ?: return

    try {
      applyVoiceSettings(ttsEngine, voiceProfile, pitchMultiplier, speedMultiplier)
      val utteranceId = UUID.randomUUID().toString()
      val params = Bundle()
      params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
      ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
      _isSpeaking.value = true
    } catch (e: Exception) {
      Log.e("TTSManager", "Error speaking text", e)
      _statusMessage.value = "Failed to speak: ${e.message}"
      _isSpeaking.value = false
    }
  }

  fun previewVoice(
    voiceProfile: VoiceProfile,
    pitchMultiplier: Float = 1.0f,
    speedMultiplier: Float = 1.0f
  ) {
    speak(
      text = voiceProfile.previewText,
      voiceProfile = voiceProfile,
      pitchMultiplier = pitchMultiplier,
      speedMultiplier = speedMultiplier
    )
  }

  private fun applyVoiceSettings(
    ttsEngine: TextToSpeech,
    voiceProfile: VoiceProfile,
    pitchMultiplier: Float,
    speedMultiplier: Float
  ) {
    // Set language/locale
    val result = ttsEngine.setLanguage(voiceProfile.locale)
    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
      ttsEngine.setLanguage(Locale.US)
    }

    // Set voice object if matching
    if (voiceProfile.systemVoiceName != null) {
      val match = ttsEngine.voices?.find { it.name == voiceProfile.systemVoiceName }
      if (match != null) {
        ttsEngine.voice = match
      }
    } else {
      // Find a matching voice in engine matching gender and locale if possible
      val matchingVoice = ttsEngine.voices?.find { v ->
        v.locale.language == voiceProfile.locale.language &&
            (voiceProfile.gender == VoiceGender.ALL ||
                (voiceProfile.gender == VoiceGender.FEMALE && v.name.contains("female", ignoreCase = true)) ||
                (voiceProfile.gender == VoiceGender.MALE && v.name.contains("male", ignoreCase = true)))
      }
      if (matchingVoice != null) {
        ttsEngine.voice = matchingVoice
      }
    }

    val calculatedPitch = (voiceProfile.defaultPitch * pitchMultiplier).coerceIn(0.4f, 2.0f)
    val calculatedSpeed = (voiceProfile.defaultSpeed * speedMultiplier).coerceIn(0.4f, 2.5f)

    ttsEngine.setPitch(calculatedPitch)
    ttsEngine.setSpeechRate(calculatedSpeed)
  }

  fun stop() {
    try {
      tts?.stop()
      _isSpeaking.value = false
      _highlightedRange.value = null
    } catch (e: Exception) {
      Log.e("TTSManager", "Error stopping TTS", e)
    }
  }

  fun synthesizeToFile(
    text: String,
    voiceProfile: VoiceProfile,
    pitchMultiplier: Float,
    speedMultiplier: Float,
    fileName: String,
    onComplete: (Boolean, File?) -> Unit
  ) {
    if (text.isBlank()) {
      onComplete(false, null)
      return
    }

    val ttsEngine = tts
    if (ttsEngine == null) {
      onComplete(false, null)
      return
    }

    try {
      val outputDir = File(context.cacheDir, "tts_exports")
      if (!outputDir.exists()) outputDir.mkdirs()

      val cleanFileName = if (fileName.endsWith(".wav")) fileName else "$fileName.wav"
      val outputFile = File(outputDir, cleanFileName)

      applyVoiceSettings(ttsEngine, voiceProfile, pitchMultiplier, speedMultiplier)

      val utteranceId = "export_${UUID.randomUUID()}"
      val params = Bundle()
      params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)

      // Add a single-shot listener for export
      val currentListener = object : UtteranceProgressListener() {
        override fun onStart(id: String?) {}

        override fun onDone(id: String?) {
          if (id == utteranceId) {
            mainHandler.post {
              setupUtteranceListener()
              onComplete(true, outputFile)
            }
          }
        }

        override fun onError(id: String?) {
          if (id == utteranceId) {
            mainHandler.post {
              setupUtteranceListener()
              onComplete(false, null)
            }
          }
        }
      }

      ttsEngine.setOnUtteranceProgressListener(currentListener)
      val res = ttsEngine.synthesizeToFile(text, params, outputFile, utteranceId)
      if (res != TextToSpeech.SUCCESS) {
        setupUtteranceListener()
        onComplete(false, null)
      }
    } catch (e: Exception) {
      Log.e("TTSManager", "Failed to synthesize to file", e)
      setupUtteranceListener()
      onComplete(false, null)
    }
  }

  fun clearStatusMessage() {
    _statusMessage.value = null
  }

  fun shutdown() {
    try {
      tts?.stop()
      tts?.shutdown()
      tts = null
    } catch (e: Exception) {
      Log.e("TTSManager", "Error shutting down TTS", e)
    }
  }
}
