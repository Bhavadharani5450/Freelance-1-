package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val role: String, // STUDENT, COLLEGE_CLIENT, EXTERNAL_CLIENT, ADMIN
    val phone: String = "+91 98765 43210",
    val organization: String = "Kangeyam Institute of Technology",
    val department: String = "Computer Science & Engineering",
    val year: String = "4th Year",
    val bio: String = "Passionate tech enthusiast building scalable campus solutions.",
    val skills: String = "React, Node.js, Python, UI/UX", // comma-separated
    val hourlyRate: Int = 500,
    val rating: Float = 4.9f,
    val projectsCompleted: Int = 8,
    val isVerified: Boolean = true,
    val github: String = "https://github.com/freeverse-kit",
    val linkedin: String = "https://linkedin.com/in/freeverse-kit",
    val portfolio: String = "https://freeverse.kit.ac.in",
    val availability: String = "Available for Projects"
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val clientId: String,
    val clientName: String,
    val clientType: String, // College Department, Club, Startup, Tech Company
    val title: String,
    val description: String,
    val category: String, // Web Development, UI/UX, AI / ML, Video, Design, etc.
    val projectType: String, // CAMPUS, INDUSTRY, MICRO_GIG, CHALLENGE
    val requiredSkills: String, // comma-separated
    val budget: Int,
    val deadline: String,
    val proposalCount: Int = 0,
    val status: String = "OPEN", // OPEN, IN_PROGRESS, WORK_SUBMITTED, COMPLETED
    val hiredStudentId: String? = null,
    val hiredStudentName: String? = null,
    val progressPercent: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "proposals")
data class ProposalEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val projectTitle: String,
    val studentId: String,
    val studentName: String,
    val studentRole: String,
    val coverMessage: String,
    val relevantSkills: String,
    val portfolioLink: String,
    val expectedDelivery: String,
    val proposedBudget: Int,
    val status: String = "PENDING", // PENDING, SHORTLISTED, HIRED, WORK_SUBMITTED, COMPLETED, REJECTED
    val submissionNotes: String? = null,
    val clientReviewComment: String? = null,
    val clientRating: Float? = null,
    val studentReviewComment: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "campus_gigs")
data class CampusGigEntity(
    @PrimaryKey val id: String,
    val task: String,
    val clubOrDepartment: String,
    val reward: Int, // in INR
    val deadline: String,
    val skills: String,
    val applicantsCount: Int = 3,
    val isApplied: Boolean = false
)

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // Workshop, Competition, Seminar, Technical, Non-Technical
    val date: String,
    val time: String,
    val venue: String,
    val description: String,
    val rules: String,
    val registeredCount: Int = 24,
    val isRegistered: Boolean = false,
    val isPast: Boolean = false
)

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val freelancerName: String,
    val freelancerRole: String,
    val startingPrice: Int,
    val deliveryDays: Int,
    val rating: Float = 4.9f,
    val reviewsCount: Int = 12,
    val description: String
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // PROJECT, APPLICATION, HIRE, REVIEW, EVENT
    val timeAgo: String,
    val isRead: Boolean = false
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val projectTitle: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "assessment_results")
data class AssessmentResultEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val subject: String,
    val scorePercent: Int,
    val verifiedSkills: String,
    val completedAt: Long = System.currentTimeMillis()
)
