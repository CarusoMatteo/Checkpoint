package com.example.checkpoint.data.repositories

import com.example.checkpoint.data.database.daos.UserDao
import com.example.checkpoint.data.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing User data.
 */
class UserRepository(
	private val userDao: UserDao
) {

	fun getUserById(id: Int): Flow<UserEntity?> {
		return userDao.getUserById(id)
	}

	suspend fun getUserByUsername(username: String): UserEntity? {
		return userDao.getUserByUsername(username)
	}

	suspend fun getUserByEmail(email: String): UserEntity? {
		return userDao.getUserByEmail(email)
	}

	/**
	 * Retrieves a list of users by providing a list of their IDs.
	 * Very useful for populating review lists.
	 */
	suspend fun getUsersByIds(userIds: List<Int>): List<UserEntity> {
		if (userIds.isEmpty()) return emptyList()
		return userDao.getUsersByIds(userIds)
	}

	suspend fun upsertUser(user: UserEntity): Long {
		return userDao.upsert(user)
	}

	suspend fun deleteUser(user: UserEntity) {
		userDao.delete(user)
	}
}