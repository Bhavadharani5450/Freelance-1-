package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel

enum class MemberTab(val title: String) {
    EXPLORE_PROJECTS("Explore Projects"),
    MY_PROFILE("My Profile"),
    MY_PORTFOLIO("My Portfolio"),
    MY_SERVICES("My Services"),
    MY_PROPOSALS("My Proposals"),
    MY_PROJECTS("My Active Contracts"),
    MESSAGES("Messages"),
    COMMUNITY("Community"),
    EVENTS("Events"),
    EARNINGS("Earnings"),
    NOTIFICATIONS("Notifications"),
    SETTINGS("Settings")
}

@Composable
fun MemberDashboard(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser ?: return
    val allProjects by viewModel.allProjects.collectAsState()
    val allProposals by viewModel.allProposals.collectAsState()
    val allServices by viewModel.allServices.collectAsState()
    val allEvents by viewModel.allEvents.collectAsState()
    val notifications by viewModel.roleNotifications.collectAsState()

    val tabIndex by viewModel.selectedMemberTab.collectAsState()
    val activeTab = MemberTab.entries.getOrElse(tabIndex) { MemberTab.EXPLORE_PROJECTS }

    val myProposals = allProposals.filter { it.studentId == user.id }
    val myActiveContracts = allProposals.filter { it.studentId == user.id && (it.status == "HIRED" || it.status == "WORK_SUBMITTED" || it.status == "COMPLETED") }
    val myServices = allServices.filter { it.freelancerId == user.id || it.freelancerName.contains(user.name, ignoreCase = true) }

    var showCreateServiceDialog by remember { mutableStateOf(false) }
    var srvTitle by remember { mutableStateOf("") }
    var srvCategory by remember { mutableStateOf("Web Development") }
    var srvPrice by remember { mutableStateOf("1500") }
    var srvDesc by remember { mutableStateOf("") }

    var selectedProposalForSubmit by remember { mutableStateOf<ProposalEntity?>(null) }
    var workNotes by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FreeverseBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Member Header
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E3A8A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF3B82F6)) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp).size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(user.name, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color.White)
                        Text("Student Freelancer • ${user.department}", fontSize = 11.sp, color = Color(0xFF93C5FD))
                    }
                }
                IconButton(onClick = { viewModel.isLogoutConfirmOpen.value = true }) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color(0xFFFCA5A5))
                }
            }
        }

        // Horizontal Navigation Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MemberTab.entries.forEach { tab ->
                val isSelected = activeTab == tab
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color(0xFF3B82F6) else Color.White,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                    modifier = Modifier.clickable { viewModel.selectedMemberTab.value = tab.ordinal }
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

        // Sub Screen Content
        when (activeTab) {
            MemberTab.EXPLORE_PROJECTS -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("EXPLORE OPEN PROJECTS (${allProjects.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                    allProjects.forEach { p ->
                        Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(p.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                    Text("₹${p.budget}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = FreeverseSuccess)
                                }
                                Text("Client: ${p.clientName} • Deadline: ${p.deadline}", fontSize = 11.sp, color = FreeverseTextSecondary)
                                Text("Skills: ${p.requiredSkills}", fontSize = 10.5.sp, color = FreeversePrimary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.selectedProjectForApply.value = p },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Apply for Project", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            MemberTab.MY_PROFILE -> {
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${user.department} • ${user.year}", fontSize = 12.sp, color = FreeverseTextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Skills: ${user.skills}", fontSize = 12.sp, color = FreeversePrimary, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Hourly Rate: ₹${user.hourlyRate}/hr • Rating: ⭐${user.rating}", fontSize = 11.5.sp, color = FreeverseTextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(user.bio, fontSize = 11.5.sp, color = FreeverseTextPrimary)
                    }
                }
            }
            MemberTab.MY_PORTFOLIO -> {
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Verified Student Portfolio Links", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("GitHub: ${user.github}", fontSize = 11.5.sp, color = FreeversePrimary)
                        Text("LinkedIn: ${user.linkedin}", fontSize = 11.5.sp, color = FreeversePrimary)
                        Text("Portfolio Website: ${user.portfolio}", fontSize = 11.5.sp, color = FreeversePrimary)
                    }
                }
            }
            MemberTab.MY_SERVICES -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("MY PUBLISHED SERVICES (${myServices.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                        TextButton(onClick = { showCreateServiceDialog = true }) {
                            Text("+ Create Service", color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)
                        }
                    }
                    if (myServices.isEmpty()) {
                        Surface(shape = RoundedCornerShape(10.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                            Text("No services published yet. Offer your design, coding, or video skills to earn.", fontSize = 12.sp, color = FreeverseTextSecondary)
                        }
                    } else {
                        myServices.forEach { s ->
                            Surface(shape = RoundedCornerShape(10.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(s.title, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, modifier = Modifier.weight(1f))
                                        Text("₹${s.startingPrice}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = FreeverseSuccess)
                                    }
                                    Text(s.description, fontSize = 11.sp, color = FreeverseTextSecondary)
                                }
                            }
                        }
                    }
                }
            }
            MemberTab.MY_PROPOSALS -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("MY SUBMITTED PROPOSALS (${myProposals.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                    myProposals.forEach { prop ->
                        Surface(shape = RoundedCornerShape(10.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(prop.projectTitle, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, modifier = Modifier.weight(1f))
                                    Text("₹${prop.proposedBudget}", fontWeight = FontWeight.Black, fontSize = 12.5.sp, color = FreeverseSuccess)
                                }
                                Text("Status: ${prop.status} • Delivery: ${prop.expectedDelivery}", fontSize = 11.sp, color = FreeversePrimary)
                            }
                        }
                    }
                }
            }
            MemberTab.MY_PROJECTS -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("MY ACTIVE CONTRACTS (${myActiveContracts.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                    myActiveContracts.forEach { c ->
                        Surface(shape = RoundedCornerShape(10.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(c.projectTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Contract Value: ₹${c.proposedBudget} • Status: ${c.status}", fontSize = 11.sp, color = FreeversePrimary)
                                if (c.status == "HIRED") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { selectedProposalForSubmit = c },
                                        colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary)
                                    ) {
                                        Text("Submit Completed Work")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            MemberTab.MESSAGES -> {
                val messages by viewModel.allChatMessages.collectAsState()
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("CLIENT MESSAGES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                    messages.forEach { m ->
                        Surface(shape = RoundedCornerShape(10.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(m.projectTitle, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("${m.senderName}: ${m.message}", fontSize = 11.sp, color = FreeverseTextPrimary)
                            }
                        }
                    }
                }
            }
            MemberTab.COMMUNITY -> {
                val announcements by viewModel.allAnnouncements.collectAsState()
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("FREEVERSE COMMUNITY & TEAMS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                    announcements.forEach { a ->
                        Surface(shape = RoundedCornerShape(10.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(a.title, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                Text(a.content, fontSize = 11.sp, color = FreeverseTextSecondary)
                            }
                        }
                    }
                }
            }
            MemberTab.EVENTS -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("CAMPUS EVENTS & WORKSHOPS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                    allEvents.forEach { e ->
                        Surface(shape = RoundedCornerShape(10.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(e.title, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                Text("${e.date} at ${e.venue}", fontSize = 11.sp, color = FreeverseTextSecondary)
                            }
                        }
                    }
                }
            }
            MemberTab.EARNINGS -> {
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("MY EARNINGS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("₹${user.totalEarnings}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = FreeverseSuccess)
                        Text("100% direct payouts to student account. No platform commission deducted.", fontSize = 11.sp, color = FreeverseTextSecondary)
                    }
                }
            }
            MemberTab.NOTIFICATIONS -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("NOTIFICATIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                    notifications.forEach { n ->
                        Surface(shape = RoundedCornerShape(10.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(n.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(n.message, fontSize = 11.sp, color = FreeverseTextSecondary)
                            }
                        }
                    }
                }
            }
            MemberTab.SETTINGS -> {
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Account & Security Settings", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Email: ${user.email}", fontSize = 11.5.sp, color = FreeverseTextSecondary)
                        Text("Status: ${user.accountStatus}", fontSize = 11.5.sp, color = FreeverseSuccess)
                    }
                }
            }
        }
    }

    if (showCreateServiceDialog) {
        AlertDialog(
            onDismissRequest = { showCreateServiceDialog = false },
            title = { Text("Publish Freelancing Service") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = srvTitle, onValueChange = { srvTitle = it }, label = { Text("Service Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = srvCategory, onValueChange = { srvCategory = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = srvPrice, onValueChange = { srvPrice = it }, label = { Text("Starting Price (₹)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = srvDesc, onValueChange = { srvDesc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (srvTitle.isNotBlank()) {
                            val price = srvPrice.toIntOrNull() ?: 1500
                            viewModel.createService(srvTitle, srvCategory, price, 3, srvDesc)
                            showCreateServiceDialog = false
                        }
                    }
                ) {
                    Text("Publish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateServiceDialog = false }) { Text("Cancel") }
            }
        )
    }

    selectedProposalForSubmit?.let { prop ->
        AlertDialog(
            onDismissRequest = { selectedProposalForSubmit = null },
            title = { Text("Submit Work Deliverables") },
            text = {
                OutlinedTextField(
                    value = workNotes,
                    onValueChange = { workNotes = it },
                    label = { Text("Project URL / Repository / Submission Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitCompletedWork(prop, workNotes)
                        selectedProposalForSubmit = null
                    }
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProposalForSubmit = null }) { Text("Cancel") }
            }
        )
    }
}
