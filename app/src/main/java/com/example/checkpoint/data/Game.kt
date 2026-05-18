package com.example.checkpoint.data

import com.example.checkpoint.R

data class LocalGame(
	val name: String,
	val publisher: String,
	val imageResourceId: Int
)


val sampleLocalGames = listOf(
	LocalGame(
		"The Legend of Zelda: Breath of the Wild - Nintendo Switch 2 Edition",
		"Nintendo",
		R.drawable.botw
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
