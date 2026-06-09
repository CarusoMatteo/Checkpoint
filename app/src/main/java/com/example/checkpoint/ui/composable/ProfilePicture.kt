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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.checkpoint.data.database.entities.UserEntity

@Composable
fun ProfilePicture(
	user: UserEntity,
	modifier: Modifier = Modifier,
	fontSize: ProfileMonogramFontSize = ProfileMonogramFontSize.Review
) {
	if (!user.avatarUrl.isNullOrEmpty()) {
		AsyncImage(
			model = user.avatarUrl,
			contentDescription = "${user.username}'s profile picture",
			contentScale = ContentScale.Crop,
			modifier = modifier
				.size(if (fontSize == ProfileMonogramFontSize.Profile) 80.dp else 40.dp)
				.clip(CircleShape)
		)
	} else {
		ProfileMonogram(
			letter = user.username.firstOrNull() ?: '?', modifier = modifier, fontSize = fontSize
		)
	}
}

enum class ProfileMonogramFontSize(val size: TextUnit) {
	Review(16.sp), Profile(35.sp)
}

@Composable
fun ProfileMonogram(
	letter: Char,
	modifier: Modifier = Modifier,
	fontSize: ProfileMonogramFontSize = ProfileMonogramFontSize.Review
) {
	Box(
		contentAlignment = Alignment.Center,
		modifier = modifier
			.size(40.dp)
			.clip(CircleShape)
			.background(MaterialTheme.colorScheme.primaryContainer)
	) {
		Text(
			text = letter.uppercaseChar().toString(), style = TextStyle(
				color = MaterialTheme.colorScheme.onPrimaryContainer,
				fontSize = fontSize.size,
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