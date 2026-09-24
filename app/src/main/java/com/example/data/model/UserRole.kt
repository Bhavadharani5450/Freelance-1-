package com.example.data.model

enum class UserRole(val displayName: String, val badgeColorHex: Long) {
    STUDENT("Student / Freelancer", 0xFF6C4DF6),
    CLIENT("Client", 0xFF3B82F6),
    ADMIN("Admin", 0xFF10B981),
    SUPER_ADMIN("Super Admin", 0xFFF59E0B),
    EVENT_MANAGER("Event Manager", 0xFFEC4899);

    companion object {
        val COLLEGE_CLIENT = CLIENT
        val EXTERNAL_CLIENT = CLIENT
    }
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
