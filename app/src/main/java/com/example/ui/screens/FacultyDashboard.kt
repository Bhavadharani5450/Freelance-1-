package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel

enum class FacultyTab(val title: String) {
    CLUB_OVERVIEW("Club Overview"),
    EVENTS("Events"),
    REGISTRATIONS("Registrations"),
    PARTICIPATION("Student Participation"),
    COORDINATORS("Club Coordinators"),
    COMMUNITY("Community"),
    FREELANCING_ACTIVITY("Freelancing Activity"),
    PROJECT_MONITORING("Project Monitoring"),
    REPORTS("Reports"),
    NOTIFICATIONS("Notifications"),
    PROFILE("Profile")
}

@Composable
fun FacultyDashboard(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser ?: return
    val allUsers by viewModel.allUsers.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()
    val allEvents by viewModel.allEvents.collectAsState()
    val allAnnouncements by viewModel.allAnnouncements.collectAsState()
    val notifications by viewModel.roleNotifications.collectAsState()

    val tabIndex by viewModel.selectedFacultyTab.collectAsState()
    val activeTab = FacultyTab.entries.getOrElse(tabIndex) { FacultyTab.CLUB_OVERVIEW }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FreeverseBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Faculty Header Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF064E3B),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF10B981)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp).size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(user.name, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color.White)
                        }
                        Text("Faculty Coordinator • FREEVERSE Club", fontSize = 11.5.sp, color = Color(0xFFA7F3D0))
                        Text("Kangeyam Institute of Technology", fontSize = 10.sp, color = Color(0xFF6EE7B7))
                    }
                }

                IconButton(onClick = { viewModel.logout() }) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color(0xFFFCA5A5))
                }
            }
        }

        // Horizontal Tab Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FacultyTab.entries.forEach { tab ->
                val isSelected = activeTab == tab
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color(0xFF10B981) else Color.White,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                    modifier = Modifier.clickable { viewModel.selectedFacultyTab.value = tab.ordinal }
                ) {
                    Text(
                        text = tab.title,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        color = if (isSelected) Color.White else FreeverseTextPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // Tab Content
        when (activeTab) {
            FacultyTab.CLUB_OVERVIEW -> {
                FacultyOverviewSection(allUsers, allProjects, allEvents)
            }
            FacultyTab.EVENTS -> {
                FacultyEventsSection(allEvents)
            }
            FacultyTab.REGISTRATIONS -> {
                FacultyRegistrationsSection(allEvents)
            }
            FacultyTab.PARTICIPATION -> {
                FacultyParticipationSection(allUsers)
            }
            FacultyTab.COORDINATORS -> {
                FacultyCoordinatorsSection(allUsers.filter { it.role == "CLUB_COORDINATOR" })
            }
            FacultyTab.COMMUNITY -> {
                FacultyCommunitySection(allAnnouncements)
            }
            FacultyTab.FREELANCING_ACTIVITY -> {
                FacultyFreelancingSection(allUsers.filter { it.role == "MEMBER" || it.role == "CLUB_COORDINATOR" })
            }
            FacultyTab.PROJECT_MONITORING -> {
                FacultyProjectMonitoringSection(allProjects)
            }
            FacultyTab.REPORTS -> {
                FacultyReportsSection()
            }
            FacultyTab.NOTIFICATIONS -> {
                FacultyNotificationsSection(notifications)
            }
            FacultyTab.PROFILE -> {
                FacultyProfileSection(user)
            }
        }
    }
}

@Composable
private fun FacultyOverviewSection(users: List<UserEntity>, projects: List<ProjectEntity>, events: List<EventEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("CLUB ACADEMIC OVERVIEW & KPIS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FacultyMetricCard("Active Students", "${users.count { it.role == "MEMBER" || it.role == "CLUB_COORDINATOR" }}", Icons.Default.Groups, Color(0xFF3B82F6), Modifier.weight(1f))
            FacultyMetricCard("Monitored Projects", "${projects.size}", Icons.Default.Assessment, Color(0xFF10B981), Modifier.weight(1f))
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Faculty Monitoring Note", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "FREEVERSE is operating under autonomous institutional guidelines. Student contracts, technical symposiums, and milestone payouts are regularly audited for academic alignment and safety.",
                    fontSize = 11.5.sp,
                    color = FreeverseTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun FacultyMetricCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = FreeverseTextPrimary)
            Text(title, fontSize = 11.sp, color = FreeverseTextSecondary)
        }
    }
}

