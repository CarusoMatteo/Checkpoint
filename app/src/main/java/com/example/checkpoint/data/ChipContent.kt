package com.example.checkpoint.data

data class ChipContent(
	val label: String,
	val selected: Boolean = false,
	val action: () -> Unit = { }
)

val sampleChipContents = listOf(
	ChipContent("Action"),
	ChipContent("Adventure"),
	ChipContent("RPG"),
	ChipContent("Strategy"),
	ChipContent("Simulation"),
	ChipContent("Sports"),
	ChipContent("Puzzle"),
	ChipContent("Horror"),
	ChipContent("Racing"),
	ChipContent("Fighting"),
	ChipContent("Platformer"),
	ChipContent("Shooter"),
	ChipContent("Stealth"),
	ChipContent("Survival"),
	ChipContent("MMO"),
	ChipContent("Sandbox"),
	ChipContent("Open World"),
	ChipContent("Indie")
)