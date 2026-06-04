package com.example.checkpoint.data.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Cryptographic utility object for securely hashing and verifying user passwords.
 */
object PasswordHasher {
	private const val ALGORITHM = "SHA-256"
	private const val SALT_LENGTH = 16

	/**
	 * Generates a cryptographically secure random salt.
	 *
	 * The salt is a unique string intended to be stored in plain text in the database
	 * alongside the hashed password. It does not need to be kept secret.
	 *
	 * @return A Base64 encoded string representing the generated salt.
	 */
	fun generateSalt(): String {
		val random = SecureRandom()
		val salt = ByteArray(SALT_LENGTH)
		random.nextBytes(salt)
		return Base64.encodeToString(salt, Base64.NO_WRAP)
	}

	/**
	 * Computes the cryptographic hash of the plain-text password combined with the provided salt.
	 *
	 * @param password The plain-text password to hash.
	 * @param salt The Base64 encoded salt string associated with the user.
	 * @return A Base64 encoded string representing the final salted password hash.
	 */
	fun hashPassword(password: String, salt: String): String {
		val md = MessageDigest.getInstance(ALGORITHM)
		md.update(Base64.decode(salt, Base64.NO_WRAP))
		val hashedPassword = md.digest(password.toByteArray(Charsets.UTF_8))
		return Base64.encodeToString(hashedPassword, Base64.NO_WRAP)
	}

	/**
	 * Verifies if an incoming plain-text password matches the expected stored hash.
	 *
	 * @param password The plain-text password provided during login/verification.
	 * @param salt The Base64 encoded salt retrieved from the user's records.
	 * @param expectedHash The target Base64 encoded hash stored in the database.
	 * @return True if the computed hash matches the expected hash, false otherwise.
	 */
	fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
		val actualHash = hashPassword(password, salt)
		return actualHash == expectedHash
	}
}