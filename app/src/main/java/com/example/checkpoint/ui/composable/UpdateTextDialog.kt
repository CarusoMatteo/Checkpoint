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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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

@Composable
fun UpdateFavouriteGenresDialog(
	onSubmit: (List<String>) -> Unit,
	onDismissRequest: () -> Unit
) {
	// TODO: Implement here
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