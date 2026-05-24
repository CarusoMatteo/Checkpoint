package com.example.checkpoint.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkpoint.data.User

@Composable
fun ProfilePicture(
	user: User,
	modifier: Modifier = Modifier
) {
	if (user.profilePicture != null) {
		// TODO: Load actual profile picture if the user has one
		throw NotImplementedError("Profile picture loading not implemented yet")
	} else {
		ProfileMonogram(
			letter = user.name.first(),
			modifier
		)
	}
}

@Composable
private fun ProfileMonogram(
	letter: Char,
	modifier: Modifier = Modifier
) {
	Box(
		contentAlignment = Alignment.Center,
		modifier = modifier
			.size(40.dp)
			.clip(CircleShape)
			.background(MaterialTheme.colorScheme.primaryContainer)
	) {
		Text(
			text = letter.uppercaseChar().toString(),
			style = TextStyle(
				color = MaterialTheme.colorScheme.onPrimaryContainer,
				fontSize = 16.sp,
				fontWeight = FontWeight.Medium
			)
		)
	}
}

@Preview
@Composable
private fun ProfileMonogramPreview() {
	ProfileMonogram(letter = 'A')
}