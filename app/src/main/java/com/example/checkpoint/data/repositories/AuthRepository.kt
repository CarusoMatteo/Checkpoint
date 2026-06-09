package com.example.checkpoint.data.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.checkpoint.data.database.daos.UserDao
import com.example.checkpoint.data.database.entities.UserEntity
import com.example.checkpoint.data.security.PasswordHasher
import com.example.checkpoint.data.session.SessionManager
import java.io.File
import java.io.FileOutputStream
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

			val isCorrect = if (user.passwordHash.contains(":")) {
				val parts = user.passwordHash.split(":")
				PasswordHasher.verifyPassword(password, parts[0], parts[1])
			} else {
				// Fallback for dataSeeder accounts
				user.passwordHash == password
			}

			if (isCorrect) {
				sessionManager.login(user.id, user.username)
				Result.success(user)
			} else {
				Result.failure(Exception("Invalid credentials"))
			}
		} catch (e: Exception) {
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
				return Result.failure(Exception("Username already in use"))
			}
			if (userDao.getUserByEmail(email) != null) {
				return Result.failure(Exception("Email already in use"))
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

			// // Save the session to the DataStore
			sessionManager.login(loggedInUser.id, loggedInUser.username)
			Log.d(TAG, "SignUp successful: userId=${loggedInUser.id}")

			Result.success(loggedInUser)
		} catch (e: Exception) {
			Log.e(TAG, "SignUp error: ${e.message}", e)
			Result.failure(e)
		}
	}

	/**
	 * Logout: deletes the session from the DataStore.
	 */
	suspend fun logout() {
		sessionManager.logout()
	}

	/**
	 * Save the chosen or taken image within the internal directory of the App.
	 */
	fun saveAvatar(context: Context, uri: Uri, userId: Int): String? {
		return try {
			val avatarDir = File(context.filesDir, "avatars").also { it.mkdirs() }
			val destFile = File(avatarDir, "user_$userId.jpg")
			context.contentResolver.openInputStream(uri)?.use { input ->
				FileOutputStream(destFile).use { output -> input.copyTo(output) }
			}
			destFile.absolutePath
		} catch (e: Exception) {
			Log.e(TAG, "Error saving avatar locally: ${e.message}")
			null
		}
	}
}