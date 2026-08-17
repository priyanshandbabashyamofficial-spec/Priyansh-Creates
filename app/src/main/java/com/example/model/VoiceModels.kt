package com.example.model

import java.util.Locale

enum class VoiceGender(val displayName: String) {
  ALL("All Genders"),
  MALE("Male"),
  FEMALE("Female"),
  NEUTRAL("Neutral / Synth")
}

enum class VoiceAge(val displayName: String) {
  ALL("All Ages"),
  CHILD("Child (7-12)"),
  YOUNG_ADULT("Young Adult (18-28)"),
  ADULT("Mature Adult (30-50)"),
  ELDERLY("Senior / Sage (60+)"),
  MYTHICAL("Mythical / Deity")
}

enum class VoiceAccent(val displayName: String, val flag: String, val locale: Locale) {
  ALL("All Accents", "🌐", Locale.US),
  US_ENGLISH("US English", "🇺🇸", Locale.US),
  UK_ENGLISH("British English", "🇬🇧", Locale.UK),
  AUSTRALIAN("Australian", "🇦🇺", Locale("en", "AU")),
  INDIAN_ENGLISH("Indian English", "🇮🇳", Locale("en", "IN")),
  CANADIAN("Canadian English", "🇨🇦", Locale.CANADA),
  IRISH("Irish English", "🇮🇪", Locale("en", "IE")),
  SCOTTISH("Scottish English", "🏴󠁧󠁢󠁳󠁣󠁴󠁿", Locale("en", "GB")),
  FRENCH("French", "🇫🇷", Locale.FRENCH),
  GERMAN("German", "🇩🇪", Locale.GERMAN),
  SPANISH("Spanish", "🇪🇸", Locale("es", "ES")),
  JAPANESE("Japanese", "🇯🇵", Locale.JAPANESE),
  ITALIAN("Italian", "🇮🇹", Locale.ITALIAN),
  BRAZILIAN_PORTUGUESE("Portuguese (BR)", "🇧🇷", Locale("pt", "BR"))
}

data class VoiceProfile(
  val id: String,
  val name: String,
  val title: String,
  val gender: VoiceGender,
  val ageRange: VoiceAge,
  val accent: VoiceAccent,
  val locale: Locale,
  val defaultPitch: Float = 1.0f,
  val defaultSpeed: Float = 1.0f,
  val description: String,
  val avatarEmoji: String,
  val previewText: String = "Hello! I am your selected voice on TTS God.",
  val isSystemVoice: Boolean = false,
  val systemVoiceName: String? = null
)

data class SpeechPreset(
  val id: String,
  val name: String,
  val emoji: String,
  val description: String,
  val pitch: Float,
  val speed: Float
)

data class SavedSpeech(
  val id: String,
  val title: String,
  val text: String,
  val voiceName: String,
  val voiceAvatar: String,
  val pitch: Float,
  val speed: Float,
  val timestamp: Long,
  val audioPath: String? = null
)

data class TtsEngineInfo(
  val engineName: String,
  val label: String,
  val isDefault: Boolean,
  val availableVoicesCount: Int
)

object VoiceCatalog {
  val defaultPresets = listOf(
    SpeechPreset(
      id = "preset_god",
      name = "TTS God",
      emoji = "⚡",
      description = "Deep, thunderous, resonant godly resonance",
      pitch = 0.65f,
      speed = 0.88f
    ),
    SpeechPreset(
      id = "preset_natural",
      name = "Natural",
      emoji = "✨",
      description = "Balanced conversational pacing",
      pitch = 1.0f,
      speed = 1.0f
    ),
    SpeechPreset(
      id = "preset_podcast",
      name = "Podcast Host",
      emoji = "🎙️",
      description = "Warm broadcast tone with crisp delivery",
      pitch = 0.92f,
      speed = 1.05f
    ),
    SpeechPreset(
      id = "preset_storyteller",
      name = "Storyteller",
      emoji = "📖",
      description = "Expressive, engaging narrative cadence",
      pitch = 0.88f,
      speed = 0.92f
    ),
    SpeechPreset(
      id = "preset_kid",
      name = "Energetic Kid",
      emoji = "🎈",
      description = "High-pitched playful animation tone",
      pitch = 1.45f,
      speed = 1.15f
    ),
    SpeechPreset(
      id = "preset_sage",
      name = "Wise Elder",
      emoji = "📜",
      description = "Slow, calm and meditative tone",
      pitch = 0.75f,
      speed = 0.80f
    ),
    SpeechPreset(
      id = "preset_robot",
      name = "Cyber Droid",
      emoji = "🤖",
      description = "Synthesized metallic cadence",
      pitch = 1.25f,
      speed = 1.30f
    ),
    SpeechPreset(
      id = "preset_speedy",
      name = "Speed Reader",
      emoji = "⚡",
      description = "Rapid 1.75x speed listening",
      pitch = 1.05f,
      speed = 1.75f
    )
  )

