package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.AuthResult
import com.example.data.repository.AuthSecurityService
import com.example.data.repository.AuthSession
import com.example.data.repository.FreeverseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ScreenNav(val title: String, val routePath: String) {
    HOME("Home", "/"),
    FREELANCERS("Freelancers", "/freelancers"),
    PROJECTS("Projects", "/projects"),
    SERVICES("Services", "/services"),
    GIGS("Campus Gigs", "/gigs"),
    EVENTS("Events", "/events"),
    ABOUT_KIT("About KIT", "/about"),
    MESSAGES("Messages", "/messages"),

    // Login Hub & Dedicated Portals
    LOGIN_HUB("Login Hub", "/login"),
    LOGIN_ADMIN("Super Admin Login", "/login/admin"),
    LOGIN_FACULTY("Faculty Coordinator Login", "/login/faculty"),
    LOGIN_COORDINATOR("Club Coordinator Login", "/login/coordinator"),
    LOGIN_MEMBER("Member / Freelancer Login", "/login/member"),
    LOGIN_CLIENT("Client Login", "/login/client"),

    // 5 Dedicated Role Dashboards
    SUPER_ADMIN_DASHBOARD("Super Admin Dashboard", "/admin/dashboard"),
    FACULTY_DASHBOARD("Faculty Dashboard", "/faculty/dashboard"),
    COORDINATOR_DASHBOARD("Club Coordinator Dashboard", "/coordinator/dashboard"),
    MEMBER_DASHBOARD("Member Dashboard", "/member/dashboard"),
    CLIENT_DASHBOARD("Client Dashboard", "/client/dashboard"),

    // Access Restricted Guard
    ACCESS_RESTRICTED("Access Restricted", "/unauthorized")
}

