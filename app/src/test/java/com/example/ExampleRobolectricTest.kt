package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.VoiceCatalog
import com.example.model.VoiceGender
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TTS God", appName)
  }

  @Test
  fun `verify voice catalog contains diverse genders and accents`() {
    val voices = VoiceCatalog.builtInVoices
    assertTrue(voices.isNotEmpty())

    val hasMale = voices.any { it.gender == VoiceGender.MALE }
    val hasFemale = voices.any { it.gender == VoiceGender.FEMALE }
    val hasNeutral = voices.any { it.gender == VoiceGender.NEUTRAL }

    assertTrue("Should have male voices", hasMale)
    assertTrue("Should have female voices", hasFemale)
    assertTrue("Should have neutral voices", hasNeutral)
  }

  @Test
  fun `verify speech speed presets within valid bounds`() {
    val presets = VoiceCatalog.defaultPresets
    assertTrue(presets.isNotEmpty())

    presets.forEach { preset ->
      assertTrue("Speed ${preset.speed} should be >= 0.5f", preset.speed >= 0.5f)
      assertTrue("Speed ${preset.speed} should be <= 2.5f", preset.speed <= 2.5f)
      assertTrue("Pitch ${preset.pitch} should be >= 0.5f", preset.pitch >= 0.5f)
      assertTrue("Pitch ${preset.pitch} should be <= 2.0f", preset.pitch <= 2.0f)
    }
  }
}
