package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.AssessmentResultEntity
import com.example.data.model.CampusGigEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.EventEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ProposalEntity
import com.example.data.model.ServiceEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.FreeverseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenNav(val title: String) {
    HOME("Home"),
    FREELANCERS("Freelancers"),
    PROJECTS("Projects"),
    SERVICES("Services"),
    GIGS("Campus Gigs"),
    EVENTS("Events"),
    ABOUT_KIT("About KIT"),
    DASHBOARD("My Dashboard"),
    MESSAGES("Messages")
}

class FreeverseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FreeverseRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = FreeverseRepository(db.freeverseDao())
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    // Active Navigation
    private val _currentScreen = MutableStateFlow(ScreenNav.HOME)
    val currentScreen: StateFlow<ScreenNav> = _currentScreen.asStateFlow()

    fun navigateTo(screen: ScreenNav) {
        _currentScreen.value = screen
    }

    // Data streams from repository
    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProposals: StateFlow<List<ProposalEntity>> = repository.allProposals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGigs: StateFlow<List<CampusGigEntity>> = repository.allGigs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEvents: StateFlow<List<EventEntity>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allServices: StateFlow<List<ServiceEntity>> = repository.allServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<NotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allChatMessages: StateFlow<List<ChatMessageEntity>> = repository.allChatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently logged-in user
    private val _currentUser = MutableStateFlow(FreeverseRepository.initialUsers[0]) // Defaults to Bhavadharani S
    val currentUser: StateFlow<UserEntity> = _currentUser.asStateFlow()

    // Test accounts quick switcher for presentation
    fun switchUserAccount(user: UserEntity) {
        _currentUser.value = user
        _snackbarMessage.value = "Switched perspective to: ${user.name} (${user.role})"
    }

    fun switchUserByRole(role: UserRole) {
        val user = when (role) {
            UserRole.STUDENT -> FreeverseRepository.initialUsers[0]
            UserRole.CLIENT -> FreeverseRepository.initialUsers[1]
            UserRole.ADMIN -> FreeverseRepository.initialUsers[2]
            UserRole.SUPER_ADMIN -> FreeverseRepository.initialUsers[3]
            UserRole.EVENT_MANAGER -> FreeverseRepository.initialUsers[4]
        }
        _currentUser.value = user
        _snackbarMessage.value = "Logged in as ${user.name} [${role.displayName}]"
    }

    // Search and filters for Freelancers
    val freelancerSearchQuery = MutableStateFlow("")
    val freelancerSelectedSkill = MutableStateFlow("All")
    val freelancerSelectedSort = MutableStateFlow("Recommended") // "Recommended", "Highest Rated", "Most Projects"

    val filteredFreelancers: StateFlow<List<UserEntity>> = combine(
        allUsers,
        freelancerSearchQuery,
        freelancerSelectedSkill,
        freelancerSelectedSort
    ) { users, query, skill, sort ->
        var list = users.filter { it.role == "STUDENT" }
        if (query.isNotBlank()) {
            list = list.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.skills.contains(query, ignoreCase = true) ||
                it.department.contains(query, ignoreCase = true) ||
                it.bio.contains(query, ignoreCase = true)
            }
        }
        if (skill != "All") {
            list = list.filter { it.skills.contains(skill, ignoreCase = true) }
        }
        when (sort) {
            "Highest Rated" -> list.sortedByDescending { it.rating }
            "Most Projects" -> list.sortedByDescending { it.projectsCompleted }
            else -> list.sortedByDescending { it.rating * 10 + it.projectsCompleted }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and filters for Projects
    val projectSearchQuery = MutableStateFlow("")
    val projectSelectedCategory = MutableStateFlow("All")
    val projectSelectedType = MutableStateFlow("All")

    val filteredProjects: StateFlow<List<ProjectEntity>> = combine(
        allProjects,
        projectSearchQuery,
        projectSelectedCategory,
        projectSelectedType
    ) { projects, query, category, type ->
        var list = projects
        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.requiredSkills.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.clientName.contains(query, ignoreCase = true)
            }
        }
        if (category != "All") {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }
        if (type != "All") {
            list = list.filter { it.projectType.equals(type, ignoreCase = true) }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Modals and dialogs
    val selectedFreelancerForProfile = MutableStateFlow<UserEntity?>(null)
    val selectedProjectForDetail = MutableStateFlow<ProjectEntity?>(null)
    val selectedProjectForApply = MutableStateFlow<ProjectEntity?>(null)
    val isAuthDialogOpen = MutableStateFlow(false)
    val isNotificationsOpen = MutableStateFlow(false)
    val isPostProjectOpen = MutableStateFlow(false)
    val isSkillAssessmentOpen = MutableStateFlow(false)
    val isTeamBuilderOpen = MutableStateFlow(false)

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showSnackbar(msg: String) {
        _snackbarMessage.value = msg
    }

    // Project Application
    fun applyForProject(
        projectId: String,
        projectTitle: String,
        coverMessage: String,
        relevantSkills: String,
        portfolioLink: String,
        expectedDelivery: String,
        proposedBudget: Int
    ) {
        viewModelScope.launch {
            val success = repository.applyForProject(
                projectId = projectId,
                projectTitle = projectTitle,
                student = _currentUser.value,
                coverMessage = coverMessage,
                relevantSkills = relevantSkills,
                portfolioLink = portfolioLink,
                expectedDelivery = expectedDelivery,
                proposedBudget = proposedBudget
            )
            if (success) {
                _snackbarMessage.value = "Proposal submitted successfully for '$projectTitle'!"
                selectedProjectForApply.value = null
            } else {
                _snackbarMessage.value = "You have already submitted a proposal for this project."
            }
        }
    }

    // Hire Student
    fun hireStudent(proposal: ProposalEntity) {
        viewModelScope.launch {
            repository.hireStudent(
                proposalId = proposal.id,
                projectId = proposal.projectId,
                studentId = proposal.studentId,
                studentName = proposal.studentName
            )
            _snackbarMessage.value = "Hired ${proposal.studentName} for '${proposal.projectTitle}'!"
        }
    }

    // Submit Work
    fun submitProjectWork(proposalId: String, projectId: String, notes: String) {
        viewModelScope.launch {
            repository.submitProjectWork(proposalId, projectId, notes)
            _snackbarMessage.value = "Completed work submitted for client review!"
        }
    }

    // Approve Work and Give Review
    fun approveProjectWork(proposalId: String, projectId: String, rating: Float, reviewComment: String) {
        viewModelScope.launch {
            repository.approveProjectWorkAndReview(proposalId, projectId, rating, reviewComment)
            _snackbarMessage.value = "Project approved & $rating★ review posted to Student Skill Passport!"
        }
    }

    // Post New Project
    fun postNewProject(
        title: String,
        description: String,
        category: String,
        projectType: String,
        skills: String,
        budget: Int,
        deadline: String
    ) {
        viewModelScope.launch {
            val newProject = ProjectEntity(
                id = "proj_${System.currentTimeMillis()}",
                clientId = _currentUser.value.id,
                clientName = _currentUser.value.organization.ifBlank { _currentUser.value.name },
                clientType = if (_currentUser.value.role == "COLLEGE_CLIENT") "College Client" else "Industry Client",
                title = title,
                description = description,
                category = category,
                projectType = projectType,
                requiredSkills = skills,
                budget = budget,
                deadline = deadline,
                proposalCount = 0,
                status = "OPEN"
            )
            repository.postNewProject(newProject)
            _snackbarMessage.value = "Project '$title' posted successfully!"
            isPostProjectOpen.value = false
        }
    }

    // Campus Gig 1-Tap Apply
    fun applyForGig(gig: CampusGigEntity) {
        viewModelScope.launch {
            repository.applyForGig(gig.id)
            _snackbarMessage.value = "Applied for gig '${gig.task}'! Organizer notified."
        }
    }

    // Event Registration
    fun registerForEvent(event: EventEntity) {
        viewModelScope.launch {
            val registered = repository.registerForEvent(event.id)
            if (registered) {
                _snackbarMessage.value = "Registered for '${event.title}'! Pass added to profile."
            } else {
                _snackbarMessage.value = "You are already registered for this event."
            }
        }
    }

    // Send Chat Message
    fun sendChatMessage(projectId: String, projectTitle: String, receiverId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendChatMessage(
                projectId = projectId,
                projectTitle = projectTitle,
                senderId = _currentUser.value.id,
                senderName = _currentUser.value.name,
                receiverId = receiverId,
                message = text
            )
        }
    }

    // Save Assessment
    fun completeAssessment(subject: String, score: Int, skills: String) {
        viewModelScope.launch {
            val result = AssessmentResultEntity(
                id = "assess_${System.currentTimeMillis()}",
                studentId = _currentUser.value.id,
                subject = subject,
                scorePercent = score,
                verifiedSkills = skills
            )
            repository.saveAssessmentResult(result)
            isSkillAssessmentOpen.value = false
            _snackbarMessage.value = "Assessment passed with $score%! Verified skill badge added."
        }
    }

    // User Registration
    fun registerNewUser(
        name: String,
        email: String,
        role: String,
        organization: String,
        department: String,
        skills: String
    ) {
        viewModelScope.launch {
            val newUser = UserEntity(
                id = "user_${System.currentTimeMillis()}",
                name = name,
                email = email,
                role = role,
                organization = organization,
                department = department,
                skills = skills,
                isVerified = true
            )
            repository.insertUser(newUser)
            _currentUser.value = newUser
            isAuthDialogOpen.value = false
            _snackbarMessage.value = "Welcome to Freeverse, $name!"
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }
}