data class AccessRestrictionInfo(
    val targetScreen: ScreenNav,
    val requiredRole: UserRole,
    val userRole: UserRole,
    val reason: String
)

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

    // Navigation History for back navigation
    private val navigationHistory = mutableListOf<ScreenNav>()

    // Access Restriction Info
    private val _restrictionInfo = MutableStateFlow<AccessRestrictionInfo?>(null)
    val restrictionInfo: StateFlow<AccessRestrictionInfo?> = _restrictionInfo.asStateFlow()

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

    val allAnnouncements: StateFlow<List<AnnouncementEntity>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<ReportComplaintEntity>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Authentication State - Starts Unauthenticated for Public Landing Page
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _currentSession = MutableStateFlow<AuthSession?>(null)
    val currentSession: StateFlow<AuthSession?> = _currentSession.asStateFlow()

    val isAuthenticated: StateFlow<Boolean> = _currentUser.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Logout confirmation modal state
    val isLogoutConfirmOpen = MutableStateFlow(false)

    // Coordinator Dual Mode
    private val _coordinatorMode = MutableStateFlow(CoordinatorMode.COORDINATOR)
    val coordinatorMode: StateFlow<CoordinatorMode> = _coordinatorMode.asStateFlow()

    fun setCoordinatorMode(mode: CoordinatorMode) {
        _coordinatorMode.value = mode
        _snackbarMessage.value = if (mode == CoordinatorMode.COORDINATOR) {
            "Switched to Coordinator Mode (Event & Club Management)"
        } else {
            "Switched to Freelancer Mode (Browse & Submit Work)"
        }
    }

    // SnackBar message
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun showSnackbar(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // Role-Based Route Guard & Navigation
    fun navigateTo(screen: ScreenNav) {
        val user = _currentUser.value

        // Rule: If already logged in, do not show login selector or login forms -> auto-redirect to own dashboard
        if (user != null && (screen == ScreenNav.LOGIN_HUB || screen.name.startsWith("LOGIN_"))) {
            goToMyDashboard()
            return
        }

        // Check protected role routes
        val requiredRole: UserRole? = when (screen) {
            ScreenNav.SUPER_ADMIN_DASHBOARD -> UserRole.SUPER_ADMIN
            ScreenNav.FACULTY_DASHBOARD -> UserRole.FACULTY_COORDINATOR
            ScreenNav.COORDINATOR_DASHBOARD -> UserRole.CLUB_COORDINATOR
            ScreenNav.MEMBER_DASHBOARD -> UserRole.MEMBER
            ScreenNav.CLIENT_DASHBOARD -> UserRole.CLIENT
            else -> null
        }

        if (requiredRole != null) {
            if (user == null) {
                // Not authenticated -> open login hub
                _snackbarMessage.value = "Please log in to access the ${requiredRole.displayName} portal."
                openLoginForRole(requiredRole)
                return
            }

            val userRole = UserRole.fromKey(user.role)
            if (requiredRole != userRole) {
                // Authorization failure -> route to Access Restricted
                _restrictionInfo.value = AccessRestrictionInfo(
                    targetScreen = screen,
                    requiredRole = requiredRole,
                    userRole = userRole,
                    reason = "This area (${screen.routePath}) is restricted to ${requiredRole.displayName} accounts only."
                )
                navigationHistory.add(_currentScreen.value)
                _currentScreen.value = ScreenNav.ACCESS_RESTRICTED
                return
            }
        }

        navigationHistory.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack() {
        if (navigationHistory.isNotEmpty()) {
            val prev = navigationHistory.removeAt(navigationHistory.size - 1)
            _currentScreen.value = prev
        } else {
            _currentScreen.value = ScreenNav.HOME
        }
    }

    fun goToMyDashboard() {
        val user = _currentUser.value ?: run {
            _currentScreen.value = ScreenNav.LOGIN_HUB
            return
        }
        val userRole = UserRole.fromKey(user.role)
        when (userRole) {
            UserRole.SUPER_ADMIN -> _currentScreen.value = ScreenNav.SUPER_ADMIN_DASHBOARD
            UserRole.FACULTY_COORDINATOR -> _currentScreen.value = ScreenNav.FACULTY_DASHBOARD
            UserRole.CLUB_COORDINATOR -> _currentScreen.value = ScreenNav.COORDINATOR_DASHBOARD
            UserRole.MEMBER -> _currentScreen.value = ScreenNav.MEMBER_DASHBOARD
            UserRole.CLIENT -> _currentScreen.value = ScreenNav.CLIENT_DASHBOARD
        }
    }

    /**
     * Backend Secure Login with Role Verification
     */
    suspend fun loginWithRole(
        email: String,
        pass: String,
        expectedRole: UserRole
    ): AuthResult {
        val cleanEmail = email.trim().lowercase()
        // Query user from database
        val user = allUsers.value.find { it.email.equals(cleanEmail, ignoreCase = true) }
            ?: repository.getUserByEmail(cleanEmail)

        val result = AuthSecurityService.authenticate(user, pass, expectedRole)
        if (result is AuthResult.Success) {
            _currentUser.value = result.user
            _currentSession.value = result.session
            _snackbarMessage.value = "Welcome, ${result.user.name}! Authenticated as ${expectedRole.displayName}."
            // Navigate directly to their role's dashboard
            goToMyDashboard()
        }
        return result
    }

    // Dashboard Tab States
    val selectedAdminTab = MutableStateFlow(0) // Index of AdminTab
    val selectedFacultyTab = MutableStateFlow(0) // Index of FacultyTab
    val selectedCoordinatorTab = MutableStateFlow(0) // Index of CoordinatorTab
    val selectedMemberTab = MutableStateFlow(0) // Index of MemberTab
    val selectedClientTab = MutableStateFlow(0) // Index of ClientTab

    fun setAdminTab(index: Int) {
        selectedAdminTab.value = index
        navigateTo(ScreenNav.SUPER_ADMIN_DASHBOARD)
    }

    fun setFacultyTab(index: Int) {
        selectedFacultyTab.value = index
        navigateTo(ScreenNav.FACULTY_DASHBOARD)
    }

    fun setCoordinatorTab(index: Int) {
        selectedCoordinatorTab.value = index
        navigateTo(ScreenNav.COORDINATOR_DASHBOARD)
    }

    fun setMemberTab(index: Int) {
        selectedMemberTab.value = index
        navigateTo(ScreenNav.MEMBER_DASHBOARD)
    }

    fun setClientTab(index: Int) {
        selectedClientTab.value = index
        navigateTo(ScreenNav.CLIENT_DASHBOARD)
    }

    /**
     * Logout and return to Public Landing Page
     */
    fun logout() {
        AuthSecurityService.logout(_currentSession.value?.token)
        _currentSession.value = null
        _currentUser.value = null
        _snackbarMessage.value = "You have been securely logged out."
        _currentScreen.value = ScreenNav.HOME
    }

    // Direct Login Portal Navigation
    fun openLoginForRole(role: UserRole) {
        when (role) {
            UserRole.SUPER_ADMIN -> navigateTo(ScreenNav.LOGIN_ADMIN)
            UserRole.FACULTY_COORDINATOR -> navigateTo(ScreenNav.LOGIN_FACULTY)
            UserRole.CLUB_COORDINATOR -> navigateTo(ScreenNav.LOGIN_COORDINATOR)
            UserRole.MEMBER -> navigateTo(ScreenNav.LOGIN_MEMBER)
            UserRole.CLIENT -> navigateTo(ScreenNav.LOGIN_CLIENT)
        }
    }

    // Role-specific filtered notifications
    val roleNotifications: StateFlow<List<NotificationEntity>> = combine(
        allNotifications,
        currentUser
    ) { notifications, user ->
        if (user == null) {
            emptyList()
        } else {
            val roleKey = user.role
            notifications.filter {
                it.userId == user.id || it.userId == roleKey || it.userId == "SUPER_ADMIN" && roleKey == "SUPER_ADMIN"
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Controls
    fun toggleUserStatus(user: UserEntity) {
        viewModelScope.launch {
            val newStatus = if (user.accountStatus == "ACTIVE") "DEACTIVATED" else "ACTIVE"
            repository.setUserStatus(user.id, newStatus)
            _snackbarMessage.value = "User ${user.name} status changed to $newStatus"
        }
    }

    fun updateUserRole(user: UserEntity, newRole: UserRole) {
        viewModelScope.launch {
            repository.updateUser(user.copy(role = newRole.roleKey))
            _snackbarMessage.value = "Updated role for ${user.name} to ${newRole.displayName}"
        }
    }

    // Post Project
    fun postProject(
        title: String,
        description: String,
        category: String,
        projectType: String,
        skills: String,
        budget: Int,
        deadline: String
    ) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.postProject(
                title = title,
                description = description,
                category = category,
                projectType = projectType,
                requiredSkills = skills,
                budget = budget,
                deadline = deadline,
                client = user
            )
            _snackbarMessage.value = "Project '$title' posted successfully!"
        }
    }

    // Create Event
    fun createEvent(
        title: String,
        category: String,
        date: String,
        time: String,
        venue: String,
        desc: String,
        rules: String
    ) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.createEvent(
                title = title,
                category = category,
                date = date,
                time = time,
                venue = venue,
                description = desc,
                rules = rules,
                coordinatorName = user.name
            )
            _snackbarMessage.value = "Event '$title' published to FREEVERSE campus calendar!"
        }
    }

    // Create Announcement
    fun postAnnouncement(title: String, content: String, priority: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.createAnnouncement(
                AnnouncementEntity(
                    id = "ann_${System.currentTimeMillis()}",
                    title = title,
                    content = content,
                    authorName = user.name,
                    authorRole = user.coordinatorDesignation ?: user.role,
                    date = "Today",
                    priority = priority
                )
            )
            _snackbarMessage.value = "Club announcement published!"
        }
    }

    // Submit Report / Complaint
    fun submitReport(subject: String, targetType: String, description: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.submitReport(
                ReportComplaintEntity(
                    id = "rep_${System.currentTimeMillis()}",
                    reporterName = user.name,
                    reporterRole = user.role,
                    targetType = targetType,
                    subject = subject,
                    description = description,
                    status = "OPEN",
                    date = "Today"
                )
            )
            _snackbarMessage.value = "Report submitted to Super Admin oversight team."
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
            _snackbarMessage.value = "Hired ${proposal.studentName} for the project!"
        }
    }

    // Submit Work
    fun submitCompletedWork(proposal: ProposalEntity, notes: String) {
        viewModelScope.launch {
            repository.submitCompletedWork(proposal.id, proposal.projectId, notes)
            _snackbarMessage.value = "Work deliverables submitted to client for approval!"
        }
    }

    // Approve Project & Release Payment
    fun approveProject(proposal: ProposalEntity, rating: Float, comment: String) {
        viewModelScope.launch {
            repository.approveAndCompleteProject(proposal.id, proposal.projectId, rating, comment)
            _snackbarMessage.value = "Project approved & ₹${proposal.proposedBudget} payment released!"
        }
    }

    // Apply for project
    fun submitProposal(
        projectId: String,
        projectTitle: String,
        coverMessage: String,
        skills: String,
        portfolioLink: String,
        delivery: String,
        budget: Int
    ) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val success = repository.applyForProject(
                projectId = projectId,
                projectTitle = projectTitle,
                student = user,
                coverMessage = coverMessage,
                relevantSkills = skills,
                portfolioLink = portfolioLink,
                expectedDelivery = delivery,
                proposedBudget = budget
            )
            if (success) {
                _snackbarMessage.value = "Proposal submitted successfully!"
            } else {
                _snackbarMessage.value = "You have already applied for this project."
            }
        }
    }

    fun applyForProject(
        projectId: String,
        projectTitle: String,
        coverMessage: String,
        relevantSkills: String,
        portfolioLink: String,
        expectedDelivery: String,
        proposedBudget: Int
    ) {
        submitProposal(projectId, projectTitle, coverMessage, relevantSkills, portfolioLink, expectedDelivery, proposedBudget)
    }

    fun postNewProject(
        title: String,
        description: String,
        category: String,
        projectType: String,
        skills: String,
        budget: Int,
        deadline: String
    ) {
        postProject(title, description, category, projectType, skills, budget, deadline)
    }

    fun completeAssessment(subject: String, score: Int, skills: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.recordAssessmentResult(user.id, subject, score, skills)
            _snackbarMessage.value = "Assessment submitted! Skill passport verified."
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
            _snackbarMessage.value = "All notifications marked as read."
        }
    }

    fun sendChatMessage(projectId: String, projectTitle: String, receiverId: String, message: String) {
        sendMessage(projectId, projectTitle, receiverId, message)
    }

    // Apply for gig
    fun applyForGig(gig: CampusGigEntity) {
        viewModelScope.launch {
            repository.applyForGig(gig)
            _snackbarMessage.value = "Applied for campus gig: ${gig.task}"
        }
    }

    // Register for event
    fun registerForEvent(event: EventEntity) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.registerForEvent(event.id, user)
            _snackbarMessage.value = "Registered for ${event.title}!"
        }
    }

    // Create Service
    fun createService(title: String, category: String, price: Int, days: Int, desc: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.createService(
                ServiceEntity(
                    id = "srv_${System.currentTimeMillis()}",
                    title = title,
                    category = category,
                    freelancerId = user.id,
                    freelancerName = user.name,
                    freelancerRole = user.department + " • " + (user.coordinatorDesignation ?: user.year),
                    startingPrice = price,
                    deliveryDays = days,
                    description = desc
                )
            )
            _snackbarMessage.value = "Service gig '$title' published to marketplace!"
        }
    }

    // Send chat message
    fun sendMessage(projectId: String, projectTitle: String, receiverId: String, message: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.sendMessage(projectId, projectTitle, user, receiverId, message)
        }
    }

    // Dialog state controllers
    val isNotificationsOpen = MutableStateFlow(false)
    val isPostProjectOpen = MutableStateFlow(false)
    val isSkillAssessmentOpen = MutableStateFlow(false)
    val isTeamBuilderOpen = MutableStateFlow(false)
    val selectedFreelancerForProfile = MutableStateFlow<UserEntity?>(null)
    val selectedProjectForApply = MutableStateFlow<ProjectEntity?>(null)

    // Freelancer search & filters
    val freelancerSearchQuery = MutableStateFlow("")
    val freelancerSelectedSkill = MutableStateFlow("All")
    val freelancerSelectedSort = MutableStateFlow("Recommended")

    val filteredFreelancers: StateFlow<List<UserEntity>> = combine(
        allUsers,
        freelancerSearchQuery,
        freelancerSelectedSkill,
        freelancerSelectedSort
    ) { users, query, skill, sort ->
        var list = users.filter { it.role == "MEMBER" || it.role == "CLUB_COORDINATOR" || it.role == "STUDENT" }
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

    // Project search & filters
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
                it.description.contains(query, ignoreCase = true) ||
                it.requiredSkills.contains(query, ignoreCase = true) ||
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
}