  val sampleTexts = listOf(
    "Welcome to TTS God. Transform any written text into realistic human speech with supreme clarity and nuance.",
    "The cosmos is within us. We are made of star-stuff. We are a way for the cosmos to know itself.",
    "Breaking news: Scientists have discovered a revolutionary method to generate natural speech with zero latency.",
    "Once upon a time in a tranquil ancient valley, the guardian of echoes awakened the dormant mountain spirits.",
    "Peter Piper picked a peck of pickled peppers. A peck of pickled peppers Peter Piper picked!",
    "Success is not final, failure is not fatal: it is the courage to continue that counts."
  )

  val builtInVoices: List<VoiceProfile> = listOf(
    // Mythical & Signature
    VoiceProfile(
      id = "god_zeus",
      name = "Zeus - Thunder Deity",
      title = "Supreme Resonant Voice",
      gender = VoiceGender.MALE,
      ageRange = VoiceAge.MYTHICAL,
      accent = VoiceAccent.US_ENGLISH,
      locale = Locale.US,
      defaultPitch = 0.62f,
      defaultSpeed = 0.88f,
      description = "Commanding, ultra-deep, majestic cinematic tone",
      avatarEmoji = "⚡",
      previewText = "Mortals and gods alike, listen to the supreme voice of Olympus!"
    ),
    VoiceProfile(
      id = "god_athena",
      name = "Athena - Wisdom Goddess",
      title = "Ethereal & Strategic",
      gender = VoiceGender.FEMALE,
      ageRange = VoiceAge.MYTHICAL,
      accent = VoiceAccent.UK_ENGLISH,
      locale = Locale.UK,
      defaultPitch = 1.15f,
      defaultSpeed = 0.95f,
      description = "Graceful, authoritative, crystal clear diction",
      avatarEmoji = "🦉",
      previewText = "Wisdom illuminates the mind. Speak your truth with unwavering clarity."
    ),

    // US English
    VoiceProfile(
      id = "us_emma",
      name = "Emma (US)",
      title = "Warm & Conversational",
      gender = VoiceGender.FEMALE,
      ageRange = VoiceAge.YOUNG_ADULT,
      accent = VoiceAccent.US_ENGLISH,
      locale = Locale.US,
      defaultPitch = 1.1f,
      defaultSpeed = 1.0f,
      description = "Modern, friendly American voice perfect for podcasts and audiobooks",
      avatarEmoji = "👩‍💼",
      previewText = "Hi there! I am Emma. I can read your articles, books, or notes smoothly."
    ),
    VoiceProfile(
      id = "us_james",
      name = "James (US)",
      title = "News & Professional",
      gender = VoiceGender.MALE,
      ageRange = VoiceAge.ADULT,
      accent = VoiceAccent.US_ENGLISH,
      locale = Locale.US,
      defaultPitch = 0.95f,
      defaultSpeed = 1.0f,
      description = "Confident broadcast anchor tone with crisp enunciation",
      avatarEmoji = "👨‍💼",
      previewText = "Good evening. James reporting with crisp, professional American pronunciation."
    ),
    VoiceProfile(
      id = "us_leo_child",
      name = "Leo (US Junior)",
      title = "Playful & Cheerful Child",
      gender = VoiceGender.MALE,
      ageRange = VoiceAge.CHILD,
      accent = VoiceAccent.US_ENGLISH,
      locale = Locale.US,
      defaultPitch = 1.45f,
      defaultSpeed = 1.08f,
      description = "Bright, youthful child voice for cartoons and children's stories",
      avatarEmoji = "👦",
      previewText = "Hey! Let's read fun stories and adventure games together!"
    ),
    VoiceProfile(
      id = "us_maya_teen",
      name = "Maya (US Youth)",
      title = "Vibrant & Expressive",
      gender = VoiceGender.FEMALE,
      ageRange = VoiceAge.YOUNG_ADULT,
      accent = VoiceAccent.US_ENGLISH,
      locale = Locale.US,
      defaultPitch = 1.25f,
      defaultSpeed = 1.1f,
      description = "Upbeat, casual everyday youth voice",
      avatarEmoji = "👱‍♀️",
      previewText = "What's up! Ready to convert your favorite texts in seconds?"
    ),
    VoiceProfile(
      id = "us_morgan_senior",
      name = "Morgan (US Elder)",
      title = "Sage & Storyteller",
      gender = VoiceGender.MALE,
      ageRange = VoiceAge.ELDERLY,
      accent = VoiceAccent.US_ENGLISH,
      locale = Locale.US,
      defaultPitch = 0.78f,
      defaultSpeed = 0.85f,
      description = "Deep, warm, grandfatherly narrative resonance",
      avatarEmoji = "👴",
      previewText = "Gather close, for every tale holds a timeless lesson of life."
    ),

    // UK British English
    VoiceProfile(
      id = "uk_oliver",
      name = "Oliver (British)",
      title = "Refined Gentleman",
      gender = VoiceGender.MALE,
      ageRange = VoiceAge.ADULT,
      accent = VoiceAccent.UK_ENGLISH,
      locale = Locale.UK,
      defaultPitch = 0.92f,
      defaultSpeed = 0.98f,
      description = "Classic British BBC accent, intellectual and articulate",
      avatarEmoji = "🤵",
      previewText = "Cheerio! Allow me to narrate your manuscript with impeccable British cadence."
    ),
    VoiceProfile(
      id = "uk_charlotte",
      name = "Charlotte (British)",
      title = "Elegant Narrator",
      gender = VoiceGender.FEMALE,
      ageRange = VoiceAge.YOUNG_ADULT,
      accent = VoiceAccent.UK_ENGLISH,
      locale = Locale.UK,
      defaultPitch = 1.12f,
      defaultSpeed = 1.0f,
      description = "Polished, gentle RP British voice with soothing rhythm",
      avatarEmoji = "👒",
      previewText = "Hello darling, I am Charlotte. Ready to bring your prose to life."
    ),
    VoiceProfile(
      id = "uk_eleanor_senior",
      name = "Lady Eleanor (UK)",
      title = "Distinguished Elder",
      gender = VoiceGender.FEMALE,
      ageRange = VoiceAge.ELDERLY,
      accent = VoiceAccent.UK_ENGLISH,
      locale = Locale.UK,
      defaultPitch = 0.85f,
      defaultSpeed = 0.88f,
      description = "Aristocratic, calm, mature British lady",
      avatarEmoji = "👵",
      previewText = "Patience and elegance are the cornerstones of proper literature."
    ),

    // Australian English
    VoiceProfile(
      id = "au_liam",
      name = "Liam (Australian)",
      title = "Laidback & Energetic",
      gender = VoiceGender.MALE,
      ageRange = VoiceAge.YOUNG_ADULT,
      accent = VoiceAccent.AUSTRALIAN,
      locale = Locale("en", "AU"),
      defaultPitch = 0.98f,
      defaultSpeed = 1.02f,
      description = "Friendly Aussie mate tone with authentic cadence",
      avatarEmoji = "🏄‍♂️",
      previewText = "G'day mate! Liam here, ready to read out whatever you throw at me!"
    ),
    VoiceProfile(
      id = "au_ruby",
      name = "Ruby (Australian)",
      title = "Sunny & Bright",
      gender = VoiceGender.FEMALE,
      ageRange = VoiceAge.ADULT,
      accent = VoiceAccent.AUSTRALIAN,
      locale = Locale("en", "AU"),
      defaultPitch = 1.08f,
      defaultSpeed = 1.0f,
      description = "Warm Australian female voice, engaging and clear",
      avatarEmoji = "🐨",
      previewText = "Hello! Ruby from Down Under, bringing lively energy to your speech."
    ),

    // Indian English
    VoiceProfile(
      id = "in_aarav",
      name = "Aarav (Indian)",
      title = "Tech & Professional",
      gender = VoiceGender.MALE,
      ageRange = VoiceAge.ADULT,
      accent = VoiceAccent.INDIAN_ENGLISH,
      locale = Locale("en", "IN"),
      defaultPitch = 0.96f,
      defaultSpeed = 1.02f,
      description = "Articulate Indian English voice, great for technical tutorials and docs",
      avatarEmoji = "👨‍💻",
      previewText = "Namaste! Aarav here, delivering crystal clear explanations with accuracy."
    ),
    VoiceProfile(
      id = "in_priya",
      name = "Priya (Indian)",
      title = "Warm & Melodious",
      gender = VoiceGender.FEMALE,
      ageRange = VoiceAge.YOUNG_ADULT,
      accent = VoiceAccent.INDIAN_ENGLISH,
      locale = Locale("en", "IN"),
      defaultPitch = 1.12f,
      defaultSpeed = 1.0f,
      description = "Soothing, expressive Indian accent for stories and everyday text",
      avatarEmoji = "👩‍🎓",
      previewText = "Greetings! I am Priya, excited to convert your text with natural warmth."
    ),

    // Canadian English
    VoiceProfile(
      id = "ca_noah",
      name = "Noah (Canadian)",
      title = "Friendly & Direct",
      gender = VoiceGender.MALE,
      ageRange = VoiceAge.ADULT,
      accent = VoiceAccent.CANADIAN,
      locale = Locale.CANADA,
      defaultPitch = 0.97f,
      defaultSpeed = 1.0f,
      description = "Clear North American Canadian accent with steady pacing",
      avatarEmoji = "🍁",
      previewText = "Hey there! Noah from Canada, happy to assist with all your voiceovers."
    ),

    // Irish & Scottish
    VoiceProfile(
      id = "ie_conor",
      name = "Conor (Irish)",
      title = "Lively Celtic Charm",
      gender = VoiceGender.MALE,
      ageRange = VoiceAge.ADULT,
      accent = VoiceAccent.IRISH,
      locale = Locale("en", "IE"),
      defaultPitch = 0.94f,
      defaultSpeed = 1.04f,
      description = "Rich Irish lilt, melodic storytelling cadence",
      avatarEmoji = "🍀",
      previewText = "Top of the morning! Conor here to weave your words into pure magic."
    ),

    // Multilingual Accents / Languages
    VoiceProfile(
      id = "fr_antoine",
      name = "Antoine (French)",
      title = "Romantic & Smooth",
      gender = VoiceGender.MALE,
      ageRange = VoiceAge.ADULT,
      accent = VoiceAccent.FRENCH,
      locale = Locale.FRENCH,
      defaultPitch = 0.95f,
      defaultSpeed = 0.98f,
      description = "Sophisticated French pronunciation and charming accent",
      avatarEmoji = "🥖",
      previewText = "Bonjour! Je suis Antoine. Laissez-moi donner vie à vos plus beaux écrits."
    ),
    VoiceProfile(
      id = "es_sofia",
      name = "Sofía (Spanish)",
      title = "Passionate & Dynamic",
      gender = VoiceGender.FEMALE,
      ageRange = VoiceAge.YOUNG_ADULT,
      accent = VoiceAccent.SPANISH,
      locale = Locale("es", "ES"),
      defaultPitch = 1.1f,
      defaultSpeed = 1.05f,
      description = "Vibrant Spanish speaker with rhythmic delivery",
      avatarEmoji = "💃",
      previewText = "¡Hola! Soy Sofía. Lista para convertir tus textos en voz con pasión."
    ),
    VoiceProfile(
      id = "de_hans",
      name = "Hans (German)",
      title = "Structured & Powerful",
      gender = VoiceGender.MALE,
      ageRange = VoiceAge.ADULT,
      accent = VoiceAccent.GERMAN,
      locale = Locale.GERMAN,
      defaultPitch = 0.90f,
      defaultSpeed = 1.0f,
      description = "Deep, structured German voice with strong presence",
      avatarEmoji = "🏰",
      previewText = "Guten Tag! Ich bin Hans. Präzise und kraftvolle Sprachausgabe für Ihren Text."
    ),
    VoiceProfile(
      id = "jp_yuki",
      name = "Yuki (Japanese)",
      title = "Gentle & Polite",
      gender = VoiceGender.FEMALE,
      ageRange = VoiceAge.YOUNG_ADULT,
      accent = VoiceAccent.JAPANESE,
      locale = Locale.JAPANESE,
      defaultPitch = 1.2f,
      defaultSpeed = 1.0f,
      description = "Polite, melodic Japanese voice with anime nuance",
      avatarEmoji = "🌸",
      previewText = "こんにちは！ユキです。あなたのテキストを丁寧に読み上げます。"
    ),

    // Neutral & AI Synth
    VoiceProfile(
      id = "synth_nexus",
      name = "Nexus-9 (Cyber Synth)",
      title = "Futuristic AI Voice",
      gender = VoiceGender.NEUTRAL,
      ageRange = VoiceAge.MYTHICAL,
      accent = VoiceAccent.US_ENGLISH,
      locale = Locale.US,
      defaultPitch = 1.28f,
      defaultSpeed = 1.22f,
      description = "High-tech robotic assistant with synth harmonics",
      avatarEmoji = "🤖",
      previewText = "System online. Initializing neural vocal matrix for speech synthesis."
    )
  )
}
