package com.example.checkpoint.data.repositories

import android.util.Log
import com.example.checkpoint.data.database.daos.UserDao
import com.example.checkpoint.data.database.entities.UserEntity
import com.example.checkpoint.data.security.PasswordHasher
import com.example.checkpoint.data.session.SessionManager
import java.time.Instant

private const val TAG = "Auth"

class AuthRepository(
	private val userDao: UserDao,
	private val sessionManager: SessionManager,
	private val gameListRepository: GameListRepository
) {
	/**
	 * Login: Verify username/email and password, save the session.
	 * The session is persisted to the DataStore by SessionManager.
	 */
	suspend fun login(usernameOrEmail: String, password: String): Result<UserEntity> {
		return try {
			val user = userDao.getUserByUsername(usernameOrEmail)
				?: userDao.getUserByEmail(usernameOrEmail)
				?: return Result.failure(Exception("User not found"))

			val isCorrect = if (user.passwordHash.contains(":")) {
				val parts = user.passwordHash.split(":")
				PasswordHasher.verifyPassword(password, parts[0], parts[1])
			} else {
				user.passwordHash == password
			}

			return if (isCorrect) {
				sessionManager.login(user.id, user.username)
				Log.d(TAG, "Login successful: userId=${user.id}, username=${user.username}")
				Result.success(user)
			} else {
				Log.w(TAG, "Login failed: Invalid credentials for $usernameOrEmail")
				Result.failure(Exception("Invalid credentials"))
			}
		} catch (e: Exception) {
			Log.e(TAG, "Login error: ${e.message}", e)
			Result.failure(e)
		}
	}

	/**
	 * SignUp: Create a new user with salt + hash, then log in.
	 * After registration, creates the default BACKLOG and SAVED lists automatically.
	 * The session is persisted to the DataStore.
	 */
	suspend fun signUp(
		username: String, email: String, password: String, bio: String = ""
	): Result<UserEntity> {
		return try {
			if (userDao.getUserByUsername(username) != null) {
				return Result.failure(Exception("Username already in use"))
			}
			if (userDao.getUserByEmail(email) != null) {
				return Result.failure(Exception("Email already in use"))
			}

			val salt = PasswordHasher.generateSalt()
			val hash = PasswordHasher.hashPassword(password, salt)

			val newUser = UserEntity(
				username = username,
				email = email,
				passwordHash = "$salt:$hash",
				bio = bio,
				publicProfile = true,
				createdAt = Instant.now().toString()
			)

			val generatedId = userDao.upsert(newUser)
			val loggedInUser = newUser.copy(id = generatedId.toInt())

			// Create the default lists for the new user
			gameListRepository.createList(
				userId = loggedInUser.id,
				name = "Backlog",
				type = "BACKLOG",
				isPublic = true
			)
			gameListRepository.createList(
				userId = loggedInUser.id,
				name = "Saved",
				type = "SAVED",
				isPublic = true
			)
			Log.d(TAG, "Default lists created for userId=${loggedInUser.id}")

			sessionManager.login(loggedInUser.id, loggedInUser.username)
			Log.d(TAG, "SignUp successful: userId=${loggedInUser.id}")
			Result.success(loggedInUser)
		} catch (e: Exception) {
			Log.e(TAG, "SignUp error: ${e.message}", e)
			Result.failure(e)
		}
	}

	/**
	 * Update avatarUrl in DB after file is copied to filesDir.
	 */
	suspend fun updateAvatarUrl(userId: Int, avatarPath: String) {
		try {
			val users = userDao.getUsersByIds(listOf(userId))
			val user = users.firstOrNull() ?: return
			userDao.upsert(user.copy(avatarUrl = avatarPath))
			Log.d(TAG, "Avatar updated for userId=$userId")
		} catch (e: Exception) {
			Log.e(TAG, "Error updating avatar: ${e.message}", e)
		}
	}

	/**
	 * Logout: deletes the session from the DataStore.
	 */
	suspend fun logout() {
		try {
			sessionManager.logout()
			Log.d(TAG, "Logout successful")
		} catch (e: Exception) {
			Log.e(TAG, "Logout error: ${e.message}", e)
		}
	}
}