@Composable
private fun FacultyEventsSection(events: List<EventEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("CLUB EVENTS & WORKSHOPS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
        events.forEach { e ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(e.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("${e.date} (${e.time}) • Venue: ${e.venue}", fontSize = 11.sp, color = FreeverseTextSecondary)
                    Text("Organized by: ${e.createdByCoordinator} • Registered: ${e.registeredCount} Students", fontSize = 11.sp, color = FreeversePrimary)
                }
            }
        }
    }
}

@Composable
private fun FacultyRegistrationsSection(events: List<EventEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("EVENT REGISTRATIONS & ATTENDANCE AUDIT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
        events.forEach { e ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(e.title, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        Text(e.date, fontSize = 11.sp, color = FreeverseTextSecondary)
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFE8F5E9)) {
                        Text("${e.registeredCount} Confirmed", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun FacultyParticipationSection(users: List<UserEntity>) {
    val students = users.filter { it.role == "MEMBER" || it.role == "CLUB_COORDINATOR" }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("STUDENT PARTICIPATION LEADERBOARD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
        students.sortedByDescending { it.projectsCompleted }.forEach { s ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(s.name, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        Text("${s.department} • ${s.year}", fontSize = 11.sp, color = FreeverseTextSecondary)
                    }
                    Text("${s.projectsCompleted} Projects Delivered", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = FreeversePrimary)
                }
            }
        }
    }
}

@Composable
private fun FacultyCoordinatorsSection(coordinators: List<UserEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("FREEVERSE STUDENT COORDINATOR BOARD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
        coordinators.forEach { c ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(c.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            c.coordinatorDesignation?.let {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(3.dp), color = Color(0xFFE8F5E9)) {
                                    Text(it, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                        }
                        Text(c.email, fontSize = 11.sp, color = FreeverseTextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun FacultyCommunitySection(announcements: List<AnnouncementEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("COMMUNITY ANNOUNCEMENTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
        announcements.forEach { ann ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(ann.title, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    Text(ann.content, fontSize = 11.sp, color = FreeverseTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("By ${ann.authorName} (${ann.authorRole}) • ${ann.date}", fontSize = 10.sp, color = FreeversePrimary)
                }
            }
        }
    }
}

@Composable
private fun FacultyFreelancingSection(students: List<UserEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("STUDENT FREELANCE EARNINGS & SKILL PROGRESS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
        students.forEach { s ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(s.name, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        Text(s.skills, fontSize = 10.5.sp, color = FreeverseTextSecondary)
                    }
                    Text("₹${s.totalEarnings} Earned", fontWeight = FontWeight.Black, fontSize = 12.5.sp, color = FreeverseSuccess)
                }
            }
        }
    }
}

@Composable
private fun FacultyProjectMonitoringSection(projects: List<ProjectEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("CAMPUS CONTRACTS & PROJECT MONITORING", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
        projects.forEach { p ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(p.title, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    Text("Client: ${p.clientName} • Budget: ₹${p.budget}", fontSize = 11.sp, color = FreeverseTextSecondary)
                    Text("Status: ${p.status} • Progress: ${p.progressPercent}%", fontSize = 11.sp, color = FreeversePrimary)
                }
            }
        }
    }
}

@Composable
private fun FacultyReportsSection() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Academic Compliance Report", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("100% of projects align with academic ethics, engineering skill enhancement, and student protection guidelines.", fontSize = 11.5.sp, color = FreeverseTextSecondary)
        }
    }
}

@Composable
private fun FacultyNotificationsSection(notifications: List<NotificationEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("FACULTY NOTIFICATIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
        notifications.forEach { n ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(n.title, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    Text(n.message, fontSize = 11.sp, color = FreeverseTextSecondary)
                }
            }
        }
    }
}

@Composable
private fun FacultyProfileSection(user: UserEntity) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("${user.year} • ${user.department}", fontSize = 12.sp, color = FreeverseTextSecondary)
            Text(user.organization, fontSize = 12.sp, color = FreeverseTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(user.bio, fontSize = 11.5.sp, color = FreeverseTextPrimary)
        }
    }
}
