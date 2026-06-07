package com.example.checkpoint.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.repositories.AchievementRepository
import com.example.checkpoint.data.session.SessionManager
import com.example.checkpoint.data.session.SessionState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AchievementsUiState(
	val achievements: List<AchievementUiModel> = emptyList(), val isLoading: Boolean = true
)

class AchievementsViewModel(
	private val sessionManager: SessionManager,
	private val achievementRepository: AchievementRepository,
) : ViewModel() {

	@OptIn(ExperimentalCoroutinesApi::class)
	val uiState: StateFlow<AchievementsUiState> =
		sessionManager.sessionState.flatMapLatest { session ->
			if (session is SessionState.LoggedIn) {
				combine(
					achievementRepository.getAllAchievements(),
					achievementRepository.getAchievementsForUser(session.userId)
				) { allAchievements, userProgress ->
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
							isPinned = prog?.isPinned ?: false
						)
					}
					AchievementsUiState(achievements = uiAchievements, isLoading = false)
				}
			} else {
				flowOf(AchievementsUiState(isLoading = false))
			}
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = AchievementsUiState()
		)

	fun togglePin(achievement: AchievementUiModel) {
		val session = sessionManager.sessionState.value
		if (session !is SessionState.LoggedIn) return

		val currentPinnedCount = uiState.value.achievements.count { it.isPinned }

		if (!achievement.isPinned && currentPinnedCount >= 3) return

		// Can only be pinned if it is actually unlocked
		if (!achievement.isPinned && !achievement.isUnlocked) return

		viewModelScope.launch {
			achievementRepository.updatePin(
				userId = session.userId,
				achievementId = achievement.id,
				isPinned = !achievement.isPinned,
				progress = achievement.progress,
				unlockedAt = achievement.unlockedAt
			)
		}
	}
}