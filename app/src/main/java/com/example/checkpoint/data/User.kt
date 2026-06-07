package com.example.checkpoint.data

/**
 * Domain model for the user.
 * Represents the "live" user who is using the app,
 * Converted from UserEntity (Room).
 */
data class User(
	val id: Int,
	val name: String,
	val profilePicture: String? = null
)


val sampleUsers = listOf(
	User(id = 0, name = "User with a really long name that should probably be truncated"),
	User(id = 1, name = "John Doe"),
	User(id = 2, name = "Jane Smith"),
	User(id = 3, name = "Alex Johnson"),
	User(id = 4, name = "Emily Davis"),
	User(id = 5, name = "Michael Brown")
)