package com.example.checkpoint.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.database.daos.UserDao
import com.example.checkpoint.data.database.entities.UserEntity
import com.example.checkpoint.data.database.entities.UserAchievementEntity
import com.example.checkpoint.data.repositories.AchievementRepository
import com.example.checkpoint.data.repositories.ReviewRepository
import com.example.checkpoint.data.session.SessionManager
import com.example.checkpoint.data.session.SessionState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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

// 1. Aggiorna lo stato includendo l'entità UserEntity dal DB
data class ProfileState(
	val user: UserEntity? = null,
	val reviews: List<com.example.checkpoint.data.database.entities.ReviewEntity> = emptyList(),
	val achievements: List<AchievementUiModel> = emptyList(),
	val isLoading: Boolean = true,
)

class ProfileViewModel(
	private val sessionManager: SessionManager,
	private val userDao: UserDao,
	private val reviewRepository: ReviewRepository,
	private val achievementRepository: AchievementRepository,
) : ViewModel() {

	// 2. Usiamo flatMapLatest per ricreare il flusso ogni volta che la sessione cambia
	@OptIn(ExperimentalCoroutinesApi::class)
	val state: StateFlow<ProfileState> = sessionManager.sessionState.flatMapLatest { session ->
		if (session is SessionState.LoggedIn) {
			// Uniamo i flussi provenienti dal database usando l'ID dell'utente loggato
			combine(
				userDao.getUserById(session.userId),
				reviewRepository.getReviewsByUser(session.userId),
				achievementRepository.getAllAchievements(),
				achievementRepository.getAchievementsForUser(session.userId)
			) { user, reviews, allAchievements, userProgress ->

				val progressMap = userProgress.associateBy { it.achievementId }
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
					user = user,
					reviews = reviews,
					achievements = uiAchievements,
					isLoading = false
				)
			}
		} else {
			// Se non è loggato, restituisce uno stato vuoto
			flowOf(ProfileState(isLoading = false))
		}
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = ProfileState(isLoading = true)
	)
}
