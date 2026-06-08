package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.checkpoint.data.repositories.CompletionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewDialog(
	onDismissRequest: () -> Unit,
	onSubmit: (Float, String, CompletionType) -> Unit
) {
	var rating by remember { mutableFloatStateOf(0f) }
	val reviewBodyTextField = rememberTextFieldState("")
	var selectedCompletionType by remember { mutableStateOf<CompletionType?>(null) }
	var errorMessage by remember { mutableStateOf<String?>(null) }

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
				Modifier
					.padding(24.dp)
					.verticalScroll(rememberScrollState())
			) {
				Text(
					"New review",
					style = MaterialTheme.typography.headlineSmall
				)
				Spacer(Modifier.height(8.dp))
				Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
					ReviewRating(
						rating = rating,
						iconSize = 50.dp
					)
				}
				Spacer(Modifier.height(8.dp))
				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					Text(
						text = "0",
						style = MaterialTheme.typography.labelSmall,
					)
					Slider(
						value = rating,
						onValueChange = { rating = it },
						valueRange = 0f..5f,
						steps = 9,
						modifier = Modifier.weight(1f)
					)
					Text(
						text = "5",
						style = MaterialTheme.typography.labelSmall,
					)
				}
				Spacer(Modifier.height(8.dp))
				OutlinedTextField(
					state = reviewBodyTextField,
					label = { Text("Review body") },
					modifier = Modifier.fillMaxWidth()
				)
				Spacer(Modifier.height(8.dp))
				HorizontalDivider()

				CompletionType.entries.forEach {
					RadioListItem(
						it.displayName,
						selected = selectedCompletionType == it,
						onClick = { selectedCompletionType = it },
						modifier = Modifier
							.fillMaxWidth()
							.height(56.dp)
					)
				}

				// Error feedback text
				if (errorMessage != null) {
					Text(
						text = errorMessage!!,
						color = MaterialTheme.colorScheme.error,
						style = MaterialTheme.typography.bodySmall,
						modifier = Modifier.padding(top = 8.dp)
					)
				}

				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 16.dp),
					horizontalArrangement = Arrangement.End
				) {
					TextButton(
						onClick = onDismissRequest
					) {
						Text("Cancel")
					}
					TextButton(
						onClick = {
							if (selectedCompletionType == null) {
								errorMessage = "Please select a completion type."
								return@TextButton
							}
							if (reviewBodyTextField.text.isBlank()) {
								errorMessage = "Please write a review body."
								return@TextButton
							}

							// Forward the result
							onSubmit(
								rating,
								reviewBodyTextField.text.toString(),
								selectedCompletionType!!
							)
						}
					) {
						Text("Submit")
					}
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun WriteReviewDialogPreview() {
	Box(Modifier.fillMaxSize()) {
		WriteReviewDialog(
			onDismissRequest = { },
			onSubmit = { _, _, _ -> }
		)
	}
}