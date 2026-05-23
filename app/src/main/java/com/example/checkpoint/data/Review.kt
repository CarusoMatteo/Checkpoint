package com.example.checkpoint.data

data class Review(
	val creator: User,
	val rating: Float,
	val comment: String,
	val completion: ReviewCompletion,
)

enum class ReviewCompletion(val description: String) {
	MAIN("Main story only"),
	MAIN_AND_EXTRA("Main + Extras"),
	COMPLETED("100% completion"),
}

val sampleReviews = listOf(
	Review(
		creator = sampleUsers[0],
		rating = 4.5f,
		comment = "Great game with an engaging story and fun gameplay! I completed everything and loved every minute of it.",
		completion = ReviewCompletion.COMPLETED
	),
	Review(
		creator = sampleUsers[1],
		rating = 3.0f,
		comment = "The game was enjoyable, but I found the controls a bit clunky.",
		completion = ReviewCompletion.MAIN
	),
	Review(
		creator = sampleUsers[2],
		rating = 5.0f,
		comment = "Absolutely loved it! The graphics and soundtrack were amazing.",
		completion = ReviewCompletion.MAIN_AND_EXTRA
	),
	Review(
		creator = sampleUsers[3],
		rating = 2.5f,
		comment = "The game had potential, but it was plagued with bugs and performance issues.",
		completion = ReviewCompletion.MAIN
	)
)