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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel
import com.example.ui.viewmodel.ScreenNav

enum class CoordinatorTab(val title: String, val isCoordinatorOnly: Boolean) {
    CLUB_OVERVIEW("Club Overview", true),
    EVENTS("Events & Create", true),
    REGISTRATIONS("Event Registrations", true),
    MEMBERS("Community Members", true),
    ANNOUNCEMENTS("Announcements", true),
    MARKETPLACE("Browse Projects", false),
    MY_SERVICES("My Services", false),
    MY_PROPOSALS("My Proposals & Work", false),
    EARNINGS("My Earnings", false),
    MESSAGES("Messages", false),
    REPORTS("Club Reports", true),
    PROFILE("Profile & Skills", false)
}

@Composable
fun CoordinatorDashboard(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser ?: return
    val coordinatorMode by viewModel.coordinatorMode.collectAsState()
    val allEvents by viewModel.allEvents.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()
    val allProposals by viewModel.allProposals.collectAsState()
    val allServices by viewModel.allServices.collectAsState()
    val allAnnouncements by viewModel.allAnnouncements.collectAsState()

    val tabIndex by viewModel.selectedCoordinatorTab.collectAsState()
    val activeTab = CoordinatorTab.entries.getOrElse(tabIndex) { CoordinatorTab.CLUB_OVERVIEW }

    // Event creation state
    var showCreateEventDialog by remember { mutableStateOf(false) }
    var eventTitle by remember { mutableStateOf("") }
    var eventCategory by remember { mutableStateOf("Hackathon") }
    var eventDate by remember { mutableStateOf("25 September 2026") }
    var eventTime by remember { mutableStateOf("2:00 PM - 5:00 PM") }
    var eventVenue by remember { mutableStateOf("Central Computing Lab 3") }
    var eventDesc by remember { mutableStateOf("") }
    var eventRules by remember { mutableStateOf("Teams of 1-4 students.") }

    // Announcement creation state
    var showCreateAnnounceDialog by remember { mutableStateOf(false) }
    var annTitle by remember { mutableStateOf("") }
    var annContent by remember { mutableStateOf("") }

    // Service creation state
    var showCreateServiceDialog by remember { mutableStateOf(false) }
    var srvTitle by remember { mutableStateOf("") }
    var srvCategory by remember { mutableStateOf("Web Development") }
    var srvPrice by remember { mutableStateOf("2500") }
    var srvDays by remember { mutableStateOf("4") }
    var srvDesc by remember { mutableStateOf("") }

    val myProposals = allProposals.filter { it.studentId == user.id }
    val myServices = allServices.filter { it.freelancerId == user.id || it.freelancerName.contains(user.name, ignoreCase = true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FreeverseBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Coordinator Header with Designation
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF3B1E78),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF8B5CF6)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp).size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "FREEVERSE CLUB",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDDD6FE),
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF8B5CF6)) {
                                    Text(
                                        text = user.coordinatorDesignation ?: "PRESIDENT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Welcome back, ${user.name.split(" ").firstOrNull() ?: user.name}",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${user.coordinatorDesignation ?: "President"} • FREEVERSE Club • KIT",
                                fontSize = 11.sp,
                                color = Color(0xFFC4B5FD)
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.isLogoutConfirmOpen.value = true }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color(0xFFFCA5A5))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // DUAL MODE SWITCH (Coordinator Mode vs Freelancer Mode)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2E1065),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        Surface(
                            shape = RoundedCornerShape(9.dp),
                            color = if (coordinatorMode == CoordinatorMode.COORDINATOR) Color(0xFF8B5CF6) else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.setCoordinatorMode(CoordinatorMode.COORDINATOR)
                                    viewModel.selectedCoordinatorTab.value = CoordinatorTab.CLUB_OVERVIEW.ordinal
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = if (coordinatorMode == CoordinatorMode.COORDINATOR) Color.White else Color(0xFFA78BFA),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Coordinator Mode",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (coordinatorMode == CoordinatorMode.COORDINATOR) Color.White else Color(0xFFA78BFA)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(9.dp),
                            color = if (coordinatorMode == CoordinatorMode.FREELANCER) Color(0xFF8B5CF6) else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.setCoordinatorMode(CoordinatorMode.FREELANCER)
                                    viewModel.selectedCoordinatorTab.value = CoordinatorTab.MARKETPLACE.ordinal
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Work,
                                    contentDescription = null,
                                    tint = if (coordinatorMode == CoordinatorMode.FREELANCER) Color.White else Color(0xFFA78BFA),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Freelancer Mode",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (coordinatorMode == CoordinatorMode.FREELANCER) Color.White else Color(0xFFA78BFA)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sub Tabs Filtered by Current Active Mode
        val visibleTabs = remember(coordinatorMode) {
            if (coordinatorMode == CoordinatorMode.COORDINATOR) {
                listOf(
                    CoordinatorTab.CLUB_OVERVIEW,
                    CoordinatorTab.EVENTS,
                    CoordinatorTab.REGISTRATIONS,
                    CoordinatorTab.MEMBERS,
                    CoordinatorTab.ANNOUNCEMENTS,
                    CoordinatorTab.REPORTS,
                    CoordinatorTab.PROFILE
                )
            } else {
                listOf(
                    CoordinatorTab.MARKETPLACE,
                    CoordinatorTab.MY_SERVICES,
                    CoordinatorTab.MY_PROPOSALS,
                    CoordinatorTab.EARNINGS,
                    CoordinatorTab.MESSAGES,
                    CoordinatorTab.PROFILE
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            visibleTabs.forEach { tab ->
                val isSelected = activeTab == tab
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color(0xFF8B5CF6) else Color.White,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                    modifier = Modifier.clickable { viewModel.selectedCoordinatorTab.value = tab.ordinal }
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

        // Active Tab Screen Views
        when (activeTab) {
            CoordinatorTab.CLUB_OVERVIEW -> {
                CoordinatorOverviewView(
                    events = allEvents,
                    membersCount = allUsers.count { it.role == "MEMBER" },
                    projects = allProjects,
                    proposals = myProposals,
                    announcements = allAnnouncements,
                    walletBalance = user.totalEarnings,
                    onCreateEventClick = { showCreateEventDialog = true },
                    onCreateAnnounceClick = { showCreateAnnounceDialog = true },
                    onExploreProjects = { viewModel.setCoordinatorTab(CoordinatorTab.MARKETPLACE.ordinal) },
                    onViewMembers = { viewModel.setCoordinatorTab(CoordinatorTab.MEMBERS.ordinal) },
                    onManageServices = { viewModel.setCoordinatorTab(CoordinatorTab.MY_SERVICES.ordinal) }
                )
            }
            CoordinatorTab.EVENTS -> {
                CoordinatorEventsView(
                    events = allEvents,
                    onCreateEvent = { showCreateEventDialog = true }
                )
            }
            CoordinatorTab.REGISTRATIONS -> {
                CoordinatorRegistrationsView(events = allEvents)
            }
            CoordinatorTab.MEMBERS -> {
                CoordinatorMembersView(members = allUsers.filter { it.role == "MEMBER" || it.role == "CLUB_COORDINATOR" })
            }
            CoordinatorTab.ANNOUNCEMENTS -> {
                CoordinatorAnnouncementsView(
                    announcements = allAnnouncements,
                    onCreateClick = { showCreateAnnounceDialog = true }
                )
            }
            CoordinatorTab.MARKETPLACE -> {
                CoordinatorFreelanceMarketplace(
                    projects = allProjects,
                    onApply = { project -> viewModel.selectedProjectForApply.value = project }
                )
            }
            CoordinatorTab.MY_SERVICES -> {
                CoordinatorServicesView(
                    services = myServices,
                    onCreateService = { showCreateServiceDialog = true }
                )
            }
            CoordinatorTab.MY_PROPOSALS -> {
                CoordinatorProposalsView(
                    proposals = myProposals,
                    onSubmitWork = { proposal, notes -> viewModel.submitCompletedWork(proposal, notes) }
                )
            }
            CoordinatorTab.EARNINGS -> {
                CoordinatorEarningsView(user = user, proposals = myProposals)
            }
            CoordinatorTab.MESSAGES -> {
                CoordinatorMessagesView(viewModel = viewModel)
            }
            CoordinatorTab.REPORTS -> {
                CoordinatorReportsView()
            }
            CoordinatorTab.PROFILE -> {
                CoordinatorProfileView(user = user)
            }
        }
    }

    // Dialog: Create Event
    if (showCreateEventDialog) {
        AlertDialog(
            onDismissRequest = { showCreateEventDialog = false },
            title = { Text("Create Club Event / Workshop", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = eventTitle, onValueChange = { eventTitle = it }, label = { Text("Event Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = eventDate, onValueChange = { eventDate = it }, label = { Text("Date (e.g. 25 Sep 2026)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = eventVenue, onValueChange = { eventVenue = it }, label = { Text("Venue") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = eventDesc, onValueChange = { eventDesc = it }, label = { Text("Description & Goals") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (eventTitle.isNotBlank()) {
                            viewModel.createEvent(eventTitle, eventCategory, eventDate, eventTime, eventVenue, eventDesc, eventRules)
                            showCreateEventDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                ) {
                    Text("Publish Event")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateEventDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Post Announcement
    if (showCreateAnnounceDialog) {
        AlertDialog(
            onDismissRequest = { showCreateAnnounceDialog = false },
            title = { Text("Post Club Announcement", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = annTitle, onValueChange = { annTitle = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = annContent, onValueChange = { annContent = it }, label = { Text("Content") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (annTitle.isNotBlank() && annContent.isNotBlank()) {
                            viewModel.postAnnouncement(annTitle, annContent, "HIGH")
                            showCreateAnnounceDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                ) {
                    Text("Publish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateAnnounceDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Create Service
    if (showCreateServiceDialog) {
        AlertDialog(
            onDismissRequest = { showCreateServiceDialog = false },
            title = { Text("Create Freelancing Service", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = srvTitle, onValueChange = { srvTitle = it }, label = { Text("Service Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = srvCategory, onValueChange = { srvCategory = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = srvPrice, onValueChange = { srvPrice = it }, label = { Text("Starting Price (₹)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = srvDesc, onValueChange = { srvDesc = it }, label = { Text("Deliverables & Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (srvTitle.isNotBlank()) {
                            val price = srvPrice.toIntOrNull() ?: 1500
                            val days = srvDays.toIntOrNull() ?: 3
                            viewModel.createService(srvTitle, srvCategory, price, days, srvDesc)
                            showCreateServiceDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                ) {
                    Text("Publish Service")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateServiceDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun CoordinatorOverviewView(
    events: List<EventEntity>,
    membersCount: Int,
    projects: List<ProjectEntity>,
    proposals: List<ProposalEntity>,
    announcements: List<AnnouncementEntity>,
    walletBalance: Int,
    onCreateEventClick: () -> Unit,
    onCreateAnnounceClick: () -> Unit,
    onExploreProjects: () -> Unit,
    onViewMembers: () -> Unit,
    onManageServices: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Section Header
        Text(
            text = "PRESIDENT OVERVIEW & CLUB HEALTH",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF8B5CF6),
            letterSpacing = 1.sp
        )

        // 6 Quick Statistics (Responsive Grids)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CoordinatorStatCard("Active Members", "$membersCount", Icons.Default.Groups, Color(0xFF8B5CF6), Modifier.weight(1f))
                CoordinatorStatCard("Upcoming Events", "${events.size}", Icons.Default.Event, Color(0xFF3B82F6), Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CoordinatorStatCard("Active Projects", "${projects.size}", Icons.Default.Work, Color(0xFF10B981), Modifier.weight(1f))
                CoordinatorStatCard("Pending Proposals", "${proposals.count { it.status == "PENDING" }}", Icons.Default.Assignment, Color(0xFFF59E0B), Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CoordinatorStatCard("Club Activities", "${events.size + announcements.size}", Icons.Default.Campaign, Color(0xFF6366F1), Modifier.weight(1f))
                CoordinatorStatCard("Freelance Earnings", "₹$walletBalance", Icons.Default.Payments, Color(0xFF0D9488), Modifier.weight(1f))
            }
        }

        // QUICK ACTIONS
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "QUICK ACTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreeverseTextSecondary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onCreateEventClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create Event", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onCreateAnnounceClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF8B5CF6))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Announcement", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
                    }

                    OutlinedButton(
                        onClick = onViewMembers,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(15.dp), tint = FreeversePrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View Members", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FreeversePrimary)
                    }

                    OutlinedButton(
                        onClick = onExploreProjects,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Explore Projects", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    }

                    OutlinedButton(
                        onClick = onManageServices,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFFF59E0B))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Manage Services", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                    }
                }
            }
        }

        // SECTION 1: CLUB ACTIVITY
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CLUB MANAGEMENT & ACTIVITIES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5CF6)
                    )
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEDE9FE)) {
                        Text(
                            text = "${events.size} Active Events",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7C3AED),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                events.take(2).forEach { event ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(event.title, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                Text("${event.date} • ${event.venue}", fontSize = 11.sp, color = FreeverseTextSecondary)
                            }
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF8B5CF6)) {
                                Text(
                                    text = "${event.registeredCount} Registered",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                if (announcements.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Latest Announcement:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FreeverseTextSecondary)
                    val latest = announcements.first()
                    Text("📢 ${latest.title}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FreeverseTextPrimary)
                    Text(latest.content, fontSize = 11.sp, color = FreeverseTextSecondary, maxLines = 2)
                }
            }
        }

        // SECTION 2: FREELANCER ACTIVITY
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FREELANCER ACTIVITY & PIPELINE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFD1FAE5)) {
                        Text(
                            text = "${proposals.size} Proposals",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (proposals.isEmpty()) {
                    Text(
                        text = "No active proposals yet. Browse open campus gigs to submit proposals.",
                        fontSize = 11.5.sp,
                        color = FreeverseTextSecondary
                    )
                } else {
                    proposals.take(2).forEach { prop ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(prop.projectTitle, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                    Text("Bid: ₹${prop.proposedBudget} • Delivery: ${prop.expectedDelivery}", fontSize = 11.sp, color = FreeverseTextSecondary)
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (prop.status == "HIRED") Color(0xFF10B981) else Color(0xFFF59E0B)
                                ) {
                                    Text(
                                        text = prop.status,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CoordinatorStatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = FreeverseTextPrimary)
            Text(title, fontSize = 11.sp, color = FreeverseTextSecondary)
        }
    }
}

@Composable
private fun CoordinatorEventsView(events: List<EventEntity>, onCreateEvent: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("CLUB EVENTS (${events.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
            TextButton(onClick = onCreateEvent) {
                Text("+ New Event", color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
        events.forEach { e ->
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(e.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("${e.date} (${e.time}) • ${e.venue}", fontSize = 11.sp, color = FreeverseTextSecondary)
                    Text("Registrations: ${e.registeredCount}", fontSize = 11.sp, color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CoordinatorRegistrationsView(events: List<EventEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("EVENT REGISTRATIONS & ATTENDEE LIST", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
        events.forEach { e ->
            Surface(shape = RoundedCornerShape(10.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(e.title, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        Text(e.date, fontSize = 11.sp, color = FreeverseTextSecondary)
                    }
                    Text("${e.registeredCount} Registered", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF8B5CF6))
                }
            }
        }
    }
}

@Composable
private fun CoordinatorMembersView(members: List<UserEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("FREEVERSE COMMUNITY MEMBERS (${members.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
        members.forEach { m ->
            Surface(shape = RoundedCornerShape(10.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(m.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        m.coordinatorDesignation?.let {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(3.dp), color = Color(0xFFEDE9FE)) {
                                Text(it, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                    }
                    Text("${m.department} • Skills: ${m.skills}", fontSize = 11.sp, color = FreeverseTextSecondary)
                }
            }
        }
    }
}

@Composable
private fun CoordinatorAnnouncementsView(announcements: List<AnnouncementEntity>, onCreateClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("CLUB ANNOUNCEMENTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
            TextButton(onClick = onCreateClick) {
                Text("+ Post", color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold)
            }
        }
        announcements.forEach { a ->
            Surface(shape = RoundedCornerShape(10.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(a.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(a.content, fontSize = 11.5.sp, color = FreeverseTextSecondary)
                    Text("By ${a.authorName} • ${a.date}", fontSize = 10.sp, color = Color(0xFF8B5CF6))
                }
            }
        }
    }
}

@Composable
private fun CoordinatorFreelanceMarketplace(projects: List<ProjectEntity>, onApply: (ProjectEntity) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("FREELANCE MARKETPLACE (BROWSE & APPLY)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
        projects.forEach { p ->
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(p.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Text("₹${p.budget}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = FreeverseSuccess)
                    }
                    Text(p.description, fontSize = 11.5.sp, color = FreeverseTextSecondary, maxLines = 2)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { onApply(p) }, shape = RoundedCornerShape(6.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))) {
                        Text("Submit Proposal", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CoordinatorServicesView(services: List<ServiceEntity>, onCreateService: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("MY FREELANCER SERVICES (${services.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
            TextButton(onClick = onCreateService) {
                Text("+ New Service", color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold)
            }
        }
        services.forEach { s ->
            Surface(shape = RoundedCornerShape(10.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(s.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Text("₹${s.startingPrice}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = FreeverseSuccess)
                    }
                    Text(s.description, fontSize = 11.sp, color = FreeverseTextSecondary)
                }
            }
        }
    }
}

@Composable
private fun CoordinatorProposalsView(proposals: List<ProposalEntity>, onSubmitWork: (ProposalEntity, String) -> Unit) {
    var workNotes by remember { mutableStateOf("") }
    var selectedForSubmit by remember { mutableStateOf<ProposalEntity?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("MY ACTIVE CONTRACTS & PROPOSALS (${proposals.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
        if (proposals.isEmpty()) {
            Surface(shape = RoundedCornerShape(10.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Text("No proposals submitted yet. Browse the marketplace in Freelancer Mode to apply.", fontSize = 12.sp, color = FreeverseTextSecondary)
            }
        } else {
            proposals.forEach { prop ->
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(prop.projectTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Text("₹${prop.proposedBudget}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = FreeverseSuccess)
                        }
                        Text("Status: ${prop.status} • Delivery: ${prop.expectedDelivery}", fontSize = 11.sp, color = FreeversePrimary)

                        if (prop.status == "HIRED") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { selectedForSubmit = prop }, colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary)) {
                                Text("Submit Completed Work")
                            }
                        }
                    }
                }
            }
        }
    }

    selectedForSubmit?.let { prop ->
        AlertDialog(
            onDismissRequest = { selectedForSubmit = null },
            title = { Text("Submit Deliverables for '${prop.projectTitle}'") },
            text = {
                OutlinedTextField(
                    value = workNotes,
                    onValueChange = { workNotes = it },
                    label = { Text("Git Repository URL / Figma Link / Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSubmitWork(prop, workNotes)
                        selectedForSubmit = null
                    }
                ) {
                    Text("Submit to Client")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedForSubmit = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun CoordinatorEarningsView(user: UserEntity, proposals: List<ProposalEntity>) {
    Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("MY FREELANCING WALLET", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
            Spacer(modifier = Modifier.height(6.dp))
            Text("₹${user.totalEarnings}", fontSize = 26.sp, fontWeight = FontWeight.Black, color = FreeverseSuccess)
            Text("Total Earned across ${user.projectsCompleted} delivered campus & client projects", fontSize = 11.5.sp, color = FreeverseTextSecondary)
        }
    }
}

@Composable
private fun CoordinatorMessagesView(viewModel: FreeverseViewModel) {
    val messages by viewModel.allChatMessages.collectAsState()
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("CLIENT MESSAGES & PROJECT CHAT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
        messages.forEach { m ->
            Surface(shape = RoundedCornerShape(10.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(m.projectTitle, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    Text("${m.senderName}: ${m.message}", fontSize = 11.5.sp, color = FreeverseTextPrimary)
                }
            }
        }
    }
}

@Composable
private fun CoordinatorReportsView() {
    Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("FREEVERSE Club Activity Report", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("All symposiums, micro-gigs, and sprint challenges are logged and verified with faculty mentors.", fontSize = 11.5.sp, color = FreeverseTextSecondary)
        }
    }
}

@Composable
private fun CoordinatorProfileView(user: UserEntity) {
    Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                user.coordinatorDesignation?.let {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(3.dp), color = Color(0xFFEDE9FE)) {
                        Text(it, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            Text("${user.department} • ${user.year}", fontSize = 12.sp, color = FreeverseTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Skills: ${user.skills}", fontSize = 11.5.sp, color = FreeversePrimary, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(user.bio, fontSize = 11.5.sp, color = FreeverseTextSecondary)
        }
    }
}
