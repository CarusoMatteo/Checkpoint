package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.checkpoint.data.ChipContent

@Composable
fun FilterChipRow(
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
				selected = chip.selected,
				leadingIcon = {
					if (chip.selected) {
						Icon(
							imageVector = Icons.Rounded.Check,
							contentDescription = "Deselect ${chip.label}"
						)
					} else null
				}
			)
		}
	}
}

@Preview
@Composable
private fun FilterChipRowPreview() {
	FilterChipRow(
		chips = listOf(
			ChipContent("Action", selected = true, action = { }),
			ChipContent("Adventure", selected = true, action = { }),
			ChipContent("RPG", selected = true, action = { }),
			ChipContent("Strategy", selected = true, action = { }),
			ChipContent("Simulation", selected = true, action = { })
		)
	)
}