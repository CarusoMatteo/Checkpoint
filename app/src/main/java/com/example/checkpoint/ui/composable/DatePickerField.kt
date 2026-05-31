package com.example.checkpoint.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
	label: String,
	modifier: Modifier = Modifier,
	onDateSelected: (Long?) -> Unit = {}
) {
	var showDatePicker by remember { mutableStateOf(false) }
	val datePickerState = rememberDatePickerState()

	val selectedDateText = datePickerState.selectedDateMillis?.let { millis ->
		SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())
			.format(Date(millis))
	} ?: ""

	OutlinedTextField(
		value = selectedDateText,
		onValueChange = { },
		readOnly = true,
		label = { Text(label) },
		placeholder = { Text("DD / MM / YYYY") },
		trailingIcon = {
			IconButton(onClick = { showDatePicker = true }) {
				Icon(
					imageVector = Icons.Rounded.DateRange,
					contentDescription = "Pick a date"
				)
			}
		},
		modifier = modifier
			.clickable { showDatePicker = true }
	)

	// --- Date Picker Dialog ---
	if (showDatePicker) {
		DatePickerDialog(
			onDismissRequest = { showDatePicker = false },
			confirmButton = {
				TextButton(
					onClick = {
						showDatePicker = false
						onDateSelected(datePickerState.selectedDateMillis)
					}
				) {
					Text("OK")
				}
			},
			dismissButton = {
				TextButton(onClick = { showDatePicker = false }) {
					Text("Cancel")
				}
			}
		) {
			DatePicker(state = datePickerState)
		}
	}
}

@Preview
@Composable
private fun DatePickerFieldPreview() {
	DatePickerField(
		label = "Date of Birth"
	) { }
}