package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.checkpoint.ui.icons.CalendarAddOn

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

@Composable
fun LabeledTextWithAction(
	title: String,
	contentText: String,
	actionText: String,
	modifier: Modifier = Modifier,
	onActionClick: () -> Unit
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		LabeledText(
			title = title,
			contentText = contentText
		)
		OutlinedButton(
			onClick = onActionClick,
			modifier = Modifier.padding(start = 16.dp)
		) {
			Icon(
				imageVector = CalendarAddOn,
				contentDescription = null,
			)
			Text(
				text = actionText,
				modifier = Modifier.padding(start = 8.dp)
			)
		}
	}
}

@Preview
@Composable
private fun LabeledTextWithActionPreview() {
	LabeledTextWithAction(
		title = "Release date",
		contentText = "January 1, 2024",
		actionText = "Add to calendar",
		modifier = Modifier
			.padding(16.dp)
			.fillMaxWidth()
	) { }
}