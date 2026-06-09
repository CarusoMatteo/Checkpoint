package com.example.checkpoint.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.checkpoint.data.ChipContent
import com.example.checkpoint.data.sampleChipContents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTextDialog(
	onSubmit: (String) -> Unit,
	onDismissRequest: () -> Unit,
	fieldToUpdate: String,
	previousValue: String? = null
) {
	val updateFieldTextField = rememberTextFieldState("")

	BasicAlertDialog(
		onDismissRequest = onDismissRequest
	) {
		Surface(
			modifier = Modifier
				.wrapContentWidth()
				.wrapContentHeight(),
			shape = MaterialTheme.shapes.large,
			tonalElevation = AlertDialogDefaults.TonalElevation,
			color = MaterialTheme.colorScheme.surfaceContainerHigh
		) {
			Column(
				Modifier.padding(24.dp)
			) {
				Text(
					"Update $fieldToUpdate",
					style = MaterialTheme.typography.headlineSmall
				)
				Spacer(Modifier.height(8.dp))
				OutlinedTextField(
					state = updateFieldTextField,
					label = { Text("New $fieldToUpdate") },
					placeholder = { Text(previousValue ?: "") },
					modifier = Modifier.fillMaxWidth()
				)
				Row(
					modifier = Modifier
						.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {
					TextButton(
						onClick = onDismissRequest
					) {
						Text("Cancel")
					}
					TextButton(
						onClick = {
							onSubmit(updateFieldTextField.text.toString())
							onDismissRequest()
						}
					) {
						Text("Submit")
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateFavouriteGenresDialog(
	genreChips: List<ChipContent>,
	initialSelectedGenres: List<String> = emptyList(),
	onDismissRequest: () -> Unit,
	onSubmit: (List<ChipContent>) -> Unit
) {
	val selectedChips = remember {
		mutableStateListOf<String>().apply {
			addAll(genreChips.map { it.label }.filter { it in initialSelectedGenres })
		}
	}

	Dialog(
		onDismissRequest = onDismissRequest,
		properties = DialogProperties(
			usePlatformDefaultWidth = false,
			dismissOnClickOutside = false
		)
	) {
		Scaffold(
			modifier = Modifier.fillMaxSize(),
			topBar = {
				TopAppBar(
					title = { Text("Edit favourite genres") },
					actions = {
						TextButton(onClick = {
							onSubmit(selectedChips.map { ChipContent(it) })
							onDismissRequest()
						}) {
							Text("Save")
						}
					}
				)
			}
		) { paddingValues ->
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(paddingValues)
					.background(MaterialTheme.colorScheme.background)
					.verticalScroll(rememberScrollState())
			) {
				Row(
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(vertical = 12.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = "Select up to three genres to display them in your profile!",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						modifier = Modifier
							.weight(1f)
							.padding(end = 8.dp)
					)
					Text(
						text = "Selected: ${selectedChips.size} / 3",
						style = MaterialTheme.typography.labelMedium,
						fontWeight = FontWeight.SemiBold,
						color = if (selectedChips.size == 3)
							MaterialTheme.colorScheme.primary
						else
							MaterialTheme.colorScheme.onSurfaceVariant
					)
				}

				//ChipGrid could not find a vai to implement, im stupid
				FlowRow(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp),
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalArrangement = Arrangement.spacedBy(8.dp)
				) {
					genreChips.forEach { chip ->
						val isSelected = selectedChips.contains(chip.label)
						FilterChip(
							selected = isSelected,
							onClick = {
								if (isSelected) {
									selectedChips.remove(chip.label)
								} else {
									if (selectedChips.size < 3) {
										selectedChips.add(chip.label)
									}
								}
							},
							label = { Text(chip.label) }
						)
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun UpdateTextDialogPreview() {
	Box(Modifier.fillMaxSize()) {
		UpdateTextDialog(
			onSubmit = { },
			onDismissRequest = { },
			fieldToUpdate = "Username",
			previousValue = "OldUsername"
		)
	}
}

@Preview
@Composable
private fun UUpdateFavouriteGenresDialogPreview() {
	Box(Modifier.fillMaxSize()) {
		UpdateFavouriteGenresDialog(
			sampleChipContents,
			initialSelectedGenres = listOf("Action", "RPG"),
			onDismissRequest = { },
			onSubmit = { },
		)
	}
}