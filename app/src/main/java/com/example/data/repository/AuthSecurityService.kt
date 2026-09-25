package com.example.data.repository

import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import java.security.MessageDigest
import java.util.UUID

data class AuthSession(
    val token: String,
    val userId: String,
    val userEmail: String,
    val role: UserRole,
    val expiresAt: Long
)

sealed class AuthResult {
    data class Success(val user: UserEntity, val session: AuthSession) : AuthResult()
    data class Error(val message: String, val isRateLimited: Boolean = false) : AuthResult()
}

object AuthSecurityService {

    private val failedAttempts = mutableMapOf<String, Int>()
    private val lockoutTimestamps = mutableMapOf<String, Long>()
    private val activeSessions = mutableMapOf<String, AuthSession>()

    private const val MAX_FAILED_ATTEMPTS = 5
    private const val LOCKOUT_DURATION_MS = 60_000L // 1 minute lockout
    private const val SESSION_DURATION_MS = 24 * 60 * 60 * 1000L // 24 hours

    /**
     * Compute SHA-256 hash with salt
     */
    fun hashPassword(password: String, salt: String): String {
        val input = "$salt:$password"
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verify plain-text password against stored hash and salt
     */
    fun verifyPassword(password: String, storedHash: String, salt: String): Boolean {
        if (storedHash.isBlank()) return false
        val computed = hashPassword(password, salt)
        return computed.equals(storedHash, ignoreCase = true)
    }

    /**
     * Authenticate user with role verification.
     * Enforces that the target role must match the user's registered role in the backend database.
     */
    fun authenticate(
        user: UserEntity?,
        enteredPassword: String,
        expectedRole: UserRole
    ): AuthResult {
        if (user == null) {
            return AuthResult.Error("No account found with this email address for ${expectedRole.displayName}.")
        }

        val email = user.email.lowercase()
        val now = System.currentTimeMillis()

        // Check rate limiting lockout
        val lockoutTime = lockoutTimestamps[email] ?: 0L
        if (now < lockoutTime) {
            val remainingSec = ((lockoutTime - now) / 1000).coerceAtLeast(1)
            return AuthResult.Error(
                "Too many failed login attempts. Account temporarily locked for security. Please try again in $remainingSec seconds.",
                isRateLimited = true
            )
        }

        // Account status check
        if (user.accountStatus.equals("DEACTIVATED", ignoreCase = true)) {
            return AuthResult.Error("This account has been deactivated by the administrator. Contact support.")
        }
        if (user.accountStatus.equals("SUSPENDED", ignoreCase = true)) {
            return AuthResult.Error("This account has been suspended due to security policy violations.")
        }

        // Validate password
        val isPasswordValid = if (user.passwordHash.isNotBlank()) {
            verifyPassword(enteredPassword, user.passwordHash, user.passwordSalt)
        } else {
            // Default fallback passwords for demo seeds
            when (expectedRole) {
                UserRole.SUPER_ADMIN -> enteredPassword == "Admin@123"
                UserRole.FACULTY_COORDINATOR -> enteredPassword == "Faculty@123"
                UserRole.CLUB_COORDINATOR -> enteredPassword == "Coord@123"
                UserRole.MEMBER -> enteredPassword == "Student@123" || enteredPassword == "Member@123"
                UserRole.CLIENT -> enteredPassword == "Client@123"
            }
        }

        if (!isPasswordValid) {
            val attempts = (failedAttempts[email] ?: 0) + 1
            failedAttempts[email] = attempts
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                lockoutTimestamps[email] = now + LOCKOUT_DURATION_MS
                failedAttempts.remove(email)
                return AuthResult.Error(
                    "Maximum failed attempts exceeded. Security lockout activated for 60 seconds.",
                    isRateLimited = true
                )
            }
            val remaining = MAX_FAILED_ATTEMPTS - attempts
            return AuthResult.Error("Invalid credentials. $remaining attempts remaining before temporary lockout.")
        }

        // Backend Role Authorization Check (MANDATORY)
        val userRole = UserRole.fromKey(user.role)
        if (userRole != expectedRole) {
            // Specifically log and reject cross-role attempts with strict message
            return AuthResult.Error(
                "These credentials are not registered as a ${expectedRole.displayName}."
            )
        }

        // Login successful: reset attempts & create secure session
        failedAttempts.remove(email)
        lockoutTimestamps.remove(email)

        val token = "fv_sec_" + UUID.randomUUID().toString().replace("-", "")
        val session = AuthSession(
            token = token,
            userId = user.id,
            userEmail = user.email,
            role = userRole,
            expiresAt = now + SESSION_DURATION_MS
        )
        activeSessions[token] = session

        return AuthResult.Success(user, session)
    }

    /**
     * Check if a given session token is valid and belongs to the authorized roles
     */
    fun isAuthorized(token: String?, vararg allowedRoles: UserRole): Boolean {
        if (token == null) return false
        val session = activeSessions[token] ?: return false
        if (System.currentTimeMillis() > session.expiresAt) {
            activeSessions.remove(token)
            return false
        }
        return allowedRoles.isEmpty() || allowedRoles.contains(session.role)
    }

    /**
     * Invalidate session on logout
     */
    fun logout(token: String?) {
        if (token != null) {
            activeSessions.remove(token)
        }
    }
}
