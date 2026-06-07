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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersDrawer(
	showBottomSheet: MutableState<Boolean>
) {
	val sheetState = rememberModalBottomSheetState()
	val scope = rememberCoroutineScope()
	ModalBottomSheet(
		sheetState = sheetState,
		onDismissRequest = { showBottomSheet.value = false },
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
					"Filter games",
					style = MaterialTheme.typography.titleLarge
				)
			}

			LabeledChipGrid(
				title = "Platforms",
				chips = listOf(
					ChipContent("PlayStation 5", selected = true, action = { }),
					ChipContent("PlayStation 4", selected = false, action = { }),
					ChipContent("PlayStation 3", selected = false, action = { }),
					ChipContent("Nintendo Switch 2", selected = false, action = { }),
					ChipContent("Nintendo Switch", selected = false, action = { }),
					ChipContent("Wii", selected = false, action = { }),
					ChipContent("Steam (PC)", selected = true, action = { }),
					ChipContent("Epic Games Store (PC)", selected = false, action = { }),
					ChipContent("Xbox Series X|S", selected = false, action = { }),
					ChipContent("Xbox One", selected = false, action = { }),
				),
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 8.dp)
			)
			HorizontalDivider(Modifier.padding(horizontal = 16.dp))
			LabeledChipGrid(
				title = "Platforms",
				chips = listOf(
					ChipContent("PlayStation 5", selected = true, action = { }),
					ChipContent("PlayStation 4", selected = false, action = { }),
					ChipContent("PlayStation 3", selected = false, action = { }),
					ChipContent("Nintendo Switch 2", selected = false, action = { }),
					ChipContent("Nintendo Switch", selected = false, action = { }),
					ChipContent("Wii", selected = false, action = { }),
					ChipContent("Steam (PC)", selected = true, action = { }),
					ChipContent("Epic Games Store (PC)", selected = false, action = { }),
					ChipContent("Xbox Series X|S", selected = false, action = { }),
					ChipContent("Xbox One", selected = false, action = { }),
				),
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 8.dp)
			)
			HorizontalDivider(Modifier.padding(horizontal = 16.dp))
			LabeledChipGrid(
				title = "Platforms",
				chips = listOf(
					ChipContent("PlayStation 5", selected = true, action = { }),
					ChipContent("PlayStation 4", selected = false, action = { }),
					ChipContent("PlayStation 3", selected = false, action = { }),
					ChipContent("Nintendo Switch 2", selected = false, action = { }),
					ChipContent("Nintendo Switch", selected = false, action = { }),
					ChipContent("Wii", selected = false, action = { }),
					ChipContent("Steam (PC)", selected = true, action = { }),
					ChipContent("Epic Games Store (PC)", selected = false, action = { }),
					ChipContent("Xbox Series X|S", selected = false, action = { }),
					ChipContent("Xbox One", selected = false, action = { }),
				),
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 8.dp)
			)
			HorizontalDivider(Modifier.padding(horizontal = 16.dp))
			LabeledChipGrid(
				title = "Platforms",
				chips = listOf(
					ChipContent("PlayStation 5", selected = true, action = { }),
					ChipContent("PlayStation 4", selected = false, action = { }),
					ChipContent("PlayStation 3", selected = false, action = { }),
					ChipContent("Nintendo Switch 2", selected = false, action = { }),
					ChipContent("Nintendo Switch", selected = false, action = { }),
					ChipContent("Wii", selected = false, action = { }),
					ChipContent("Steam (PC)", selected = true, action = { }),
					ChipContent("Epic Games Store (PC)", selected = false, action = { }),
					ChipContent("Xbox Series X|S", selected = false, action = { }),
					ChipContent("Xbox One", selected = false, action = { }),
				),
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 8.dp)
			)
			HorizontalDivider(Modifier.padding(horizontal = 16.dp))
			LabeledChipGrid(
				title = "Platforms",
				chips = listOf(
					ChipContent("PlayStation 5", selected = true, action = { }),
					ChipContent("PlayStation 4", selected = false, action = { }),
					ChipContent("PlayStation 3", selected = false, action = { }),
					ChipContent("Nintendo Switch 2", selected = false, action = { }),
					ChipContent("Nintendo Switch", selected = false, action = { }),
					ChipContent("Wii", selected = false, action = { }),
					ChipContent("Steam (PC)", selected = true, action = { }),
					ChipContent("Epic Games Store (PC)", selected = false, action = { }),
					ChipContent("Xbox Series X|S", selected = false, action = { }),
					ChipContent("Xbox One", selected = false, action = { }),
				),
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 8.dp)
			)
			HorizontalDivider(Modifier.padding(horizontal = 16.dp))
			LabeledChipGrid(
				title = "Platforms",
				chips = listOf(
					ChipContent("PlayStation 5", selected = true, action = { }),
					ChipContent("PlayStation 4", selected = false, action = { }),
					ChipContent("PlayStation 3", selected = false, action = { }),
					ChipContent("Nintendo Switch 2", selected = false, action = { }),
					ChipContent("Nintendo Switch", selected = false, action = { }),
					ChipContent("Wii", selected = false, action = { }),
					ChipContent("Steam (PC)", selected = true, action = { }),
					ChipContent("Epic Games Store (PC)", selected = false, action = { }),
					ChipContent("Xbox Series X|S", selected = false, action = { }),
					ChipContent("Xbox One", selected = false, action = { }),
				),
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 8.dp)
			)
			HorizontalDivider(Modifier.padding(horizontal = 16.dp))
			LabeledChipGrid(
				title = "Genre",
				chips = listOf(
					ChipContent("Action", selected = false, action = { }),
					ChipContent("Adventure", selected = false, action = { }),
					ChipContent("RPG", selected = true, action = { }),
					ChipContent("Strategy", selected = true, action = { }),
					ChipContent("Simulation", selected = true, action = { }),
					ChipContent("Survival Horror", selected = true, action = { }),
					ChipContent("Indie", selected = true, action = { }),
				),
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 8.dp)
			)
		}

		HorizontalDivider(Modifier.padding(horizontal = 16.dp))
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			horizontalArrangement = Arrangement.spacedBy(16.dp)
		) {
			OutlinedButton(
				onClick = { /* TODO: reset */ },
				modifier = Modifier.fillMaxWidth(0.5f)
			) {
				Text("Reset all")
				Spacer(Modifier.width(8.dp))
				Badge { Text("3") }
			}
			Button(
				onClick = {
					scope.launch { sheetState.hide() }.invokeOnCompletion {
						if (!sheetState.isVisible) {
							showBottomSheet.value = false
						}
					}
					// TODO: Apply filters
				},
				modifier = Modifier.fillMaxWidth()
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
		FiltersDrawer(showBottomSheet)
	}
}