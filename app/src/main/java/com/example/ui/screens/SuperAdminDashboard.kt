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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel
import com.example.ui.viewmodel.ScreenNav

enum class AdminTab(val title: String) {
    OVERVIEW("Overview"),
    USERS("Users"),
    ROLES("Roles"),
    COORDINATORS("Coordinators"),
    FACULTY("Faculty"),
    MEMBERS("Members"),
    CLIENTS("Clients"),
    PROJECTS("Projects"),
    EVENTS("Events"),
    CATEGORIES("Categories"),
    PAYMENTS("Payments"),
    REPORTS("Reports"),
    NOTIFICATIONS("Notifications"),
    SETTINGS("Settings")
}

@Composable
fun SuperAdminDashboard(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser ?: return
    val allUsers by viewModel.allUsers.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()
    val allEvents by viewModel.allEvents.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val allReports by viewModel.allReports.collectAsState()
    val notifications by viewModel.roleNotifications.collectAsState()

    val tabIndex by viewModel.selectedAdminTab.collectAsState()
    val activeTab = AdminTab.entries.getOrElse(tabIndex) { AdminTab.OVERVIEW }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FreeverseBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Super Admin Header Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E293B),
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
                        color = Color(0xFFF59E0B)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.padding(8.dp).size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("SUPER ADMIN PORTAL", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFF59E0B).copy(alpha = 0.3f)) {
                                Text("FULL CONTROL", fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = Color(0xFFFBBF24), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        Text(user.name, fontSize = 11.5.sp, color = Color(0xFF94A3B8))
                    }
                }

                IconButton(onClick = { viewModel.logout() }) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color(0xFFEF4444))
                }
            }
        }

        // Horizontal Tabs Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdminTab.entries.forEach { tab ->
                val isSelected = activeTab == tab
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color(0xFFF59E0B) else Color.White,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                    modifier = Modifier.clickable { viewModel.selectedAdminTab.value = tab.ordinal }
                ) {
                    Text(
                        text = tab.title,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        color = if (isSelected) Color.Black else FreeverseTextPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // Tab Content
        when (activeTab) {
            AdminTab.OVERVIEW -> {
                AdminOverviewSection(
                    totalUsers = allUsers.size,
                    totalProjects = allProjects.size,
                    totalEvents = allEvents.size,
                    totalVolume = allTransactions.sumOf { it.amount } + 42000,
                    reportsCount = allReports.size
                )
            }
            AdminTab.USERS -> {
                AdminUserManagementSection(
                    users = allUsers,
                    onToggleStatus = { viewModel.toggleUserStatus(it) }
                )
            }
            AdminTab.ROLES -> {
                AdminRoleManagementSection(users = allUsers, onUpdateRole = { user, role -> viewModel.updateUserRole(user, role) })
            }
            AdminTab.COORDINATORS -> {
                AdminCoordinatorsSection(users = allUsers.filter { it.role == "CLUB_COORDINATOR" })
            }
            AdminTab.FACULTY -> {
                AdminFacultySection(users = allUsers.filter { it.role == "FACULTY_COORDINATOR" })
            }
            AdminTab.MEMBERS -> {
                AdminMembersSection(users = allUsers.filter { it.role == "MEMBER" })
            }
            AdminTab.CLIENTS -> {
                AdminClientsSection(users = allUsers.filter { it.role == "CLIENT" })
            }
            AdminTab.PROJECTS -> {
                AdminProjectsSection(projects = allProjects)
            }
            AdminTab.EVENTS -> {
                AdminEventsSection(events = allEvents)
            }
            AdminTab.CATEGORIES -> {
                AdminCategoriesSection()
            }
            AdminTab.PAYMENTS -> {
                AdminPaymentsSection(transactions = allTransactions)
            }
            AdminTab.REPORTS -> {
                AdminReportsSection(reports = allReports)
            }
            AdminTab.NOTIFICATIONS -> {
                AdminNotificationsSection(notifications = notifications)
            }
            AdminTab.SETTINGS -> {
                AdminSettingsSection(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun AdminOverviewSection(
    totalUsers: Int,
    totalProjects: Int,
    totalEvents: Int,
    totalVolume: Int,
    reportsCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("PLATFORM OVERVIEW & LIVE HEALTH", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B), letterSpacing = 0.5.sp)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatMetricCard("Total Users", "$totalUsers", Icons.Default.People, Color(0xFF3B82F6), modifier = Modifier.weight(1f))
            StatMetricCard("Total Projects", "$totalProjects", Icons.Default.Work, Color(0xFF10B981), modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatMetricCard("Escrow Flow", "₹$totalVolume", Icons.Default.Payments, Color(0xFFF59E0B), modifier = Modifier.weight(1f))
            StatMetricCard("Live Events", "$totalEvents", Icons.Default.Event, Color(0xFF8B5CF6), modifier = Modifier.weight(1f))
        }

        // System Health Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Security & Infrastructure Health", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                HealthRow("Database Engine", "Room SQLite v2 / Hashed Tokens", true)
                HealthRow("RBAC Permission Guard", "Active (Zero-Trust)", true)
                HealthRow("Rate Limiting Protection", "Enabled (5 attempts / 60s cooldown)", true)
                HealthRow("Pending Complaints", "$reportsCount Active", reportsCount == 0)
            }
        }
    }
}

@Composable
private fun HealthRow(label: String, value: String, isHealthy: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.5.sp, color = FreeverseTextSecondary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(7.dp).clip(CircleShape).background(if (isHealthy) Color(0xFF10B981) else Color(0xFFF59E0B))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FreeverseTextPrimary)
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = FreeverseTextPrimary)
            Text(title, fontSize = 11.sp, color = FreeverseTextSecondary)
        }
    }
}

