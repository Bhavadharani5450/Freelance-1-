package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AssessmentResultEntity
import com.example.data.model.CampusGigEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.EventEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ProposalEntity
import com.example.data.model.ServiceEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FreeverseDao {
    // Users
    @Query("SELECT * FROM users ORDER BY rating DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    // Projects
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    fun getProjectById(id: String): Flow<ProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<ProjectEntity>)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    // Proposals
    @Query("SELECT * FROM proposals ORDER BY createdAt DESC")
    fun getAllProposals(): Flow<List<ProposalEntity>>

    @Query("SELECT * FROM proposals WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getProposalsByProjectId(projectId: String): Flow<List<ProposalEntity>>

    @Query("SELECT * FROM proposals WHERE studentId = :studentId ORDER BY createdAt DESC")
    fun getProposalsByStudentId(studentId: String): Flow<List<ProposalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProposal(proposal: ProposalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProposals(proposals: List<ProposalEntity>)

    @Update
    suspend fun updateProposal(proposal: ProposalEntity)

    // Campus Gigs
    @Query("SELECT * FROM campus_gigs ORDER BY reward DESC")
    fun getAllGigs(): Flow<List<CampusGigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGigs(gigs: List<CampusGigEntity>)

    @Update
    suspend fun updateGig(gig: CampusGigEntity)

    // Events
    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Update
    suspend fun updateEvent(event: EventEntity)

    // Services
    @Query("SELECT * FROM services ORDER BY startingPrice ASC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timeAgo ASC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    // Chat
    @Query("SELECT * FROM chat_messages WHERE projectId = :projectId ORDER BY timestamp ASC")
    fun getChatMessages(projectId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessages(messages: List<ChatMessageEntity>)

    // Assessments
    @Query("SELECT * FROM assessment_results WHERE studentId = :studentId")
    fun getAssessmentsForStudent(studentId: String): Flow<List<AssessmentResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: AssessmentResultEntity)
}
