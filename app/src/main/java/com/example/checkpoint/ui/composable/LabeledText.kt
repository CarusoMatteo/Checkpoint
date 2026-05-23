package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LabeledText(
	title: String,
	contentText: String,
	modifier: Modifier = Modifier
) {
	Column(modifier = modifier) {
		Text(
			text = title,
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier.padding(bottom = 8.dp)
		)
		Text(
			text = contentText,
			style = MaterialTheme.typography.bodyMedium
		)
	}
}