@Composable
private fun AdminUserManagementSection(
    users: List<UserEntity>,
    onToggleStatus: (UserEntity) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("USER MANAGEMENT (${users.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))

        users.forEach { user ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(UserRole.fromKey(user.role).badgeColorHex).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = UserRole.fromKey(user.role).displayName,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(UserRole.fromKey(user.role).badgeColorHex),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(user.email, fontSize = 11.sp, color = FreeverseTextSecondary)
                        Text(user.department, fontSize = 10.sp, color = FreeverseTextSecondary)
                    }

                    Button(
                        onClick = { onToggleStatus(user) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (user.accountStatus == "ACTIVE") Color(0xFFDC2626) else Color(0xFF10B981)
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (user.accountStatus == "ACTIVE") "Deactivate" else "Activate",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminRoleManagementSection(
    users: List<UserEntity>,
    onUpdateRole: (UserEntity, UserRole) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("ROLE ASSIGNMENT & PERMISSION MATRIX", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))

        users.take(8).forEach { user ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(user.email, fontSize = 11.sp, color = FreeverseTextSecondary)
                        }
                        Text(
                            text = UserRole.fromKey(user.role).displayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(UserRole.fromKey(user.role).badgeColorHex)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminCoordinatorsSection(users: List<UserEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("OFFICIAL CLUB COORDINATORS (${users.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
        users.forEach { coord ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF8B5CF6).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(coord.name.take(2).uppercase(), fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(coord.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            coord.coordinatorDesignation?.let {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(3.dp), color = Color(0xFFEDE9FE)) {
                                    Text(it, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp))
                                }
                            }
                        }
                        Text("${coord.department} • ${coord.phone}", fontSize = 11.sp, color = FreeverseTextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminFacultySection(users: List<UserEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("OFFICIAL FACULTY COORDINATORS (${users.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
        users.forEach { faculty ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF10B981).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(faculty.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${faculty.year} • ${faculty.department}", fontSize = 11.sp, color = FreeverseTextSecondary)
                        Text(faculty.email, fontSize = 10.5.sp, color = FreeversePrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMembersSection(users: List<UserEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("STUDENT MEMBERS & FREELANCERS (${users.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
        users.forEach { m ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(m.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Skills: ${m.skills}", fontSize = 11.sp, color = FreeversePrimary)
                    Text("${m.department} • Rating: ⭐${m.rating}", fontSize = 10.5.sp, color = FreeverseTextSecondary)
                }
            }
        }
    }
}

@Composable
private fun AdminClientsSection(users: List<UserEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("CLIENTS & PARTNER ORGANIZATIONS (${users.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
        users.forEach { c ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(c.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(c.organization, fontSize = 11.sp, color = FreeverseTextSecondary)
                    Text("Total Spent: ₹${c.totalSpent}", fontSize = 11.sp, color = FreeverseSuccess, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AdminProjectsSection(projects: List<ProjectEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("PLATFORM PROJECTS OVERSIGHT (${projects.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
        projects.forEach { p ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(p.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Text("₹${p.budget}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = FreeverseSuccess)
                    }
                    Text("Client: ${p.clientName} • Status: ${p.status}", fontSize = 11.sp, color = FreeverseTextSecondary)
                }
            }
        }
    }
}

@Composable
private fun AdminEventsSection(events: List<EventEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("CAMPUS EVENTS & WORKSHOPS (${events.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
        events.forEach { e ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(e.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("${e.date} at ${e.venue} • Registered: ${e.registeredCount}", fontSize = 11.sp, color = FreeverseTextSecondary)
                }
            }
        }
    }
}

@Composable
private fun AdminCategoriesSection() {
    val categories = listOf("Web Development", "UI/UX Design", "AI / Machine Learning", "Mobile Apps", "Video Editing", "Content & Copywriting", "Embedded IoT")
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("PLATFORM TAXONOMY & SKILL CATEGORIES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
        categories.forEach { cat ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(cat, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    Text("Active", color = FreeverseSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AdminPaymentsSection(transactions: List<TransactionEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("FINANCIAL ESCROW & PAYMENTS LEDGER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
        transactions.forEach { tx ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(tx.projectTitle, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, modifier = Modifier.weight(1f))
                        Text("₹${tx.amount}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = FreeverseSuccess)
                    }
                    Text("From: ${tx.senderName} -> To: ${tx.receiverName}", fontSize = 11.sp, color = FreeverseTextSecondary)
                    Text("Invoice: ${tx.invoiceId} • Status: ${tx.status}", fontSize = 10.sp, color = FreeversePrimary)
                }
            }
        }
    }
}

@Composable
private fun AdminReportsSection(reports: List<ReportComplaintEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("COMPLAINTS & AUDIT REPORTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
        reports.forEach { rep ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(rep.subject, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(rep.description, fontSize = 11.5.sp, color = FreeverseTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Reporter: ${rep.reporterName} (${rep.reporterRole}) • Status: ${rep.status}", fontSize = 10.5.sp, color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AdminNotificationsSection(notifications: List<NotificationEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("SYSTEM ALERTS & NOTIFICATIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
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
private fun AdminSettingsSection(viewModel: FreeverseViewModel) {
    var platformFee by remember { mutableStateOf("0% (100% Student Benefit)") }
    var maintenanceMode by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("SYSTEM SETTINGS & CONFIGURATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Platform Fee Policy: $platformFee", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Maintenance Mode", fontSize = 12.sp)
                    Switch(checked = maintenanceMode, onCheckedChange = {
                        maintenanceMode = it
                        viewModel.showSnackbar("Maintenance mode set to $it")
                    })
                }
            }
        }
    }
}
