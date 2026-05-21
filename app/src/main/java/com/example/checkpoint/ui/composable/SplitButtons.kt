package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SmallSplitButtons(
	onPrimaryClick: () -> Unit,
	onSecondaryClick: () -> Unit,
	primaryIcon: @Composable () -> Unit,
	primaryLabel: String,
	secondaryIcon: @Composable () -> Unit,
	modifier: Modifier = Modifier
) {
	Row(modifier = modifier) {
		Button(
			onClick = onPrimaryClick,
			shape = RoundedCornerShape(
				topStart = 20.dp,
				bottomStart = 20.dp,
				topEnd = 4.dp,
				bottomEnd = 4.dp
			),
			contentPadding = PaddingValues(start = 12.dp, end = 10.dp),
			modifier = Modifier.height(40.dp)
		) {
			primaryIcon()
			Text(
				text = primaryLabel,
				modifier = Modifier.padding(start = 4.dp)
			)
		}
		Spacer(modifier.width(2.dp))
		Button(
			onClick = onSecondaryClick,
			shape = RoundedCornerShape(
				topStart = 4.dp,
				bottomStart = 4.dp,
				topEnd = 20.dp,
				bottomEnd = 20.dp
			),
			contentPadding = PaddingValues(start = 12.dp, end = 14.dp),
			modifier = Modifier.height(40.dp)
		) {
			secondaryIcon()
		}
	}
}

@Preview
@Composable
private fun SmallSplitButtonsPreview() {
	SmallSplitButtons(
		onPrimaryClick = { },
		onSecondaryClick = { },
		primaryIcon = {
			Icon(
				imageVector = Icons.Rounded.AddCircleOutline,
				contentDescription = null
			)
		},
		primaryLabel = "Add to Backlog",
		secondaryIcon = {
			Icon(
				imageVector = Icons.Rounded.KeyboardArrowDown,
				contentDescription = "Add to List"
			)
		}
	)
}