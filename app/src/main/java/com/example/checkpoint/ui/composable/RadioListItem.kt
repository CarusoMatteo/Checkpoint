package com.example.checkpoint.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
fun RadioListItem(
	text: String,
	selected: Boolean,
	modifier: Modifier = Modifier,
	onClick: () -> Unit = { }
) {
	Row(
		modifier = modifier
			.clip(MaterialTheme.shapes.small)
			.clickable(onClick = onClick),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Text(text)
		RadioButton(
			selected = selected,
			onClick = null
		)
	}
}