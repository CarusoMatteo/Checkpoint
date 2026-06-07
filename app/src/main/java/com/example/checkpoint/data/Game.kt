package com.example.checkpoint.data

import com.example.checkpoint.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

data class LocalGame(
	val name: String,
	val publisher: String,
	val imageResourceId: Int,
	val description: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris. Maecenas congue ligula ac quam viverra nec consectetur ante hendrerit. Donec et mollis dolor. Praesent et diam eget libero egestas mattis sit amet vitae augue. Nam tincidunt congue enim, ut porta lorem lacinia consectetur.",
	val releaseDate: LocalDate = LocalDate.now(),
	val genres: List<ChipContent> = sampleChipContents,
	val reviews: List<Review> = emptyList(),
	val notificationsEnabled: Boolean = false
)

fun LocalDate.toLocalFormat(
	dateStyle: FormatStyle = FormatStyle.MEDIUM
): String {
	return this.format(
		DateTimeFormatter.ofLocalizedDate(dateStyle).withLocale(Locale.getDefault())
	)
}


val sampleLocalGames = listOf(
	LocalGame(
		"The Legend of Zelda: Breath of the Wild - Nintendo Switch 2 Edition",
		"Nintendo",
		R.drawable.botw,
		description = "Travel across vast fields, through forests, and to mountain peaks as you discover what has become of the kingdom of Hyrule-and step into a world of discovery, exploration, and adventure that's been upgraded with performance enhancements in The Legend of Zelda: Breath of the Wild - Nintendo Switch 2 Edition! With improved framerates, faster load times, and enhanced resolution and textures, this thrilling open-air adventure has never looked better!"
	),
	LocalGame("Resident Evil Requiem", "CAPCOM", R.drawable.re9),
	LocalGame("Pragmata", "CAPCOM", R.drawable.pgm),
	LocalGame("Resident Evil Village", "CAPCOM", R.drawable.re8),
	LocalGame("007 First Light", "IO Interactive", R.drawable.fl),
	LocalGame("Tomodachi Life: Living the Dream", "Nintendo", R.drawable.tmdltd),
	LocalGame(
		"The Legend of Zelda: Breath of the Wild - Nintendo Switch 2 Edition",
		"Nintendo",
		R.drawable.botw
	),
	LocalGame("Resident Evil Requiem", "CAPCOM", R.drawable.re9),
	LocalGame("Pragmata", "CAPCOM", R.drawable.pgm),
	LocalGame("Resident Evil Village", "CAPCOM", R.drawable.re8),
)
