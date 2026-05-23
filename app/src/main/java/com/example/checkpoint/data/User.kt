package com.example.checkpoint.data

data class User(
	val name: String,
	// TODO: set actual type for image.
	// null if user has monogram profile picture
	val profilePicture: String? = null
)

val sampleUsers = listOf(
	User(name = "User with a really long name that should probably be truncated"),
	User(name = "John Doe"),
	User(name = "Jane Smith"),
	User(name = "Alex Johnson"),
	User(name = "Emily Davis"),
	User(name = "Michael Brown")
)