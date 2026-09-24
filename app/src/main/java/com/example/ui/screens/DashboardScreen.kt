package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.ProposalEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel
import com.example.ui.viewmodel.ScreenNav

@Composable
fun DashboardScreen(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val proposals by viewModel.allProposals.collectAsState()
    val projects by viewModel.allProjects.collectAsState()

    var submitWorkNotes by remember { mutableStateOf("") }
    var selectedProposalForWorkSubmit by remember { mutableStateOf<ProposalEntity?>(null) }
    var selectedProposalForClientReview by remember { mutableStateOf<ProposalEntity?>(null) }
    var reviewRating by remember { mutableStateOf(5f) }
    var reviewComment by remember { mutableStateOf("Outstanding technical execution, responsive communication, and delivered on time!") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FreeverseBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Branded Platform Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.img_freeverse_logo),
                        contentDescription = "FREEVERSE Logo",
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("FREEVERSE DASHBOARD", fontWeight = FontWeight.Black, fontSize = 13.sp, color = FreeversePrimary)
                            Spacer(modifier = Modifier.width(5.dp))
                            Surface(shape = RoundedCornerShape(3.dp), color = Color(0xFFEBF3FF)) {
                                Text("KIT", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = FreeverseSecondary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                        Text("Kangeyam Institute of Technology • Autonomous", fontSize = 9.sp, color = FreeverseTextSecondary)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FreeverseSoftLavender
                ) {
                    Text(
                        text = "LIVE",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreeversePrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // User Profile Header Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = FreeverseSoftLavender,
                            border = androidx.compose.foundation.BorderStroke(2.dp, FreeversePrimary),
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentUser.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FreeversePrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(currentUser.name, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Verified, contentDescription = null, tint = FreeversePrimary, modifier = Modifier.size(16.dp))
                            }
                            Text(currentUser.email, fontSize = 11.sp, color = FreeverseTextSecondary)
                            Text(currentUser.organization, fontSize = 11.sp, color = FreeverseSecondary, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = when (currentUser.role) {
                            "STUDENT" -> FreeverseSoftLavender
                            "COLLEGE_CLIENT" -> Color(0xFFEBF3FF)
                            "EXTERNAL_CLIENT" -> Color(0xFFE0F7FA)
                            else -> Color(0xFFFEF3C7)
                        }
                    ) {
                        Text(
                            text = when (currentUser.role) {
                                "STUDENT" -> "Student Freelancer"
                                "COLLEGE_CLIENT" -> "College Client"
                                "EXTERNAL_CLIENT" -> "External Client"
                                else -> "Platform Admin"
                            },
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (currentUser.role) {
                                "STUDENT" -> FreeversePrimary
                                "COLLEGE_CLIENT" -> FreeverseSecondary
                                "EXTERNAL_CLIENT" -> FreeverseAccentCyan
                                else -> FreeverseWarning
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // ======================================================
        // 1. STUDENT FREELANCER DASHBOARD
        // ======================================================
        if (currentUser.role == "STUDENT") {
            // SKILL PASSPORT
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD6D7FB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = FreeversePrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "VERIFIED SKILL PASSPORT",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = FreeversePrimary,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFE8F8F5)) {
                            Text("✓ KIT Verified", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FreeverseSuccess, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Experience metrics
                    Surface(shape = RoundedCornerShape(10.dp), color = FreeverseSoftLavender) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${currentUser.projectsCompleted}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FreeversePrimary)
                                Text("Total Projects", fontSize = 9.5.sp, color = FreeverseTextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("★ ${currentUser.rating}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FreeverseWarning)
                                Text("Reputation", fontSize = 9.5.sp, color = FreeverseTextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("5", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FreeverseSecondary)
                                Text("College Gigs", fontSize = 9.5.sp, color = FreeverseTextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("3", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FreeverseSuccess)
                                Text("Client Projects", fontSize = 9.5.sp, color = FreeverseTextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Skills Progress
                    val skills = currentUser.skills.split(",").map { it.trim() }
                    skills.take(4).forEachIndexed { index, sk ->
                        val percent = when (index) {
                            0 -> 92
                            1 -> 88
                            2 -> 80
                            else -> 75
                        }
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(sk, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("$percent% (✓ Project Verified)", fontSize = 10.5.sp, color = FreeverseSuccess, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            LinearProgressIndicator(
                                progress = { percent / 100f },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = FreeversePrimary,
                                trackColor = Color(0xFFE2E8F0)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.isSkillAssessmentOpen.value = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary)
                    ) {
                        Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Take Proof-of-Skill Assessment", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    }
                }
            }

            // MY APPLICATIONS & PROPOSALS
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "MY APPLICATIONS & ACTIVE WORK",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreeversePrimary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val myProposals = proposals.filter { it.studentId == currentUser.id }
                    if (myProposals.isEmpty()) {
                        Text("No active applications yet. Browse project marketplace to apply!", fontSize = 12.sp, color = FreeverseTextSecondary)
                    } else {
                        myProposals.forEach { prop ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = FreeverseSoftLavender,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(prop.projectTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, modifier = Modifier.weight(1f))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = when (prop.status) {
                                                "HIRED" -> FreeverseSuccess
                                                "WORK_SUBMITTED" -> FreeverseWarning
                                                "COMPLETED" -> FreeversePrimary
                                                else -> Color(0xFF64748B)
                                            }
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
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Proposed Bid: ₹${prop.proposedBudget} • Delivery: ${prop.expectedDelivery}", fontSize = 11.sp, color = FreeverseTextSecondary)

                                    if (prop.status == "HIRED") {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { selectedProposalForWorkSubmit = prop },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = FreeverseSuccess),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Submit Completed Work for Review", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (prop.status == "COMPLETED" && prop.clientReviewComment != null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Client Review (${prop.clientRating}★): \"${prop.clientReviewComment}\"", fontSize = 11.sp, color = FreeversePrimary, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ======================================================
        // 2. COLLEGE CLIENT / EXTERNAL CLIENT DASHBOARD
        // ======================================================
        if (currentUser.role == "COLLEGE_CLIENT" || currentUser.role == "EXTERNAL_CLIENT") {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD6D7FB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentUser.role == "COLLEGE_CLIENT") "COLLEGE CLIENT CONSOLE" else "INDUSTRY CLIENT PORTAL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FreeversePrimary,
                            letterSpacing = 0.5.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (currentUser.role == "EXTERNAL_CLIENT") {
                                OutlinedButton(
                                    onClick = { viewModel.isTeamBuilderOpen.value = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Team Builder", fontSize = 10.5.sp)
                                }
                            }
                            Button(
                                onClick = { viewModel.isPostProjectOpen.value = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("+ Post Project", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("RECEIVED APPLICATIONS & WORKFLOW", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = FreeverseTextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))

                    proposals.forEach { prop ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = FreeverseSoftLavender,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(prop.studentName, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                        Text("For: ${prop.projectTitle}", fontSize = 11.sp, color = FreeversePrimary, maxLines = 1)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (prop.status == "HIRED") FreeverseSuccess else FreeversePrimary
                                    ) {
                                        Text(prop.status, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Pitch: ${prop.coverMessage}", fontSize = 11.sp, color = FreeverseTextSecondary, maxLines = 2)

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Bid: ₹${prop.proposedBudget} (⏱ ${prop.expectedDelivery})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FreeverseTextPrimary)

                                    if (prop.status == "PENDING") {
                                        Button(
                                            onClick = { viewModel.hireStudent(prop) },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                        ) {
                                            Text("Hire Student", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else if (prop.status == "WORK_SUBMITTED") {
                                        Button(
                                            onClick = { selectedProposalForClientReview = prop },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = FreeverseSuccess),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                        ) {
                                            Text("Approve & Review", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else if (prop.status == "HIRED") {
                                        Text("Active in Project Workspace", fontSize = 11.sp, color = FreeverseSuccess, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ======================================================
        // 3. ADMIN DASHBOARD
        // ======================================================
        if (currentUser.role == "ADMIN") {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFEF3C7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "FREEVERSE PLATFORM ADMINISTRATION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreeverseWarning,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Key platform stats
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(shape = RoundedCornerShape(8.dp), color = FreeverseSoftLavender, modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("6", fontWeight = FontWeight.Black, fontSize = 16.sp, color = FreeversePrimary)
                                Text("Core Leaders", fontSize = 9.5.sp, color = FreeverseTextSecondary)
                            }
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = FreeverseSoftLavender, modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${projects.size}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = FreeversePrimary)
                                Text("Projects", fontSize = 9.5.sp, color = FreeverseTextSecondary)
                            }
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = FreeverseSoftLavender, modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("98.4%", fontWeight = FontWeight.Black, fontSize = 16.sp, color = FreeverseSuccess)
                                Text("System Health", fontSize = 9.5.sp, color = FreeverseTextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("PLATFORM CONTROLS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    val adminActions = listOf(
                        "Verify Student Portfolios & GitHub Repos",
                        "Moderate Department Project Postings",
                        "Manage Campus Hackathon & Workshop Submissions",
                        "Freeverse Platform Security & Access Logs"
                    )

                    adminActions.forEach { act ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFAFAFE),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(act, fontSize = 11.5.sp, color = FreeverseTextPrimary)
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FreeverseSuccess, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Submit Completed Work
    selectedProposalForWorkSubmit?.let { prop ->
        AlertDialog(
            onDismissRequest = { selectedProposalForWorkSubmit = null },
            title = { Text("Submit Completed Work") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Project: ${prop.projectTitle}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = submitWorkNotes,
                        onValueChange = { submitWorkNotes = it },
                        label = { Text("Submission Notes / Deliverable Links (e.g. GitHub repo, Figma URL)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitProjectWork(prop.id, prop.projectId, submitWorkNotes)
                        selectedProposalForWorkSubmit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary)
                ) {
                    Text("Submit for Client Approval")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProposalForWorkSubmit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal: Client Review & Approve Work
    selectedProposalForClientReview?.let { prop ->
        AlertDialog(
            onDismissRequest = { selectedProposalForClientReview = null },
            title = { Text("Approve Work & Rate Student") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Reviewing ${prop.studentName} for '${prop.projectTitle}'", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Rating: ${reviewRating.toInt()} Stars", fontSize = 12.sp, color = FreeverseWarning, fontWeight = FontWeight.Bold)
                    Slider(
                        value = reviewRating,
                        onValueChange = { reviewRating = it },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Public Testimonial / Feedback") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.approveProjectWork(prop.id, prop.projectId, reviewRating, reviewComment)
                        selectedProposalForClientReview = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FreeverseSuccess)
                ) {
                    Text("Approve & Award Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProposalForClientReview = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
