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
) {
	/**
	 * Login: Verify username/email and password, save the session.
	 * The session is persisted to the DataStore by SessionManager.
	 */
	suspend fun login(usernameOrEmail: String, password: String): Result<UserEntity> {
		return try {
			val user = userDao.getUserByUsername(usernameOrEmail) ?: userDao.getUserByEmail(
				usernameOrEmail
			) ?: return Result.failure(Exception("User not found"))

			// Fallback support: if the seed data does not contain the separator ':'
			// valueOfSalt:valueOfHash
			val isCorrect = if (user.passwordHash.contains(":")) {
				val parts = user.passwordHash.split(":")
				PasswordHasher.verifyPassword(password, parts[0], parts[1])
			} else {
				// Fallback per account del DatabaseSeeder
				user.passwordHash == password
			}

			return if (isCorrect) {
				// Save the session to the DataStore
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
	 * The session is persisted to the DataStore.
	 */
	suspend fun signUp(
		username: String, email: String, password: String, bio: String = ""
	): Result<UserEntity> {
		return try {
			if (userDao.getUserByUsername(username) != null) {
				return Result.failure(Exception("Username già in uso"))
			}
			if (userDao.getUserByEmail(email) != null) {
				return Result.failure(Exception("Email già in uso"))
			}

			val salt = PasswordHasher.generateSalt()
			val hash = PasswordHasher.hashPassword(password, salt)
			val combinedPasswordHash = "$salt:$hash"

			val newUser = UserEntity(
				username = username,
				email = email,
				passwordHash = combinedPasswordHash,
				bio = bio,
				publicProfile = true,
				createdAt = Instant.now().toString()
			)

			val generatedId = userDao.upsert(newUser)
			val loggedInUser = newUser.copy(id = generatedId.toInt())

			// Save the session to the DataStore
			sessionManager.login(loggedInUser.id, loggedInUser.username)
			Log.d(
				TAG,
				"SignUp successful: userId=${loggedInUser.id}, username=${loggedInUser.username}"
			)
			return Result.success(loggedInUser)
		} catch (e: Exception) {
			Log.e(TAG, "SignUp error: ${e.message}", e)
			Result.failure(e)
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