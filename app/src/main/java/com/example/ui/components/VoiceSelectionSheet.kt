package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VoiceAccent
import com.example.model.VoiceAge
import com.example.model.VoiceGender
import com.example.model.VoiceProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSelectionSheet(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  voices: List<VoiceProfile>,
  selectedVoice: VoiceProfile,
  onSelectVoice: (VoiceProfile) -> Unit,
  onPreviewVoice: (VoiceProfile) -> Unit,
  searchQuery: String,
  onSearchQueryChanged: (String) -> Unit,
  genderFilter: VoiceGender,
  onGenderFilterChanged: (VoiceGender) -> Unit,
  accentFilter: VoiceAccent,
  onAccentFilterChanged: (VoiceAccent) -> Unit,
  ageFilter: VoiceAge,
  onAgeFilterChanged: (VoiceAge) -> Unit,
  tabIndex: Int,
  onTabChanged: (Int) -> Unit,
  onClearFilters: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (!isOpen) return

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 6.dp,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    modifier = modifier.testTag("voice_selection_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.88f)
        .padding(horizontal = 16.dp)
    ) {
      // Sheet Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Select Voice Persona",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Pick gender, accent, and age range to speak your text",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_voice_sheet_button")
        ) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Close",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Tab selector: Curated Personas vs Installed System Voices
      PrimaryTabRow(
        selectedTabIndex = tabIndex,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 12.dp)
      ) {
        Tab(
          selected = tabIndex == 0,
          onClick = { onTabChanged(0) },
          text = { Text("Curated Personas", fontWeight = FontWeight.SemiBold) },
          modifier = Modifier.testTag("tab_curated_voices")
        )
        Tab(
          selected = tabIndex == 1,
          onClick = { onTabChanged(1) },
          text = { Text("System Voices", fontWeight = FontWeight.SemiBold) },
          modifier = Modifier.testTag("tab_system_voices")
        )
      }

      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        placeholder = { Text("Search voice name, country, accent...") },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.primary
          )
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchQueryChanged("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear search")
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
          unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("voice_search_input")
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Filter Section (Gender, Accent, Age)
      FilterBarSection(
        genderFilter = genderFilter,
        onGenderFilterChanged = onGenderFilterChanged,
        accentFilter = accentFilter,
        onAccentFilterChanged = onAccentFilterChanged,
        ageFilter = ageFilter,
        onAgeFilterChanged = onAgeFilterChanged
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Results header with active count and clear filters
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${voices.size} voices available",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )

        if (genderFilter != VoiceGender.ALL || accentFilter != VoiceAccent.ALL || ageFilter != VoiceAge.ALL || searchQuery.isNotEmpty()) {
          Text(
            text = "Reset filters",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier
              .clickable(onClick = onClearFilters)
              .padding(4.dp)
              .testTag("reset_filters_button")
          )
        }
      }

      HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
        modifier = Modifier.padding(bottom = 8.dp)
      )

      // Voices List
      if (voices.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(24.dp)
          ) {
            Text(
              text = "🔍",
              fontSize = 36.sp
            )
            Text(
              text = "No voices match current filters",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Try adjusting gender, accent, or age filters",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
              onClick = onClearFilters,
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("Clear All Filters")
            }
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
          items(voices, key = { it.id }) { voice ->
            VoiceListItemCard(
              voice = voice,
              isSelected = voice.id == selectedVoice.id,
              onSelect = {
                onSelectVoice(voice)
                onDismiss()
              },
              onPreview = { onPreviewVoice(voice) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun FilterBarSection(
  genderFilter: VoiceGender,
  onGenderFilterChanged: (VoiceGender) -> Unit,
  accentFilter: VoiceAccent,
  onAccentFilterChanged: (VoiceAccent) -> Unit,
  ageFilter: VoiceAge,
  onAgeFilterChanged: (VoiceAge) -> Unit
) {
  var activeFilterCategory by remember { mutableStateOf("gender") } // "gender", "accent", "age"

  Column(modifier = Modifier.fillMaxWidth()) {
    // Filter Category selector
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(vertical = 4.dp)
    ) {
      FilterCategoryChip(
        label = "Gender (${if (genderFilter == VoiceGender.ALL) "All" else genderFilter.displayName})",
        isSelected = activeFilterCategory == "gender",
        hasActiveFilter = genderFilter != VoiceGender.ALL,
        onClick = { activeFilterCategory = "gender" },
        modifier = Modifier.testTag("filter_category_gender")
      )
      FilterCategoryChip(
        label = "Accent (${if (accentFilter == VoiceAccent.ALL) "All" else accentFilter.displayName})",
        isSelected = activeFilterCategory == "accent",
        hasActiveFilter = accentFilter != VoiceAccent.ALL,
        onClick = { activeFilterCategory = "accent" },
        modifier = Modifier.testTag("filter_category_accent")
      )
      FilterCategoryChip(
        label = "Age (${if (ageFilter == VoiceAge.ALL) "All" else ageFilter.displayName})",
        isSelected = activeFilterCategory == "age",
        hasActiveFilter = ageFilter != VoiceAge.ALL,
        onClick = { activeFilterCategory = "age" },
        modifier = Modifier.testTag("filter_category_age")
      )
    }

    // Filter Options Carousel
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(vertical = 4.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      when (activeFilterCategory) {
        "gender" -> {
          items(VoiceGender.values()) { gender ->
            FilterChip(
              selected = genderFilter == gender,
              onClick = { onGenderFilterChanged(gender) },
              label = { Text(gender.displayName, fontSize = 12.sp) },
              shape = RoundedCornerShape(10.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              ),
              modifier = Modifier.testTag("gender_chip_${gender.name}")
            )
          }
        }
        "accent" -> {
          items(VoiceAccent.values()) { accent ->
            FilterChip(
              selected = accentFilter == accent,
              onClick = { onAccentFilterChanged(accent) },
              label = { Text("${accent.flag} ${accent.displayName}", fontSize = 12.sp) },
              shape = RoundedCornerShape(10.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
              ),
              modifier = Modifier.testTag("accent_chip_${accent.name}")
            )
          }
        }
        "age" -> {
          items(VoiceAge.values()) { age ->
            FilterChip(
              selected = ageFilter == age,
              onClick = { onAgeFilterChanged(age) },
              label = { Text(age.displayName, fontSize = 12.sp) },
              shape = RoundedCornerShape(10.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
              ),
              modifier = Modifier.testTag("age_chip_${age.name}")
            )
          }
        }
      }
    }
  }
}

@Composable
fun FilterCategoryChip(
  label: String,
  isSelected: Boolean,
  hasActiveFilter: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (isSelected) {
      MaterialTheme.colorScheme.primary
    } else if (hasActiveFilter) {
      MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
    } else {
      MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    },
    modifier = modifier.clickable(onClick = onClick)
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = if (isSelected || hasActiveFilter) FontWeight.Bold else FontWeight.Normal,
        fontSize = 11.5.sp
      ),
      color = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
      } else if (hasActiveFilter) {
        MaterialTheme.colorScheme.onPrimaryContainer
      } else {
        MaterialTheme.colorScheme.onSurfaceVariant
      },
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceListItemCard(
  voice: VoiceProfile,
  isSelected: Boolean,
  onSelect: () -> Unit,
  onPreview: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("voice_item_${voice.id}")
      .clickable(onClick = onSelect),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
      } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
      }
    ),
    border = if (isSelected) {
      BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
      BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.weight(1f)
        ) {
          // Voice avatar
          Surface(
            modifier = Modifier.size(46.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = voice.avatarEmoji,
                fontSize = 24.sp
              )
            }
          }

          Column(modifier = Modifier.weight(1f)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = voice.name,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              if (isSelected) {
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(2.dp)
                  )
                }
              }
            }
            Text(
              text = voice.title,
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
              color = MaterialTheme.colorScheme.primary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        // Preview sound button
        IconButton(
          onClick = onPreview,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .testTag("preview_btn_${voice.id}")
        ) {
          Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = "Listen to preview",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = voice.description,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Tags and action row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.weight(1f)
        ) {
          VoiceTagChip(
            label = "${voice.accent.flag} ${voice.accent.displayName}",
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
          )
          VoiceTagChip(
            label = voice.gender.displayName,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
          )
          VoiceTagChip(
            label = voice.ageRange.displayName,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Button(
          onClick = onSelect,
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
          modifier = Modifier
            .height(34.dp)
            .testTag("select_btn_${voice.id}")
        ) {
          Text(
            text = if (isSelected) "Selected" else "Use Voice",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }
  }
}
