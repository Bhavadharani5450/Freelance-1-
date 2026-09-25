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

enum class ClientTab(val title: String) {
    POST_PROJECT("Post Project"),
    MY_PROJECTS("My Projects"),
    PROPOSALS("Proposals Received"),
    FIND_TALENT("Find Talent"),
    HIRED_FREELANCERS("Hired Freelancers"),
    ACTIVE_PROJECTS("Active Projects"),
    COMPLETED_PROJECTS("Completed Projects"),
    MESSAGES("Messages"),
    PAYMENTS("Payments & Escrow"),
    REVIEWS("Reviews"),
    NOTIFICATIONS("Notifications"),
    PROFILE("Profile")
}

@Composable
fun ClientDashboard(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser ?: return
    val allProjects by viewModel.allProjects.collectAsState()
    val allProposals by viewModel.allProposals.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val notifications by viewModel.roleNotifications.collectAsState()

    val tabIndex by viewModel.selectedClientTab.collectAsState()
    val activeTab = ClientTab.entries.getOrElse(tabIndex) { ClientTab.MY_PROJECTS }

    val myProjects = allProjects.filter { it.clientId == user.id || it.clientName.contains(user.name, ignoreCase = true) || user.id == "user_client_main" }
    val myProposals = allProposals.filter { prop -> myProjects.any { it.id == prop.projectId } }
    val hiredProposals = myProposals.filter { it.status == "HIRED" || it.status == "WORK_SUBMITTED" || it.status == "COMPLETED" }

    // Post Project form state
    var projTitle by remember { mutableStateOf("") }
    var projDesc by remember { mutableStateOf("") }
    var projCategory by remember { mutableStateOf("Web Development") }
    var projType by remember { mutableStateOf("CAMPUS") }
    var projSkills by remember { mutableStateOf("React, Node.js, UI/UX") }
    var projBudget by remember { mutableStateOf("8000") }
    var projDeadline by remember { mutableStateOf("15 Oct 2026") }

    // Approval / Review dialog
    var selectedProposalForApproval by remember { mutableStateOf<ProposalEntity?>(null) }
    var reviewRating by remember { mutableStateOf(5f) }
    var reviewComment by remember { mutableStateOf("Excellent execution, highly recommended!") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FreeverseBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Client Header
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF134E4A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF0D9488)) {
                        Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp).size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(user.name, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color.White)
                        Text(user.organization, fontSize = 11.sp, color = Color(0xFF99F6E4))
                    }
                }
                IconButton(onClick = { viewModel.isLogoutConfirmOpen.value = true }) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color(0xFFFCA5A5))
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
            ClientTab.entries.forEach { tab ->
                val isSelected = activeTab == tab
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color(0xFF0D9488) else Color.White,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                    modifier = Modifier.clickable { viewModel.selectedClientTab.value = tab.ordinal }
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
            ClientTab.POST_PROJECT -> {
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("COMMISSION A NEW STUDENT PROJECT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
                        OutlinedTextField(value = projTitle, onValueChange = { projTitle = it }, label = { Text("Project Title") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = projDesc, onValueChange = { projDesc = it }, label = { Text("Scope of Work & Deliverables") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                        OutlinedTextField(value = projCategory, onValueChange = { projCategory = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = projSkills, onValueChange = { projSkills = it }, label = { Text("Required Skills") }, modifier = Modifier.fillMaxWidth())
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(value = projBudget, onValueChange = { projBudget = it }, label = { Text("Budget (₹)") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = projDeadline, onValueChange = { projDeadline = it }, label = { Text("Deadline") }, modifier = Modifier.weight(1f))
                        }
                        Button(
                            onClick = {
                                if (projTitle.isNotBlank()) {
                                    val b = projBudget.toIntOrNull() ?: 5000
                                    viewModel.postProject(projTitle, projDesc, projCategory, projType, projSkills, b, projDeadline)
                                    projTitle = ""
                                    projDesc = ""
                                    viewModel.selectedClientTab.value = ClientTab.MY_PROJECTS.ordinal
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text("Publish Project & Notify Student Freelancers", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            ClientTab.MY_PROJECTS -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("MY COMMISSIONED PROJECTS (${myProjects.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
                    myProjects.forEach { p ->
                        Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(p.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                    Text("₹${p.budget}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = FreeverseSuccess)
                                }
                                Text(p.description, fontSize = 11.5.sp, color = FreeverseTextSecondary, maxLines = 2)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Proposals: ${p.proposalCount} • Status: ${p.status}", fontSize = 11.sp, color = Color(0xFF0D9488), fontWeight = FontWeight.Bold)
                                    Text("Progress: ${p.progressPercent}%", fontSize = 11.sp, color = FreeversePrimary)
                                }
                            }
                        }
                    }
                }
            }
            ClientTab.PROPOSALS -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("PROPOSALS RECEIVED FOR REVIEW (${myProposals.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
                    myProposals.forEach { prop ->
                        Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(prop.studentName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Bid: ₹${prop.proposedBudget}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = FreeverseSuccess)
                                }
                                Text("Project: ${prop.projectTitle}", fontSize = 11.sp, color = FreeversePrimary)
                                Text("“${prop.coverMessage}”", fontSize = 11.5.sp, color = FreeverseTextSecondary)
                                Text("Delivery: ${prop.expectedDelivery} • Status: ${prop.status}", fontSize = 10.5.sp, color = FreeverseTextSecondary)
                                if (prop.status == "PENDING") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { viewModel.hireStudent(prop) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Hire Freelancer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            ClientTab.FIND_TALENT -> {
                val talent = allUsers.filter { it.role == "MEMBER" || it.role == "CLUB_COORDINATOR" }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("SEARCH STUDENT TALENT DIRECTORY (${talent.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
                    talent.forEach { t ->
                        Surface(shape = RoundedCornerShape(10.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(t.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Skills: ${t.skills}", fontSize = 11.sp, color = FreeversePrimary)
                                Text("Rate: ₹${t.hourlyRate}/hr • Rating: ⭐${t.rating}", fontSize = 10.5.sp, color = FreeverseTextSecondary)
                            }
                        }
                    }
                }
            }
            ClientTab.HIRED_FREELANCERS, ClientTab.ACTIVE_PROJECTS -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("ACTIVE CONTRACTS & HIRES (${hiredProposals.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
                    hiredProposals.forEach { h ->
                        Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(h.projectTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Freelancer: ${h.studentName} • Status: ${h.status}", fontSize = 11.sp, color = FreeversePrimary)
                                if (h.status == "WORK_SUBMITTED") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    h.submissionNotes?.let {
                                        Text("Submitted Work: $it", fontSize = 11.sp, color = FreeverseTextSecondary)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Button(
                                        onClick = { selectedProposalForApproval = h },
                                        colors = ButtonDefaults.buttonColors(containerColor = FreeverseSuccess)
                                    ) {
                                        Text("Approve Work & Release Payment", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            ClientTab.COMPLETED_PROJECTS -> {
                val completed = myProjects.filter { it.status == "COMPLETED" }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("COMPLETED PROJECTS (${completed.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
                    completed.forEach { comp ->
                        Surface(shape = RoundedCornerShape(10.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(comp.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Delivered by: ${comp.hiredStudentName ?: "Freelancer"} • Budget: ₹${comp.budget}", fontSize = 11.sp, color = FreeverseSuccess)
                            }
                        }
                    }
                }
            }
            ClientTab.MESSAGES -> {
                val messages by viewModel.allChatMessages.collectAsState()
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("FREELANCER MESSAGES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
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
            ClientTab.PAYMENTS -> {
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("CLIENT ESCROW & TOTAL SPENT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("₹${user.totalSpent}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = FreeverseTextPrimary)
                        Text("All milestones are held in escrow until deliverables are approved by you.", fontSize = 11.sp, color = FreeverseTextSecondary)
                    }
                }
            }
            ClientTab.REVIEWS -> {
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Freelancer Ratings & Recommendations", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Give back to campus engineers by writing detailed performance testimonials.", fontSize = 11.5.sp, color = FreeverseTextSecondary)
                    }
                }
            }
            ClientTab.NOTIFICATIONS -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("CLIENT NOTIFICATIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
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
            ClientTab.PROFILE -> {
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(user.organization, fontSize = 12.sp, color = FreeverseTextSecondary)
                        Text(user.email, fontSize = 11.5.sp, color = FreeversePrimary)
                    }
                }
            }
        }
    }

    // Modal: Approve & Review
    selectedProposalForApproval?.let { prop ->
        AlertDialog(
            onDismissRequest = { selectedProposalForApproval = null },
            title = { Text("Approve & Release Payment (₹${prop.proposedBudget})") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Deliverables verified for '${prop.projectTitle}'. Funds will be transferred to ${prop.studentName}.", fontSize = 11.5.sp, color = FreeverseTextSecondary)
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Client Review Comment") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.approveProject(prop, reviewRating, reviewComment)
                        selectedProposalForApproval = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FreeverseSuccess)
                ) {
                    Text("Approve & Release")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProposalForApproval = null }) { Text("Cancel") }
            }
        )
    }
}
