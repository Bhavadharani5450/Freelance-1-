package com.example.data.repository

import com.example.data.dao.FreeverseDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class FreeverseRepository(private val dao: FreeverseDao) {

    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val allProjects: Flow<List<ProjectEntity>> = dao.getAllProjects()
    val allProposals: Flow<List<ProposalEntity>> = dao.getAllProposals()
    val allGigs: Flow<List<CampusGigEntity>> = dao.getAllGigs()
    val allEvents: Flow<List<EventEntity>> = dao.getAllEvents()
    val allServices: Flow<List<ServiceEntity>> = dao.getAllServices()
    val allNotifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()
    val allChatMessages: Flow<List<ChatMessageEntity>> = dao.getAllChatMessages()
    val allAnnouncements: Flow<List<AnnouncementEntity>> = dao.getAllAnnouncements()
    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val allReports: Flow<List<ReportComplaintEntity>> = dao.getAllReports()

    fun getProposalsForProject(projectId: String): Flow<List<ProposalEntity>> =
        dao.getProposalsByProjectId(projectId)

    fun getProposalsForStudent(studentId: String): Flow<List<ProposalEntity>> =
        dao.getProposalsByStudentId(studentId)

    fun getProjectsByClient(clientId: String): Flow<List<ProjectEntity>> =
        dao.getProjectsByClient(clientId)

    fun getNotificationsForUser(userId: String, roleKey: String): Flow<List<NotificationEntity>> =
        dao.getNotificationsForUser(userId, roleKey)

    fun getChatMessagesForProject(projectId: String): Flow<List<ChatMessageEntity>> =
        dao.getChatMessages(projectId)

    fun getAssessmentsForStudent(studentId: String): Flow<List<AssessmentResultEntity>> =
        dao.getAssessmentsForStudent(studentId)

    suspend fun getUserByEmail(email: String): UserEntity? =
        dao.getUserByEmail(email)

    suspend fun applyForProject(
        projectId: String,
        projectTitle: String,
        student: UserEntity,
        coverMessage: String,
        relevantSkills: String,
        portfolioLink: String,
        expectedDelivery: String,
        proposedBudget: Int
    ): Boolean {
        // Prevent duplicate proposal
        val existing = dao.getProposalsByProjectId(projectId).first()
        if (existing.any { it.studentId == student.id }) {
            return false
        }

        val proposal = ProposalEntity(
            id = "prop_${System.currentTimeMillis()}",
            projectId = projectId,
            projectTitle = projectTitle,
            studentId = student.id,
            studentName = student.name,
            studentRole = student.department + " - " + student.year,
            coverMessage = coverMessage,
            relevantSkills = relevantSkills,
            portfolioLink = portfolioLink,
            expectedDelivery = expectedDelivery,
            proposedBudget = proposedBudget,
            status = "PENDING"
        )
        dao.insertProposal(proposal)

        val project = dao.getProjectById(projectId).first()
        if (project != null) {
            dao.updateProject(project.copy(proposalCount = project.proposalCount + 1))
        }

        // Notify client and admins
        dao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                userId = project?.clientId ?: "CLIENT",
                title = "New Proposal Received",
                message = "${student.name} submitted a proposal for '$projectTitle'",
                type = "APPLICATION",
                timeAgo = "Just now"
            )
        )
        return true
    }

    suspend fun hireStudent(
        proposalId: String,
        projectId: String,
        studentId: String,
        studentName: String
    ) {
        val proposalList = dao.getAllProposals().first()
        val prop = proposalList.find { it.id == proposalId }
        if (prop != null) {
            dao.updateProposal(prop.copy(status = "HIRED"))
        }

        val project = dao.getProjectById(projectId).first()
        if (project != null) {
            dao.updateProject(
                project.copy(
                    status = "IN_PROGRESS",
                    hiredStudentId = studentId,
                    hiredStudentName = studentName,
                    progressPercent = 10
                )
            )
        }

        // Notification for student
        dao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                userId = studentId,
                title = "🎉 You were Hired!",
                message = "Congratulations! You have been awarded the contract for '${project?.title ?: "Project"}'.",
                type = "HIRE",
                timeAgo = "Just now"
            )
        )
    }

    suspend fun submitCompletedWork(
        proposalId: String,
        projectId: String,
        submissionNotes: String
    ) {
        val proposalList = dao.getAllProposals().first()
        val prop = proposalList.find { it.id == proposalId }
        if (prop != null) {
            dao.updateProposal(
                prop.copy(
                    status = "WORK_SUBMITTED",
                    submissionNotes = submissionNotes
                )
            )
        }

        val project = dao.getProjectById(projectId).first()
        if (project != null) {
            dao.updateProject(
                project.copy(
                    status = "WORK_SUBMITTED",
                    progressPercent = 90
                )
            )

            dao.insertNotification(
                NotificationEntity(
                    id = "notif_${System.currentTimeMillis()}",
                    userId = project.clientId,
                    title = "Work Submitted for Review",
                    message = "Freelancer has submitted completed milestone for '${project.title}'.",
                    type = "PROJECT",
                    timeAgo = "Just now"
                )
            )
        }
    }

    suspend fun approveAndCompleteProject(
        proposalId: String,
        projectId: String,
        rating: Float,
        comment: String
    ) {
        val proposalList = dao.getAllProposals().first()
        val prop = proposalList.find { it.id == proposalId }
        if (prop != null) {
            dao.updateProposal(
                prop.copy(
                    status = "COMPLETED",
                    clientRating = rating,
                    clientReviewComment = comment
                )
            )

            // Update user earnings
            val users = dao.getAllUsers().first()
            val student = users.find { it.id == prop.studentId }
            if (student != null) {
                dao.updateUser(
                    student.copy(
                        projectsCompleted = student.projectsCompleted + 1,
                        totalEarnings = student.totalEarnings + prop.proposedBudget
                    )
                )
            }
        }

        val project = dao.getProjectById(projectId).first()
        if (project != null) {
            dao.updateProject(
                project.copy(
                    status = "COMPLETED",
                    progressPercent = 100
                )
            )

            // Record transaction
            dao.insertTransaction(
                TransactionEntity(
                    id = "tx_${System.currentTimeMillis()}",
                    projectId = project.id,
                    projectTitle = project.title,
                    senderName = project.clientName,
                    receiverName = prop?.studentName ?: "Student Freelancer",
                    amount = prop?.proposedBudget ?: project.budget,
                    status = "COMPLETED",
                    date = "Today",
                    invoiceId = "INV-2026-${(1000..9999).random()}"
                )
            )

            dao.insertNotification(
                NotificationEntity(
                    id = "notif_${System.currentTimeMillis()}",
                    userId = prop?.studentId ?: "MEMBER",
                    title = "Payment Released & Review Received! ⭐",
                    message = "Client marked '${project.title}' completed with a $rating-star rating. Funds transferred.",
                    type = "PAYMENT",
                    timeAgo = "Just now"
                )
            )
        }
    }

    suspend fun postProject(
        title: String,
        description: String,
        category: String,
        projectType: String,
        requiredSkills: String,
        budget: Int,
        deadline: String,
        client: UserEntity
    ) {
        val project = ProjectEntity(
            id = "proj_${System.currentTimeMillis()}",
            clientId = client.id,
            clientName = client.name,
            clientType = client.department.ifBlank { "Industry Client" },
            title = title,
            description = description,
            category = category,
            projectType = projectType,
            requiredSkills = requiredSkills,
            budget = budget,
            deadline = deadline,
            proposalCount = 0,
            status = "OPEN"
        )
        dao.insertProject(project)

        dao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                userId = "SUPER_ADMIN",
                title = "New Project Listed",
                message = "${client.name} published project: '$title' (₹$budget)",
                type = "PROJECT",
                timeAgo = "Just now"
            )
        )
    }

    suspend fun createEvent(
        title: String,
        category: String,
        date: String,
        time: String,
        venue: String,
        description: String,
        rules: String,
        coordinatorName: String
    ) {
        val event = EventEntity(
            id = "evt_${System.currentTimeMillis()}",
            title = title,
            category = category,
            date = date,
            time = time,
            venue = venue,
            description = description,
            rules = rules,
            registeredCount = 0,
            createdByCoordinator = coordinatorName
        )
        dao.insertEvent(event)

        dao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                userId = "FACULTY_COORDINATOR",
                title = "New Club Event Created",
                message = "$coordinatorName published '$title' scheduled for $date at $venue.",
                type = "EVENT",
                timeAgo = "Just now"
            )
        )
    }

    suspend fun registerForEvent(eventId: String, user: UserEntity) {
        val events = dao.getAllEvents().first()
        val event = events.find { it.id == eventId }
        if (event != null && !event.isRegistered) {
            dao.updateEvent(event.copy(registeredCount = event.registeredCount + 1, isRegistered = true))
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_${System.currentTimeMillis()}",
                    userId = "CLUB_COORDINATOR",
                    title = "New Event Registration",
                    message = "${user.name} registered for '${event.title}'",
                    type = "EVENT",
                    timeAgo = "Just now"
                )
            )
        }
    }

    suspend fun applyForGig(gig: CampusGigEntity) {
        dao.updateGig(gig.copy(isApplied = true, applicantsCount = gig.applicantsCount + 1))
    }

    suspend fun createService(service: ServiceEntity) {
        dao.insertService(service)
    }

    suspend fun createAnnouncement(announcement: AnnouncementEntity) {
        dao.insertAnnouncement(announcement)
    }

    suspend fun submitReport(report: ReportComplaintEntity) {
        dao.insertReport(report)
        dao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                userId = "SUPER_ADMIN",
                title = "New Platform Report/Complaint",
                message = "From ${report.reporterName}: '${report.subject}'",
                type = "REPORT",
                timeAgo = "Just now"
            )
        )
    }

    suspend fun setUserStatus(userId: String, status: String) {
        dao.setUserAccountStatus(userId, status)
    }

    suspend fun updateUser(user: UserEntity) {
        dao.updateUser(user)
    }

    suspend fun sendMessage(
        projectId: String,
        projectTitle: String,
        sender: UserEntity,
        receiverId: String,
        message: String
    ) {
        val chat = ChatMessageEntity(
            id = "msg_${System.currentTimeMillis()}",
            projectId = projectId,
            projectTitle = projectTitle,
            senderId = sender.id,
            senderName = sender.name,
            receiverId = receiverId,
            message = message
        )
        dao.insertChatMessage(chat)
    }

    suspend fun recordAssessmentResult(
        studentId: String,
        subject: String,
        scorePercent: Int,
        verifiedSkills: String
    ) {
        val result = AssessmentResultEntity(
            id = "assess_${System.currentTimeMillis()}",
            studentId = studentId,
            subject = subject,
            scorePercent = scorePercent,
            verifiedSkills = verifiedSkills
        )
        dao.insertAssessment(result)
    }

    suspend fun markAllNotificationsRead() {
        dao.markAllNotificationsRead()
    }

    suspend fun insertUser(user: UserEntity) {
        dao.insertUser(user)
    }

    suspend fun seedDatabaseIfEmpty() {
        val users = dao.getAllUsers().first()
        if (users.isEmpty()) {
            dao.insertUsers(initialUsers)
            dao.insertProjects(initialProjects)
            dao.insertProposals(initialProposals)
            dao.insertGigs(initialGigs)
            dao.insertEvents(initialEvents)
            dao.insertServices(initialServices)
            dao.insertNotifications(initialNotifications)
            dao.insertChatMessages(initialMessages)
            dao.insertAnnouncements(initialAnnouncements)
            dao.insertTransactions(initialTransactions)
            dao.insertReports(initialReports)
        }
    }

    companion object {
        val salt = "fv_secure_salt_2026"

        val initialUsers = listOf(
            // 1. SUPER ADMIN
            UserEntity(
                id = "user_superadmin",
                name = "Freeverse Super Admin",
                email = "superadmin@freeverse.com",
                role = "SUPER_ADMIN",
                coordinatorDesignation = "Platform Lead Administrator",
                passwordHash = AuthSecurityService.hashPassword("Admin@123", salt),
                passwordSalt = salt,
                accountStatus = "ACTIVE",
                phone = "+91 94432 99000",
                organization = "Kangeyam Institute of Technology",
                department = "Central Administration & Governance",
                year = "Lead Administrator",
                bio = "Executive Super Administrator with highest system-level permissions, role assignments, user governance, payment oversight, and platform settings.",
                skills = "Platform Administration, Role Governance, Security Operations, Financial Controls",
                isVerified = true
            ),

            // 2. FACULTY COORDINATORS
            UserEntity(
                id = "user_faculty_jaishimma",
                name = "Mr. S Jaishimma",
                email = "jaishimma@freeverse.kit.ac.in",
                role = "FACULTY_COORDINATOR",
                coordinatorDesignation = "Faculty Coordinator",
                passwordHash = AuthSecurityService.hashPassword("Faculty@123", salt),
                passwordSalt = salt,
                accountStatus = "ACTIVE",
                phone = "+91 94420 11223",
                organization = "Kangeyam Institute of Technology",
                department = "Department of Computer Science & Engineering",
                year = "Assistant Professor",
                bio = "Official Faculty Coordinator for FREEVERSE Club. Mentoring student engineers in real-world application architecture, ethical freelancing, and incubation projects.",
                skills = "Academic Mentorship, Full-Stack Architecture, Industry Relations, Project Oversight",
                isVerified = true
            ),
            UserEntity(
                id = "user_faculty_nithyadevi",
                name = "Mrs. S Nithya Devi",
                email = "nithyadevi@freeverse.kit.ac.in",
                role = "FACULTY_COORDINATOR",
                coordinatorDesignation = "Faculty Coordinator",
                passwordHash = AuthSecurityService.hashPassword("Faculty@123", salt),
                passwordSalt = salt,
                accountStatus = "ACTIVE",
                phone = "+91 94420 11224",
                organization = "Kangeyam Institute of Technology",
                department = "Department of Information Technology",
                year = "Assistant Professor",
                bio = "Official Faculty Coordinator for FREEVERSE Club. Guiding student hackathons, innovation challenges, and institutional client contracts.",
                skills = "Student Engagement, Hackathon Mentoring, Quality Auditing, Curriculum Integration",
                isVerified = true
            ),

            // 3. CLUB COORDINATORS
            UserEntity(
                id = "user_bhavadharani",
                name = "Bhavadharani S",
                email = "bhavadharani@freeverse.kit.ac.in",
                role = "CLUB_COORDINATOR",
                coordinatorDesignation = "President",
                passwordHash = AuthSecurityService.hashPassword("Coord@123", salt),
                passwordSalt = salt,
                accountStatus = "ACTIVE",
                phone = "+91 94421 88210",
                organization = "Kangeyam Institute of Technology",
                department = "Computer Science & Engineering",
                year = "4th Year",
                bio = "President of FREEVERSE Club. Full-Stack Developer & AI Specialist leading campus technical sprints and student freelance opportunities.",
                skills = "React, Node.js, Kotlin, Python, Gemini AI, Jetpack Compose, System Design",
                hourlyRate = 750,
                rating = 4.95f,
                projectsCompleted = 14,
                totalEarnings = 32500,
                isVerified = true,
                github = "https://github.com/bhavadharani-s",
                linkedin = "https://linkedin.com/in/bhavadharani-s",
                portfolio = "https://bhavadharani.dev",
                availability = "Available for Projects"
            ),
            UserEntity(
                id = "user_nevitha",
                name = "Nevitha S V",
                email = "nevitha@freeverse.kit.ac.in",
                role = "CLUB_COORDINATOR",
                coordinatorDesignation = "Secretary",
                passwordHash = AuthSecurityService.hashPassword("Coord@123", salt),
                passwordSalt = salt,
                accountStatus = "ACTIVE",
                phone = "+91 94883 12345",
                organization = "Kangeyam Institute of Technology",
                department = "Information Technology",
                year = "3rd Year",
                bio = "Secretary of FREEVERSE Club. UI/UX Designer & Frontend Specialist passionate about clean interfaces, design systems, and responsive web experiences.",
                skills = "Figma, UI/UX Design, React, Tailwind CSS, Motion Graphics, Prototyping",
                hourlyRate = 600,
                rating = 4.90f,
                projectsCompleted = 11,
                totalEarnings = 21000,
                isVerified = true,
                github = "https://github.com/nevitha-sv",
                linkedin = "https://linkedin.com/in/nevitha-sv",
                portfolio = "https://nevitha.design",
                availability = "Available for Projects"
            ),
            UserEntity(
                id = "user_keerthana",
                name = "Keerthana B.K",
                email = "keerthana@freeverse.kit.ac.in",
                role = "CLUB_COORDINATOR",
                coordinatorDesignation = "Vice President",
                passwordHash = AuthSecurityService.hashPassword("Coord@123", salt),
                passwordSalt = salt,
                accountStatus = "ACTIVE",
                phone = "+91 97892 45678",
                organization = "Kangeyam Institute of Technology",
                department = "Electronics & Communication",
                year = "4th Year",
                bio = "Vice President of FREEVERSE Club. Cross-platform mobile developer & embedded IoT enthusiast bridging hardware telemetry with cloud dashboards.",
                skills = "Flutter, Android, Dart, Firebase, REST APIs, IoT, Kotlin",
                hourlyRate = 650,
                rating = 4.88f,
                projectsCompleted = 9,
                totalEarnings = 18500,
                isVerified = true,
                github = "https://github.com/keerthana-bk",
                linkedin = "https://linkedin.com/in/keerthana-bk",
                portfolio = "https://keerthana.tech",
                availability = "Available for Projects"
            ),
            UserEntity(
                id = "user_kavyassri",
                name = "KAVYASSRI G",
                email = "kavyassri@freeverse.kit.ac.in",
                role = "CLUB_COORDINATOR",
                coordinatorDesignation = "Joint Secretary",
                passwordHash = AuthSecurityService.hashPassword("Coord@123", salt),
                passwordSalt = salt,
                accountStatus = "ACTIVE",
                phone = "+91 98425 67890",
                organization = "Kangeyam Institute of Technology",
                department = "Computer Science & Engineering",
                year = "3rd Year",
                bio = "Joint Secretary of FREEVERSE Club. Visual designer, brand identity specialist, and digital illustration expert creating cohesive marketing collateral.",
                skills = "Illustrator, Photoshop, Brand Identity, Vector Art, Poster Design, Canva Pro",
                hourlyRate = 500,
                rating = 4.92f,
                projectsCompleted = 16,
                totalEarnings = 22000,
                isVerified = true,
                github = "https://github.com/kavyassri-g",
                linkedin = "https://linkedin.com/in/kavyassri-g",
                portfolio = "https://kavyassri.art",
                availability = "Available for Projects"
            ),
            UserEntity(
                id = "user_dyanidhi",
                name = "Dyanidhi N",
                email = "dyanidhi@freeverse.kit.ac.in",
                role = "CLUB_COORDINATOR",
                coordinatorDesignation = "Treasurer",
                passwordHash = AuthSecurityService.hashPassword("Coord@123", salt),
                passwordSalt = salt,
                accountStatus = "ACTIVE",
                phone = "+91 99430 78912",
                organization = "Kangeyam Institute of Technology",
                department = "Computer Science & Engineering",
                year = "4th Year",
                bio = "Treasurer of FREEVERSE Club. Backend & Cloud Infrastructure engineer specializing in high-throughput databases, microservices, and Docker pipelines.",
                skills = "Spring Boot, PostgreSQL, Docker, Java, AWS, Microservices, SQL",
                hourlyRate = 800,
                rating = 4.91f,
                projectsCompleted = 10,
                totalEarnings = 26000,
                isVerified = true,
                github = "https://github.com/dyanidhi-n",
                linkedin = "https://linkedin.com/in/dyanidhi-n",
                portfolio = "https://dyanidhi.cloud",
                availability = "Available for Projects"
            ),
            UserEntity(
                id = "user_vaishnav",
                name = "Vaishnav M N",
                email = "vaishnav@freeverse.kit.ac.in",
                role = "CLUB_COORDINATOR",
                coordinatorDesignation = "Joint Treasurer",
                passwordHash = AuthSecurityService.hashPassword("Coord@123", salt),
                passwordSalt = salt,
                accountStatus = "ACTIVE",
                phone = "+91 96291 23456",
                organization = "Kangeyam Institute of Technology",
                department = "Information Technology",
                year = "2nd Year",
                bio = "Joint Treasurer of FREEVERSE Club. Video editor, cinematic videographer & motion graphics creator for college symposiums, promotional reels and fests.",
                skills = "Premiere Pro, After Effects, DaVinci Resolve, Video Color Grading, Reels",
                hourlyRate = 550,
                rating = 4.87f,
                projectsCompleted = 13,
                totalEarnings = 19500,
                isVerified = true,
                github = "https://github.com/vaishnav-mn",
                linkedin = "https://linkedin.com/in/vaishnav-mn",
                portfolio = "https://vaishnav.media",
                availability = "Available for Projects"
            ),

            // 4. MEMBERS / FREELANCERS
            UserEntity(
                id = "user_member_rahul",
                name = "Rahul K",
                email = "member@freeverse.com",
                role = "MEMBER",
                passwordHash = AuthSecurityService.hashPassword("Student@123", salt),
                passwordSalt = salt,
                accountStatus = "ACTIVE",
                phone = "+91 98941 23401",
                organization = "Kangeyam Institute of Technology",
                department = "Computer Science & Engineering",
                year = "3rd Year",
                bio = "Student Member & Freelancer. Building responsive web applications with React, Tailwind CSS, and Kotlin microservices.",
                skills = "React, JavaScript, Tailwind, Python, Git",
                hourlyRate = 450,
                rating = 4.85f,
                projectsCompleted = 6,
                totalEarnings = 12000,
                isVerified = true
            ),
            UserEntity(
                id = "user_member_priya",
                name = "Priya M",
                email = "priya@freeverse.kit.ac.in",
                role = "MEMBER",
                passwordHash = AuthSecurityService.hashPassword("Student@123", salt),
                passwordSalt = salt,
                accountStatus = "ACTIVE",
                phone = "+91 98941 23402",
                organization = "Kangeyam Institute of Technology",
                department = "Information Technology",
                year = "3rd Year",
                bio = "Student Member & UI/UX Specialist. Wireframing, user persona research, and prototyping in Figma.",
                skills = "Figma, User Research, Wireframing, Design Systems",
                hourlyRate = 500,
                rating = 4.90f,
                projectsCompleted = 7,
                totalEarnings = 14500,
                isVerified = true
            ),

            // 5. CLIENTS
            UserEntity(
                id = "user_client_main",
                name = "Campus & Industry Client",
                email = "client@freeverse.com",
                role = "CLIENT",
                passwordHash = AuthSecurityService.hashPassword("Client@123", salt),
                passwordSalt = salt,
                accountStatus = "ACTIVE",
                phone = "+91 98401 55667",
                organization = "KIT Academic Departments & Industry Partners",
                department = "Product & Academic Operations",
                year = "Client Partner",
                bio = "Department heads and enterprise clients commissioning student freelance projects, web portals, IoT dashboards and multimedia.",
                skills = "Client Partner, Project Sponsor, Quality Review",
                totalSpent = 38500,
                isVerified = true
            ),
            UserEntity(
                id = "user_client_vikram",
                name = "Vikram Technologies",
                email = "vikram@technologies.in",
                role = "CLIENT",
                passwordHash = AuthSecurityService.hashPassword("Client@123", salt),
                passwordSalt = salt,
                accountStatus = "ACTIVE",
                phone = "+91 98401 99887",
                organization = "Vikram Technologies Pvt Ltd",
                department = "Software Engineering & IoT Solutions",
                year = "Enterprise Client",
                bio = "Regional tech partner hiring skilled student developers for web applications, IoT dashboards and rapid prototyping.",
                skills = "Software Engineering, AI Solutions, Enterprise Client",
                totalSpent = 45000,
                isVerified = true
            )
        )

        val initialProjects = listOf(
            ProjectEntity(
                id = "proj_1",
                clientId = "user_client_main",
                clientName = "KIT CSE Department",
                clientType = "College Department",
                title = "Department Accreditation & Faculty Research Portal",
                description = "Build a responsive web application to track NBA accreditation metrics, faculty research papers, and student achievements with exportable PDF reports.",
                category = "Web Development",
                projectType = "CAMPUS",
                requiredSkills = "React, Node.js, PostgreSQL, Tailwind CSS",
                budget = 8500,
                deadline = "15 Oct 2026",
                proposalCount = 3,
                status = "OPEN"
            ),
            ProjectEntity(
                id = "proj_2",
                clientId = "user_client_vikram",
                clientName = "Vikram Technologies",
                clientType = "Startup / Company",
                title = "Smart Agriculture IoT & Yield Analytics Dashboard",
                description = "Develop a modern responsive dashboard for local Kongu farmers showing soil moisture sensors, weather telemetry, and yield prediction with charts.",
                category = "AI / ML",
                projectType = "INDUSTRY",
                requiredSkills = "React, Python, Gemini AI, Chart.js",
                budget = 14000,
                deadline = "28 Oct 2026",
                proposalCount = 4,
                status = "OPEN"
            ),
            ProjectEntity(
                id = "proj_3",
                clientId = "user_client_main",
                clientName = "KIT Cultural & Media Club",
                clientType = "College Club",
                title = "Annual Fest 'KITECHNO 2026' Teaser Video & Motion Graphics",
                description = "Produce a 90-second high-energy cinematic promo video with 3D logo animation and event countdown highlights for Instagram and campus LED screens.",
                category = "Video Editing",
                projectType = "CAMPUS",
                requiredSkills = "Premiere Pro, After Effects, Sound Design",
                budget = 4500,
                deadline = "05 Oct 2026",
                proposalCount = 2,
                status = "IN_PROGRESS",
                hiredStudentId = "user_vaishnav",
                hiredStudentName = "Vaishnav M N",
                progressPercent = 65
            ),
            ProjectEntity(
                id = "proj_4",
                clientId = "user_client_vikram",
                clientName = "Tirupur Garment Exports",
                clientType = "Local Business",
                title = "E-Commerce Catalog & Brand Identity Redesign",
                description = "Create a minimalist brand guidelines book, logo mark, and mobile-friendly product catalog for sustainable organic cotton garments.",
                category = "UI/UX Design",
                projectType = "INDUSTRY",
                requiredSkills = "Figma, Brand Identity, UI/UX, Illustrator",
                budget = 9000,
                deadline = "12 Nov 2026",
                proposalCount = 5,
                status = "OPEN"
            ),
            ProjectEntity(
                id = "proj_5",
                clientId = "user_client_main",
                clientName = "KIT Training & Placement Cell",
                clientType = "College Department",
                title = "Campus Placement Mock Interview & Skill Gap Tracker",
                description = "Develop an interactive assessment module where students take coding tests and receive automated feedback on resume strength and technical gaps.",
                category = "Web Development",
                projectType = "CAMPUS",
                requiredSkills = "Kotlin, React, SQL, Material 3",
                budget = 6000,
                deadline = "20 Oct 2026",
                proposalCount = 2,
                status = "OPEN"
            )
        )

        val initialProposals = listOf(
            ProposalEntity(
                id = "prop_demo_1",
                projectId = "proj_1",
                projectTitle = "Department Accreditation & Faculty Research Portal",
                studentId = "user_bhavadharani",
                studentName = "Bhavadharani S",
                studentRole = "Computer Science - 4th Year",
                coverMessage = "I have previously developed the Freeverse platform and campus ERP modules. I can deliver a clean React + Node.js portal with role-based access control and PDF export within 12 days.",
                relevantSkills = "React, Node.js, PostgreSQL, Full-Stack",
                portfolioLink = "https://bhavadharani.dev",
                expectedDelivery = "12 Days",
                proposedBudget = 8000,
                status = "PENDING"
            ),
            ProposalEntity(
                id = "prop_demo_2",
                projectId = "proj_3",
                projectTitle = "Annual Fest 'KITECHNO 2026' Teaser Video & Motion Graphics",
                studentId = "user_vaishnav",
                studentName = "Vaishnav M N",
                studentRole = "Information Technology - 2nd Year",
                coverMessage = "Edited previous year's symposium teaser with over 15k views. I will provide 4K motion graphics with custom cinematic sound grading.",
                relevantSkills = "Premiere Pro, After Effects, Sound Design",
                portfolioLink = "https://vaishnav.media",
                expectedDelivery = "5 Days",
                proposedBudget = 4500,
                status = "HIRED"
            )
        )

        val initialGigs = listOf(
            CampusGigEntity(
                id = "gig_1",
                task = "Design Technical Symposium Poster",
                clubOrDepartment = "KIT CSI Student Branch",
                reward = 600,
                deadline = "In 2 Days",
                skills = "Photoshop, Canva, Typography",
                applicantsCount = 4
            ),
            CampusGigEntity(
                id = "gig_2",
                task = "Campus Event Photography (Half Day)",
                clubOrDepartment = "KIT Alumni Association",
                reward = 1200,
                deadline = "This Saturday",
                skills = "DSLR, Event Coverage, Lightroom",
                applicantsCount = 2
            ),
            CampusGigEntity(
                id = "gig_3",
                task = "Fix Mobile Navbar & Responsiveness",
                clubOrDepartment = "KIT Innovation & Robotics Club",
                reward = 500,
                deadline = "Tomorrow",
                skills = "HTML, CSS, JavaScript, React",
                applicantsCount = 5
            ),
            CampusGigEntity(
                id = "gig_4",
                task = "Club Instagram Reels Video Editing",
                clubOrDepartment = "KIT Rotaract Club",
                reward = 700,
                deadline = "In 3 Days",
                skills = "CapCut, Premiere Pro, Beat Sync",
                applicantsCount = 6
            ),
            CampusGigEntity(
                id = "gig_5",
                task = "Technical Presentation Slide Deck (15 Slides)",
                clubOrDepartment = "KIT Mechanical Dept",
                reward = 800,
                deadline = "In 4 Days",
                skills = "PowerPoint, Figma, Visual Design",
                applicantsCount = 3
            )
        )

        val initialEvents = listOf(
            EventEntity(
                id = "evt_1",
                title = "Campus UI/UX Design Jam",
                category = "Hackathon",
                date = "21 August 2026",
                time = "3:00 PM - 5:00 PM",
                venue = "Central Computing Lab 3",
                description = "Turn creative ideas into interactive prototypes! Design high-impact web and mobile user journeys. Individual or team participation.",
                rules = "1. Original artwork only. 2. Software allowed: Figma, Illustrator. 3. Submit source links before 5:00 PM.",
                registeredCount = 48,
                isRegistered = false,
                isPast = false,
                createdByCoordinator = "Bhavadharani S"
            ),
            EventEntity(
                id = "evt_2",
                title = "Full-Stack Freelancing Masterclass",
                category = "Workshop",
                date = "02 October 2026",
                time = "10:00 AM - 1:00 PM",
                venue = "Campus Auditorium",
                description = "Learn how to land high-paying freelancing projects, negotiate contracts, and create production web apps with React and Jetpack Compose.",
                rules = "Open to all engineering students. Bring your laptops. Certificates provided.",
                registeredCount = 112,
                isRegistered = true,
                isPast = false,
                createdByCoordinator = "Nevitha S V"
            ),
            EventEntity(
                id = "evt_3",
                title = "24-Hour Web App Challenge",
                category = "Hackathon",
                date = "18 October 2026",
                time = "9:00 AM (24 Hours)",
                venue = "KIT Incubation Center & Online",
                description = "Build a complete campus-first web application within 24 hours. Prize pool: ₹10,000 + Internship opportunities!",
                rules = "Teams of 1 to 4 students. Git commits must be within 24-hour window. Real API integration mandatory.",
                registeredCount = 64,
                isRegistered = false,
                isPast = false,
                createdByCoordinator = "Dyanidhi N"
            ),
            EventEntity(
                id = "evt_4",
                title = "UI/UX Design Sprint & Figma Jam",
                category = "Technical",
                date = "15 September 2026",
                time = "2:00 PM - 5:00 PM",
                venue = "KIT Seminar Hall B",
                description = "Hands-on design thinking, wireframing, and interactive prototyping session with industry alumni mentors.",
                rules = "Past event completed with 85 participants.",
                registeredCount = 85,
                isRegistered = false,
                isPast = true,
                createdByCoordinator = "KAVYASSRI G"
            )
        )

        val initialServices = listOf(
            ServiceEntity(
                id = "srv_1",
                title = "Full-Stack Responsive Website Development",
                category = "Web Development",
                freelancerId = "user_bhavadharani",
                freelancerName = "Bhavadharani S",
                freelancerRole = "President • CSE 4th Year",
                startingPrice = 3000,
                deliveryDays = 5,
                rating = 4.95f,
                reviewsCount = 18,
                description = "Production-grade responsive website with React, Tailwind CSS, clean components, and backend API integration."
            ),
            ServiceEntity(
                id = "srv_2",
                title = "Modern Brand Identity & Vector Logo Design",
                category = "Design",
                freelancerId = "user_kavyassri",
                freelancerName = "KAVYASSRI G",
                freelancerRole = "Joint Secretary • CSE 3rd Year",
                startingPrice = 1200,
                deliveryDays = 3,
                rating = 4.92f,
                reviewsCount = 24,
                description = "Unique vector logo design, custom color palette, brand guidelines, and high-resolution vector assets."
            ),
            ServiceEntity(
                id = "srv_3",
                title = "Mobile App UI/UX Design in Figma",
                category = "UI/UX Design",
                freelancerId = "user_nevitha",
                freelancerName = "Nevitha S V",
                freelancerRole = "Secretary • IT 3rd Year",
                startingPrice = 2500,
                deliveryDays = 4,
                rating = 4.90f,
                reviewsCount = 14,
                description = "Complete Material 3 mobile app interface designs with interactive prototypes and developer design tokens."
            ),
            ServiceEntity(
                id = "srv_4",
                title = "Cinematic Event Teaser & Reels Editing",
                category = "Video Editing",
                freelancerId = "user_vaishnav",
                freelancerName = "Vaishnav M N",
                freelancerRole = "Joint Treasurer • IT 2nd Year",
                startingPrice = 1500,
                deliveryDays = 2,
                rating = 4.88f,
                reviewsCount = 16,
                description = "Fast-paced cinematic video editing, motion graphics titles, sound design, and color grading for events & clubs."
            ),
            ServiceEntity(
                id = "srv_5",
                title = "Cross-Platform Flutter & Android Apps",
                category = "App Development",
                freelancerId = "user_keerthana",
                freelancerName = "Keerthana B.K",
                freelancerRole = "Vice President • ECE 4th Year",
                startingPrice = 4000,
                deliveryDays = 7,
                rating = 4.89f,
                reviewsCount = 11,
                description = "Smooth 60fps Flutter and native Android mobile apps with local database and cloud API sync."
            )
        )

        val initialNotifications = listOf(
            NotificationEntity(
                id = "notif_super_1",
                userId = "SUPER_ADMIN",
                title = "System Security Status Normal",
                message = "All 5 role security endpoints and database hashes verified.",
                type = "SYSTEM",
                timeAgo = "5 mins ago"
            ),
            NotificationEntity(
                id = "notif_faculty_1",
                userId = "FACULTY_COORDINATOR",
                title = "Event Registrations Reached 48",
                message = "Campus UI/UX Design Jam registration count is increasing. Review attendees list.",
                type = "EVENT",
                timeAgo = "30 mins ago"
            ),
            NotificationEntity(
                id = "notif_coord_1",
                userId = "CLUB_COORDINATOR",
                title = "New Proposal on Campus Portal",
                message = "Bhavadharani S submitted a proposal for the Department Accreditation Portal.",
                type = "APPLICATION",
                timeAgo = "1 hour ago"
            ),
            NotificationEntity(
                id = "notif_member_1",
                userId = "MEMBER",
                title = "New Project in Web Development",
                message = "KIT CSE Department posted 'Department Accreditation Portal' (Budget ₹8,500).",
                type = "PROJECT",
                timeAgo = "2 hours ago"
            ),
            NotificationEntity(
                id = "notif_client_1",
                userId = "CLIENT",
                title = "Proposal Received",
                message = "A student freelancer applied to your active project.",
                type = "APPLICATION",
                timeAgo = "2 hours ago"
            )
        )

        val initialMessages = listOf(
            ChatMessageEntity(
                id = "msg_1",
                projectId = "proj_3",
                projectTitle = "KITECHNO 2026 Fest Teaser",
                senderId = "user_client_main",
                senderName = "KIT Cultural Club",
                receiverId = "user_vaishnav",
                message = "Hi Vaishnav, we have uploaded the raw drone shots and fest theme music. Let us know your timeline!",
                timestamp = System.currentTimeMillis() - 3600000
            ),
            ChatMessageEntity(
                id = "msg_2",
                projectId = "proj_3",
                projectTitle = "KITECHNO 2026 Fest Teaser",
                senderId = "user_vaishnav",
                senderName = "Vaishnav M N",
                receiverId = "user_client_main",
                message = "Got the assets! First 30s draft will be ready by tomorrow evening with the 3D logo reveal.",
                timestamp = System.currentTimeMillis() - 1800000
            )
        )

        val initialAnnouncements = listOf(
            AnnouncementEntity(
                id = "ann_1",
                title = "FREEVERSE Hackathon Sprint 2026 Announced",
                content = "All student members are invited to register for the upcoming 24-Hour Web Challenge. Cash prizes and direct client contracts will be awarded.",
                authorName = "Bhavadharani S",
                authorRole = "President • FREEVERSE Club",
                date = "24 Aug 2026",
                priority = "HIGH"
            ),
            AnnouncementEntity(
                id = "ann_2",
                title = "Faculty Mentorship Hours Available",
                content = "Mr. S Jaishimma and Mrs. S Nithya Devi will be holding technical office hours every Wednesday 3-5 PM in Central Computing Lab 3.",
                authorName = "Mr. S Jaishimma",
                authorRole = "Faculty Coordinator",
                date = "22 Aug 2026",
                priority = "NORMAL"
            )
        )

        val initialTransactions = listOf(
            TransactionEntity(
                id = "tx_101",
                projectId = "proj_3",
                projectTitle = "Annual Fest 'KITECHNO 2026' Teaser Video",
                senderName = "KIT Cultural & Media Club",
                receiverName = "Vaishnav M N",
                amount = 4500,
                status = "ESCROW_HELD",
                date = "23 Aug 2026",
                invoiceId = "INV-2026-4891"
            ),
            TransactionEntity(
                id = "tx_102",
                projectId = "proj_1",
                projectTitle = "Department Accreditation Portal (Milestone 1)",
                senderName = "KIT CSE Department",
                receiverName = "Bhavadharani S",
                amount = 8000,
                status = "COMPLETED",
                date = "18 Aug 2026",
                invoiceId = "INV-2026-3720"
            )
        )

        val initialReports = listOf(
            ReportComplaintEntity(
                id = "rep_1",
                reporterName = "Vikram Technologies",
                reporterRole = "CLIENT",
                targetType = "PROJECT",
                subject = "Request for Milestone Clarification",
                description = "Need review on deliverables format for IoT telemetry integration schema.",
                status = "RESOLVED",
                date = "20 Aug 2026"
            )
        )
    }
}
