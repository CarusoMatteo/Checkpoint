package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.checkpoint.data.ChipContent
import com.example.checkpoint.data.sampleChipContents

@Composable
fun LabeledChipRow(
	title: String,
	chips: List<ChipContent>,
	modifier: Modifier = Modifier,
	padding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
	Column(modifier = modifier) {
		Text(
			text = title,
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier
				.padding(padding)
				.padding(bottom = 8.dp)
		)
		ChipRow(
			chips,
			modifier = Modifier.fillMaxWidth(),
			padding
		)
	}
}

@Composable
fun ChipRow(
	chips: List<ChipContent>,
	modifier: Modifier = Modifier,
	padding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
	LazyRow(
		modifier = modifier,
		contentPadding = padding,
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		items(chips) { chip ->
			FilterChip(
				label = { Text(chip.label) },
				onClick = chip.action,
				selected = chip.selected
			)
		}
	}
}

@Preview
@Composable
private fun LabeledChipRowPreview() {
	LabeledChipRow(
		title = "Genres",
		chips = sampleChipContents,
		modifier = Modifier.fillMaxWidth()
	)
}

@Preview
@Composable
private fun ChipRowPreview() {
	ChipRow(
		chips = sampleChipContents,
		modifier = Modifier.fillMaxWidth()
	)
}