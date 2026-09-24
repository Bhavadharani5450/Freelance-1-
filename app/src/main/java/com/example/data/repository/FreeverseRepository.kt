package com.example.data.repository

import com.example.data.dao.FreeverseDao
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

    fun getProposalsForProject(projectId: String): Flow<List<ProposalEntity>> =
        dao.getProposalsByProjectId(projectId)

    fun getProposalsForStudent(studentId: String): Flow<List<ProposalEntity>> =
        dao.getProposalsByStudentId(studentId)

    fun getChatMessagesForProject(projectId: String): Flow<List<ChatMessageEntity>> =
        dao.getChatMessages(projectId)

    fun getAssessmentsForStudent(studentId: String): Flow<List<AssessmentResultEntity>> =
        dao.getAssessmentsForStudent(studentId)

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

        // Update proposal count in project
        val project = dao.getProjectById(projectId).first()
        if (project != null) {
            dao.updateProject(project.copy(proposalCount = project.proposalCount + 1))
        }

        // Add notification
        dao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                userId = "client",
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
                    progressPercent = 25
                )
            )
        }

        dao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                userId = studentId,
                title = "Congratulations! You are Hired",
                message = "You have been officially hired for '${project?.title ?: "Project"}'. Workspace is now active.",
                type = "HIRE",
                timeAgo = "Just now"
            )
        )
    }

    suspend fun submitProjectWork(proposalId: String, projectId: String, notes: String) {
        val proposalList = dao.getAllProposals().first()
        val prop = proposalList.find { it.id == proposalId }
        if (prop != null) {
            dao.updateProposal(prop.copy(status = "WORK_SUBMITTED", submissionNotes = notes))
        }

        val project = dao.getProjectById(projectId).first()
        if (project != null) {
            dao.updateProject(project.copy(status = "WORK_SUBMITTED", progressPercent = 90))
        }

        dao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                userId = "client",
                title = "Work Submitted for Review",
                message = "Completed work has been submitted for '${project?.title}'. Review and approve now.",
                type = "PROJECT",
                timeAgo = "Just now"
            )
        )
    }

    suspend fun approveProjectWorkAndReview(
        proposalId: String,
        projectId: String,
        rating: Float,
        reviewComment: String
    ) {
        val proposalList = dao.getAllProposals().first()
        val prop = proposalList.find { it.id == proposalId }
        if (prop != null) {
            dao.updateProposal(
                prop.copy(
                    status = "COMPLETED",
                    clientRating = rating,
                    clientReviewComment = reviewComment
                )
            )
            // Update student project completed count & rating
            val student = dao.getUserById(prop.studentId).first()
            if (student != null) {
                val newProjects = student.projectsCompleted + 1
                val newRating = ((student.rating * student.projectsCompleted) + rating) / newProjects
                dao.updateUser(
                    student.copy(
                        projectsCompleted = newProjects,
                        rating = String.format("%.1f", newRating).toFloat()
                    )
                )
            }
        }

        val project = dao.getProjectById(projectId).first()
        if (project != null) {
            dao.updateProject(project.copy(status = "COMPLETED", progressPercent = 100))
        }

        dao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                userId = prop?.studentId ?: "student",
                title = "Project Approved & Reviewed!",
                message = "The client approved your work with a $rating★ review. Added to your Skill Passport!",
                type = "REVIEW",
                timeAgo = "Just now"
            )
        )
    }

    suspend fun submitStudentReview(proposalId: String, reviewComment: String) {
        val proposalList = dao.getAllProposals().first()
        val prop = proposalList.find { it.id == proposalId }
        if (prop != null) {
            dao.updateProposal(prop.copy(studentReviewComment = reviewComment))
        }
    }

    suspend fun postNewProject(project: ProjectEntity) {
        dao.insertProject(project)
        dao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                userId = "student",
                title = "New Project Posted",
                message = "New opportunity: '${project.title}' (${project.projectType}) with budget ₹${project.budget}",
                type = "PROJECT",
                timeAgo = "Just now"
            )
        )
    }

    suspend fun applyForGig(gigId: String) {
        val gigs = dao.getAllGigs().first()
        val gig = gigs.find { it.id == gigId }
        if (gig != null && !gig.isApplied) {
            dao.updateGig(gig.copy(isApplied = true, applicantsCount = gig.applicantsCount + 1))
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_${System.currentTimeMillis()}",
                    userId = "student",
                    title = "Campus Gig Applied",
                    message = "Successfully applied for '${gig.task}' organized by ${gig.clubOrDepartment}",
                    type = "APPLICATION",
                    timeAgo = "Just now"
                )
            )
        }
    }

    suspend fun registerForEvent(eventId: String): Boolean {
        val events = dao.getAllEvents().first()
        val event = events.find { it.id == eventId }
        if (event != null && !event.isRegistered) {
            dao.updateEvent(event.copy(isRegistered = true, registeredCount = event.registeredCount + 1))
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_${System.currentTimeMillis()}",
                    userId = "student",
                    title = "Event Registered!",
                    message = "You are confirmed for '${event.title}' on ${event.date}. Ticket saved to profile.",
                    type = "EVENT",
                    timeAgo = "Just now"
                )
            )
            return true
        }
        return false
    }

    suspend fun sendChatMessage(
        projectId: String,
        projectTitle: String,
        senderId: String,
        senderName: String,
        receiverId: String,
        message: String
    ) {
        dao.insertChatMessage(
            ChatMessageEntity(
                id = "msg_${System.currentTimeMillis()}",
                projectId = projectId,
                projectTitle = projectTitle,
                senderId = senderId,
                senderName = senderName,
                receiverId = receiverId,
                message = message,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun saveAssessmentResult(result: AssessmentResultEntity) {
        dao.insertAssessment(result)
        // Add skill verification to user
        val student = dao.getUserById(result.studentId).first()
        if (student != null) {
            dao.updateUser(student.copy(isVerified = true))
        }
        dao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                userId = result.studentId,
                title = "Skill Assessment Passed!",
                message = "Scored ${result.scorePercent}% on ${result.subject}. 'Assessment Verified' badge added to Skill Passport!",
                type = "REVIEW",
                timeAgo = "Just now"
            )
        )
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
        }
    }

    // Initial realistic demo seeds
    companion object {
        val initialUsers = listOf(
            UserEntity(
                id = "user_student",
                name = "Bhavadharani S",
                email = "student@freeverse.com",
                role = "STUDENT",
                phone = "+91 94421 88210",
                organization = "Kangeyam Institute of Technology",
                department = "Computer Science & Engineering",
                year = "4th Year",
                bio = "President of Freeverse Club. Full-Stack Developer & AI Specialist passionate about building scalable campus tools.",
                skills = "React, Node.js, Kotlin, Python, Gemini AI, Jetpack Compose",
                hourlyRate = 750,
                rating = 4.95f,
                projectsCompleted = 14,
                isVerified = true,
                github = "https://github.com/bhavadharani-s",
                linkedin = "https://linkedin.com/in/bhavadharani-s",
                portfolio = "https://bhavadharani.dev",
                availability = "Available for Projects"
            ),
            UserEntity(
                id = "user_client",
                name = "Campus & Industry Client",
                email = "client@freeverse.com",
                role = "CLIENT",
                phone = "+91 98401 55667",
                organization = "KIT Department / Industry Partner",
                department = "Product & Academic Operations",
                year = "Client Partner",
                bio = "Department head and enterprise sponsor commissioning student freelance projects, web tools, and mobile apps.",
                skills = "Client Partner, Project Sponsor",
                isVerified = true
            ),
            UserEntity(
                id = "user_admin",
                name = "Freeverse Admin",
                email = "admin@freeverse.com",
                role = "ADMIN",
                phone = "+91 94432 99001",
                organization = "Kangeyam Institute of Technology",
                department = "Platform Operations",
                year = "Staff",
                bio = "Platform operations, proposal review oversight, and project moderation.",
                skills = "Platform Administration, Moderation",
                isVerified = true
            ),
            UserEntity(
                id = "user_superadmin",
                name = "Freeverse Super Admin",
                email = "superadmin@freeverse.com",
                role = "SUPER_ADMIN",
                phone = "+91 94432 99000",
                organization = "Kangeyam Institute of Technology",
                department = "Central Administration",
                year = "Lead Administrator",
                bio = "Executive super administrator with full system controls, user management, and platform analytics.",
                skills = "Executive Control, User Management, Analytics",
                isVerified = true
            ),
            UserEntity(
                id = "user_eventmanager",
                name = "Freeverse Event Manager",
                email = "eventmanager@freeverse.com",
                role = "EVENT_MANAGER",
                phone = "+91 94432 99002",
                organization = "Kangeyam Institute of Technology",
                department = "Events & Student Activities",
                year = "Club Coordinator",
                bio = "Organizing campus hackathons, design jams, skill workshops, and technical symposiums.",
                skills = "Event Management, Hackathons, Workshops",
                isVerified = true
            ),
            UserEntity(
                id = "user_nevitha",
                name = "Nevitha S V",
                email = "nevitha@freeverse.kit.ac.in",
                role = "STUDENT",
                phone = "+91 94883 12345",
                organization = "Kangeyam Institute of Technology",
                department = "Information Technology",
                year = "3rd Year",
                bio = "Secretary of Freeverse Club. UI/UX Designer & Frontend Artisan with a sharp eye for modern Material 3 typography and micro-interactions.",
                skills = "Figma, UI/UX Design, React, Tailwind CSS, Motion Graphics",
                hourlyRate = 600,
                rating = 4.90f,
                projectsCompleted = 11,
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
                role = "STUDENT",
                phone = "+91 97892 45678",
                organization = "Kangeyam Institute of Technology",
                department = "Electronics & Communication",
                year = "4th Year",
                bio = "Vice President of Freeverse Club. Cross-platform mobile developer & embedded IoT hobbyist.",
                skills = "Flutter, Android, Dart, Firebase, REST APIs, IoT",
                hourlyRate = 650,
                rating = 4.88f,
                projectsCompleted = 9,
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
                role = "STUDENT",
                phone = "+91 98425 67890",
                organization = "Kangeyam Institute of Technology",
                department = "Computer Science & Engineering",
                year = "3rd Year",
                bio = "Joint Secretary of Freeverse Club. Visual designer, brand identity specialist, and digital illustration expert.",
                skills = "Illustrator, Photoshop, Brand Identity, Poster Design, Canva Pro",
                hourlyRate = 500,
                rating = 4.92f,
                projectsCompleted = 16,
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
                role = "STUDENT",
                phone = "+91 99430 78912",
                organization = "Kangeyam Institute of Technology",
                department = "Computer Science & Engineering",
                year = "4th Year",
                bio = "Treasurer of Freeverse Club. Backend & Cloud Infrastructure engineer specializing in high-throughput databases and Docker pipelines.",
                skills = "Spring Boot, PostgreSQL, Docker, Java, AWS, Microservices",
                hourlyRate = 800,
                rating = 4.91f,
                projectsCompleted = 10,
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
                role = "STUDENT",
                phone = "+91 96291 23456",
                organization = "Kangeyam Institute of Technology",
                department = "Information Technology",
                year = "2nd Year",
                bio = "Joint Treasurer of Freeverse Club. Video editor, cinematic videographer & motion graphics creator for college symposiums and clubs.",
                skills = "Premiere Pro, After Effects, DaVinci Resolve, Video Color Grading, Reels",
                hourlyRate = 550,
                rating = 4.87f,
                projectsCompleted = 13,
                isVerified = true,
                github = "https://github.com/vaishnav-mn",
                linkedin = "https://linkedin.com/in/vaishnav-mn",
                portfolio = "https://vaishnav.media",
                availability = "Available for Projects"
            )
        )

        val initialProjects = listOf(
            ProjectEntity(
                id = "proj_1",
                clientId = "client_college",
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
                clientId = "client_external",
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
                clientId = "client_college",
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
                clientId = "client_external",
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
                clientId = "client_college",
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
                description = "Turn your creative ideas into interactive prototypes! Design high-impact web and mobile user journeys. Individual or team participation.",
                rules = "1. Original artwork only. 2. Software allowed: Figma, Illustrator. 3. Submit source links before 5:00 PM.",
                registeredCount = 48,
                isRegistered = false,
                isPast = false
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
                isPast = false
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
                isPast = false
            ),
            EventEntity(
                id = "evt_4",
                title = "UI/UX Design Sprint & Figma Jam",
                category = "Technical",
                date = "15 September 2026",
                time = "2:00 PM - 5:00 PM",
                venue = "KIT Seminar Hall B",
                description = "Hands-on design thinking, wireframing, and interactive prototyping session with industry alumni mentors.",
                rules = "Past event completed with 85 participants. Check gallery photos.",
                registeredCount = 85,
                isRegistered = false,
                isPast = true
            )
        )

        val initialServices = listOf(
            ServiceEntity(
                id = "srv_1",
                title = "Full-Stack Responsive Website Development",
                category = "Web Development",
                freelancerName = "Bhavadharani S",
                freelancerRole = "CSE 4th Year",
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
                freelancerName = "KAVYASSRI G",
                freelancerRole = "CSE 3rd Year",
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
                freelancerName = "Nevitha S V",
                freelancerRole = "IT 3rd Year",
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
                freelancerName = "Vaishnav M N",
                freelancerRole = "IT 2nd Year",
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
                freelancerName = "Keerthana B.K",
                freelancerRole = "ECE 4th Year",
                startingPrice = 4000,
                deliveryDays = 7,
                rating = 4.89f,
                reviewsCount = 11,
                description = "Smooth 60fps Flutter and native Android mobile apps with local database and cloud API sync."
            )
        )

        val initialNotifications = listOf(
            NotificationEntity(
                id = "notif_seed_1",
                userId = "user_bhavadharani",
                title = "Welcome to Freeverse!",
                message = "Your Skill Passport is active. Start exploring verified campus opportunities and student projects.",
                type = "PROJECT",
                timeAgo = "10 mins ago",
                isRead = false
            ),
            NotificationEntity(
                id = "notif_seed_2",
                userId = "user_bhavadharani",
                title = "Campus UI/UX Design Jam",
                message = "The 21 August 2026 design jam is coming up. Check rules and submit your entry!",
                type = "EVENT",
                timeAgo = "1 hour ago",
                isRead = false
            ),
            NotificationEntity(
                id = "notif_seed_3",
                userId = "client_college",
                title = "New Proposal on Portal Project",
                message = "Bhavadharani S submitted a proposal for the Department Accreditation Portal.",
                type = "APPLICATION",
                timeAgo = "2 hours ago",
                isRead = false
            )
        )

        val initialMessages = listOf(
            ChatMessageEntity(
                id = "msg_1",
                projectId = "proj_3",
                projectTitle = "KITECHNO 2026 Fest Teaser",
                senderId = "client_college",
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
                receiverId = "client_college",
                message = "Got the assets! First 30s draft will be ready by tomorrow evening with the 3D logo reveal.",
                timestamp = System.currentTimeMillis() - 1800000
            )
        )
    }
}
