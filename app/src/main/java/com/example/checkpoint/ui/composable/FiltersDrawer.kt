package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.checkpoint.data.ChipContent
import com.example.checkpoint.data.database.entities.GenreEntity
import com.example.checkpoint.data.database.entities.PlatformEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersDrawer(
	showBottomSheet: MutableState<Boolean>,
	genres: List<GenreEntity>,
	platforms: List<PlatformEntity>,
	selectedGenreIds: Set<Int>,
	selectedPlatformIds: Set<Int>,
	onGenreToggle: (Int) -> Unit,
	onPlatformToggle: (Int) -> Unit,
	onResetAll: () -> Unit
) {
	val sheetState = rememberModalBottomSheetState()
	val scope = rememberCoroutineScope()

	// Filter State pending befor Apply button is pressed
	val pendingGenreIds = remember { mutableStateOf(selectedGenreIds) }
	val pendingPlatformIds = remember { mutableStateOf(selectedPlatformIds) }

	fun togglePendingGenre(id: Int) {
		val updated = pendingGenreIds.value.toMutableSet()
		if (!updated.remove(id)) updated.add(id)
		pendingGenreIds.value = updated
	}

	fun togglePendingPlatform(id: Int) {
		val updated = pendingPlatformIds.value.toMutableSet()
		if (!updated.remove(id)) updated.add(id)
		pendingPlatformIds.value = updated
	}

	// Commits the diff between pending state and current VM state
	fun applyToViewModel() {
		val genresToAdd = pendingGenreIds.value - selectedGenreIds
		val genresToRemove = selectedGenreIds - pendingGenreIds.value
		genresToAdd.forEach { onGenreToggle(it) }
		genresToRemove.forEach { onGenreToggle(it) }

		val platformsToAdd = pendingPlatformIds.value - selectedPlatformIds
		val platformsToRemove = selectedPlatformIds - pendingPlatformIds.value
		platformsToAdd.forEach { onPlatformToggle(it) }
		platformsToRemove.forEach { onPlatformToggle(it) }
	}

	ModalBottomSheet(
		// Dismiss without Apply, pending changes are discarded
		onDismissRequest = { showBottomSheet.value = false }, sheetState = sheetState
	) {
		Column(
			modifier = Modifier
				.weight(1f, false)
				.verticalScroll(rememberScrollState())
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp),
				horizontalArrangement = Arrangement.Center
			) {
				Text(
					"Filter games", style = MaterialTheme.typography.titleLarge
				)
			}

			if (genres.isNotEmpty()) {
				val genreChips = genres.map { genre ->
					ChipContent(
						label = genre.name,
						selected = pendingGenreIds.value.contains(genre.igdbId),
						action = { togglePendingGenre(genre.igdbId) })
				}
				LabeledChipGrid(
					title = "Genres",
					chips = genreChips,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp, vertical = 8.dp)
				)
			}

			HorizontalDivider(Modifier.padding(horizontal = 16.dp))

			if (platforms.isNotEmpty()) {
				val platformChips = platforms.map { platform ->
					ChipContent(
						label = platform.abbreviation ?: platform.name,
						selected = pendingPlatformIds.value.contains(platform.igdbId),
						action = { togglePendingPlatform(platform.igdbId) })
				}
				LabeledChipGrid(
					title = "Platforms",
					chips = platformChips,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp, vertical = 8.dp)
				)
			}
		}

		HorizontalDivider(Modifier.padding(horizontal = 16.dp))
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			horizontalArrangement = Arrangement.spacedBy(16.dp)
		) {
			val totalPending = pendingGenreIds.value.size + pendingPlatformIds.value.size

			OutlinedButton(
				// Reset all: clears only the local pending state
				onClick = {
					pendingGenreIds.value = emptySet()
					pendingPlatformIds.value = emptySet()
				}, modifier = Modifier.fillMaxWidth(0.5f), enabled = totalPending > 0
			) {
				Text("Reset all")
				if (totalPending > 0) {
					Spacer(Modifier.width(8.dp))
					Badge { Text(totalPending.toString()) }
				}
			}

			Button(
				// Apply: commits pending state to vm, then closes the sheet
				onClick = {
					applyToViewModel()
					scope.launch { sheetState.hide() }.invokeOnCompletion {
						if (!sheetState.isVisible) {
							showBottomSheet.value = false
						}
					}
				}, modifier = Modifier.fillMaxWidth()
			) {
				Text("Apply")
			}
		}
	}
}

@Preview
@Composable
private fun FiltersDrawerPreview() {
	val showBottomSheet = remember { mutableStateOf(true) }
	Box(
		modifier = Modifier.fillMaxSize()
	) {
		FiltersDrawer(
			showBottomSheet = showBottomSheet,
			genres = emptyList(),
			platforms = emptyList(),
			selectedGenreIds = emptySet(),
			selectedPlatformIds = emptySet(),
			onGenreToggle = {},
			onPlatformToggle = {},
			onResetAll = {})
	}
}