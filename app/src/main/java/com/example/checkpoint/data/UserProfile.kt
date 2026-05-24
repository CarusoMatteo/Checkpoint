package com.example.checkpoint.data

data class UserProfile(
	val user: User,
	val email: String,
	val bio: String,
	val preferredGenres: List<String>,
	val reviews: List<Review>
)

val sampleUserProfile = UserProfile(
	user = sampleUsers[1],
	email = "email@example.com",
	bio = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
			"Sed commodo commodo dolor, sit amet dapibus ante. " +
			"Nunc feugiat, augue imperdiet tempus rhoncus, nisl metus sagittis dolor, " +
			"nec scelerisque lectus metus ut lorem. Aliquam erat volutpat. " +
			"Pellentesque habitant morbi tristique senectus et netus et malesuada " +
			"fames ac turpis egestas.",
	preferredGenres = listOf(
		"Shooter",
		"Puzzle",
		"Adventure",
		"First person shooter",
		"Role Playing Game"
	),
	reviews = sampleReviews
)