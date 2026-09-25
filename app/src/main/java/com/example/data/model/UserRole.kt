package com.example.data.model

enum class UserRole(
    val roleKey: String,
    val displayName: String,
    val badgeColorHex: Long,
    val loginRoute: String,
    val dashboardRoute: String,
    val description: String
) {
    SUPER_ADMIN(
        roleKey = "SUPER_ADMIN",
        displayName = "Super Admin",
        badgeColorHex = 0xFFF59E0B, // Amber
        loginRoute = "/login/admin",
        dashboardRoute = "/admin/dashboard",
        description = "System Administration & Full Control"
    ),
    FACULTY_COORDINATOR(
        roleKey = "FACULTY_COORDINATOR",
        displayName = "Faculty Coordinator",
        badgeColorHex = 0xFF10B981, // Emerald
        loginRoute = "/login/faculty",
        dashboardRoute = "/faculty/dashboard",
        description = "Faculty Monitoring & Club Oversight"
    ),
    CLUB_COORDINATOR(
        roleKey = "CLUB_COORDINATOR",
        displayName = "Club Coordinator",
        badgeColorHex = 0xFF8B5CF6, // Purple
        loginRoute = "/login/coordinator",
        dashboardRoute = "/coordinator/dashboard",
        description = "Club Management + Freelancing"
    ),
    MEMBER(
        roleKey = "MEMBER",
        displayName = "Member / Freelancer",
        badgeColorHex = 0xFF3B82F6, // Blue
        loginRoute = "/login/member",
        dashboardRoute = "/member/dashboard",
        description = "Find Work, Submit Proposals & Earn"
    ),
    CLIENT(
        roleKey = "CLIENT",
        displayName = "Client",
        badgeColorHex = 0xFF0D9488, // Teal
        loginRoute = "/login/client",
        dashboardRoute = "/client/dashboard",
        description = "Hire Student Talent & Post Projects"
    );

    companion object {
        fun fromKey(key: String): UserRole {
            return entries.find { it.roleKey.equals(key, ignoreCase = true) || it.name.equals(key, ignoreCase = true) }
                ?: when (key.uppercase()) {
                    "STUDENT" -> MEMBER
                    "ADMIN" -> SUPER_ADMIN
                    "EVENT_MANAGER" -> CLUB_COORDINATOR
                    "COLLEGE_CLIENT", "EXTERNAL_CLIENT" -> CLIENT
                    else -> MEMBER
                }
        }
    }
}

enum class CoordinatorMode {
    COORDINATOR,
    FREELANCER
}

enum class ProjectType(val displayName: String) {
    CAMPUS("Campus Project"),
    INDUSTRY("Industry Project"),
    MICRO_GIG("Quick Micro Gig"),
    CHALLENGE("24-Hour Challenge")
}

enum class ProjectStatus {
    OPEN,
    IN_PROGRESS,
    WORK_SUBMITTED,
    COMPLETED,
    CANCELLED
}

enum class ApplicationStatus {
    PENDING,
    SHORTLISTED,
    HIRED,
    WORK_SUBMITTED,
    COMPLETED,
    REJECTED
}
