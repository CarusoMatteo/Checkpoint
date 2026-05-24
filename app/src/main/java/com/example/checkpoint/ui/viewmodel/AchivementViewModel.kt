package com.example.checkpoint.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.checkpoint.data.Achievement
import com.example.checkpoint.data.sampleAchievements
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AchievementsViewModel : ViewModel() {

	val allAchievements: List<Achievement> = sampleAchievements

	private val _pinnedIds = MutableStateFlow<Set<Int>>(emptySet())
	val pinnedIds: StateFlow<Set<Int>> = _pinnedIds.asStateFlow()

	val pinnedAchievements: List<Achievement>
		get() = allAchievements.filter { it.id in _pinnedIds.value }

	fun togglePin(achievementId: Int) {
		val current = _pinnedIds.value
		_pinnedIds.value = if (current.contains(achievementId)) {
			current - achievementId
		} else if (current.size < 3) {
			current + achievementId
		} else {
			current
		}
	}

	fun isPinned(achievementId: Int): Boolean = achievementId in _pinnedIds.value
	fun canPin(achievement: Achievement): Boolean =
		achievement.isUnlocked && (isPinned(achievement.id) || _pinnedIds.value.size < 3)
}