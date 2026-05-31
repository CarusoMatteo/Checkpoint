package com.example.checkpoint.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.database.entities.AchievementCategoryEntity
import com.example.checkpoint.data.database.entities.AchievementEntity
import com.example.checkpoint.data.database.entities.ReviewEntity
import com.example.checkpoint.data.database.entities.UserAchievementEntity
import com.example.checkpoint.data.repositories.AchievementRepository
import com.example.checkpoint.data.repositories.ReviewRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AchievementUiModel(
	val id: Int,
	val code: String,
	val name: String,
	val description: String?,
	val iconUrl: String?,
	val threshold: Int,
	val progress: Int,
	val unlockedAt: String?,
) {
	val isUnlocked: Boolean get() = progress >= threshold
	val progressFraction: Float get() = (progress.toFloat() / threshold).coerceIn(0f, 1f)
}

data class ProfileState(
	val reviews: List<ReviewEntity> = emptyList(),
	val achievements: List<AchievementUiModel> = emptyList(),
	val isLoading: Boolean = true,
)

/**
 * ViewModel per ProfileScreen.
 *
 * [userId] = 1 è l'utente "logged-in" di default fin quando non è
 * implementato il login. Da sostituire con la sessione reale.
 */
class ProfileViewModel(
	private val reviewRepository: ReviewRepository,
	private val achievementRepository: AchievementRepository,
	private val userId: Int = 1,
) : ViewModel() {

	val state: StateFlow<ProfileState> = combine(
		reviewRepository.getReviewsByUser(userId),
		achievementRepository.getAllAchievements(),
		achievementRepository.getAchievementsForUser(userId),
	) { reviews, allAchievements, userProgress ->

		val progressMap: Map<Int, UserAchievementEntity> =
			userProgress.associateBy { it.achievementId }

		val uiAchievements = allAchievements.map { ach ->
			val prog = progressMap[ach.id]
			AchievementUiModel(
				id = ach.id,
				code = ach.code,
				name = ach.name,
				description = ach.description,
				iconUrl = ach.iconUrl,
				threshold = ach.threshold,
				progress = prog?.progress ?: 0,
				unlockedAt = prog?.unlockedAt,
			)
		}

		ProfileState(
			reviews = reviews,
			achievements = uiAchievements,
			isLoading = false,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = ProfileState(isLoading = true),
	)
}
