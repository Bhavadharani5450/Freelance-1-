package com.example.ui.dialogs

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
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel

// 1. Proposal Submission Dialog
@Composable
fun ProposalDialog(
    project: ProjectEntity,
    viewModel: FreeverseViewModel,
    onDismiss: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var coverMessage by remember { mutableStateOf("I am very interested in collaborating on this project. I have extensive experience building scalable solutions at KIT.") }
    var relevantSkills by remember { mutableStateOf(currentUser.skills) }
    var portfolioLink by remember { mutableStateOf(currentUser.portfolio) }
    var expectedDelivery by remember { mutableStateOf("7 Days") }
    var proposedBudget by remember { mutableStateOf(project.budget.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Submit Proposal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = FreeverseTextPrimary
                        )
                        Text(
                            text = project.title,
                            fontSize = 12.sp,
                            color = FreeversePrimary,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // AI Proposal Assistant Snippet
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FreeverseSoftLavender,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6D7FB))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "AI Assistant",
                            tint = FreeversePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Proposal Assistant matched your Skill Passport (${currentUser.skills.take(25)}...) with this project's requirements!",
                            fontSize = 11.5.sp,
                            color = FreeversePrimary,
                            lineHeight = 15.sp
                        )
                    }
                }

                OutlinedTextField(
                    value = coverMessage,
                    onValueChange = { coverMessage = it },
                    label = { Text("Cover Message / Pitch") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = relevantSkills,
                    onValueChange = { relevantSkills = it },
                    label = { Text("Relevant Skills") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = expectedDelivery,
                        onValueChange = { expectedDelivery = it },
                        label = { Text("Delivery Time") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = proposedBudget,
                        onValueChange = { proposedBudget = it },
                        label = { Text("Bid (₹)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                OutlinedTextField(
                    value = portfolioLink,
                    onValueChange = { portfolioLink = it },
                    label = { Text("Portfolio / GitHub URL") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Button(
                    onClick = {
                        val budgetInt = proposedBudget.toIntOrNull() ?: project.budget
                        viewModel.applyForProject(
                            projectId = project.id,
                            projectTitle = project.title,
                            coverMessage = coverMessage,
                            relevantSkills = relevantSkills,
                            portfolioLink = portfolioLink,
                            expectedDelivery = expectedDelivery,
                            proposedBudget = budgetInt
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary)
                ) {
                    Text("Submit Real Proposal", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 2. Post Project Dialog
@Composable
fun PostProjectDialog(
    viewModel: FreeverseViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Web Development") }
    var projectType by remember { mutableStateOf("CAMPUS") }
    var skills by remember { mutableStateOf("React, Node.js, Tailwind CSS") }
    var budget by remember { mutableStateOf("5000") }
    var deadline by remember { mutableStateOf("25 Oct 2026") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Post New Project",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreeverseTextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Project Title") },
                    placeholder = { Text("e.g. Club Website Redesign") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Requirements") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(10.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = projectType,
                        onValueChange = { projectType = it },
                        label = { Text("Type (CAMPUS/INDUSTRY)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                OutlinedTextField(
                    value = skills,
                    onValueChange = { skills = it },
                    label = { Text("Required Skills (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = budget,
                        onValueChange = { budget = it },
                        label = { Text("Budget (₹)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        label = { Text("Deadline") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Button(
                    onClick = {
                        if (title.isNotBlank() && description.isNotBlank()) {
                            viewModel.postNewProject(
                                title = title,
                                description = description,
                                category = category,
                                projectType = projectType,
                                skills = skills,
                                budget = budget.toIntOrNull() ?: 3000,
                                deadline = deadline
                            )
                        } else {
                            viewModel.showSnackbar("Please enter project title and description.")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary)
                ) {
                    Text("Publish Project to Freeverse", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 3. Skill Assessment Dialog
@Composable
fun SkillAssessmentDialog(
    viewModel: FreeverseViewModel,
    onDismiss: () -> Unit
) {
    var q1Answer by remember { mutableStateOf(0) }
    var q2Answer by remember { mutableStateOf(0) }
    var q3Answer by remember { mutableStateOf(0) }
    var hasSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = FreeverseSuccess)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Proof-of-Skill Assessment",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FreeverseTextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Topic: Modern Full-Stack & React Architecture (20 Mins)",
                    fontSize = 12.sp,
                    color = FreeverseTextSecondary,
                    fontWeight = FontWeight.Medium
                )

                if (!hasSubmitted) {
                    // Q1
                    Text(
                        text = "1. In React & Jetpack Compose, how should state be managed to minimize unnecessary re-renders?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = q1Answer == 1, onClick = { q1Answer = 1 })
                            Text("Use global mutable variables directly", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = q1Answer == 2, onClick = { q1Answer = 2 })
                            Text("Use StateFlow / remember with unidirectional data flow", fontSize = 12.sp)
                        }
                    }

                    // Q2
                    Text(
                        text = "2. What is the role of Room / SQLite in offline-first Android apps?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = q2Answer == 1, onClick = { q2Answer = 1 })
                            Text("Local persistence abstraction with reactive Flow queries", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = q2Answer == 2, onClick = { q2Answer = 2 })
                            Text("Only for temporary session cookies", fontSize = 12.sp)
                        }
                    }

                    // Q3
                    Text(
                        text = "3. How does Freeverse Match calculate candidate relevance?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = q3Answer == 1, onClick = { q3Answer = 1 })
                            Text("Skill (40%) + Portfolio (25%) + Availability (15%) + History", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = q3Answer == 2, onClick = { q3Answer = 2 })
                            Text("Random ranking based on signup date", fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = {
                            var calculated = 0
                            if (q1Answer == 2) calculated += 34
                            if (q2Answer == 1) calculated += 33
                            if (q3Answer == 1) calculated += 33
                            score = calculated
                            hasSubmitted = true
                            viewModel.completeAssessment("Full-Stack & React Architecture", score, "React, StateFlow, Room, Architecture")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary)
                    ) {
                        Text("Evaluate & Verify My Skills", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FreeverseSoftLavender,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FreeverseSuccess,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Assessment Passed: $score%",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = FreeversePrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Badge '✓ Assessment Verified' added to your Freeverse Skill Passport!",
                                fontSize = 12.sp,
                                color = FreeverseTextSecondary
                            )
                        }
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary)
                    ) {
                        Text("View Updated Skill Passport", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// 4. Team Builder Dialog
@Composable
fun TeamBuilderDialog(
    viewModel: FreeverseViewModel,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GroupAdd, contentDescription = null, tint = FreeversePrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Student Team Builder",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = FreeverseTextPrimary
                            )
                        }
                        Text(
                            text = "AI-matched complementary student teams",
                            fontSize = 11.5.sp,
                            color = FreeverseTextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FreeverseSoftLavender,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6D7FB))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TEAM COMPATIBILITY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FreeversePrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = FreeverseSuccess
                            ) {
                                Text(
                                    text = "94% MATCH",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Suggested Squad for: 'AI Agriculture & IoT Platform'",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FreeverseTextPrimary
                        )
                    }
                }

                // Team members
                val squad = listOf(
                    Triple("Frontend Architect", "Bhavadharani S", "React, Jetpack Compose, Tailwind"),
                    Triple("UI/UX Designer", "Nevitha S V", "Figma, Design Tokens, Prototyping"),
                    Triple("Backend & Cloud", "Dyanidhi N", "Spring Boot, PostgreSQL, Docker"),
                    Triple("Mobile / IoT", "Keerthana B.K", "Flutter, Kotlin, Firebase, Sensors")
                )

                squad.forEach { (role, name, skills) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFAFAFE),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = role, fontSize = 10.5.sp, color = FreeversePrimary, fontWeight = FontWeight.Bold)
                                Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FreeverseTextPrimary)
                                Text(text = skills, fontSize = 10.5.sp, color = FreeverseTextSecondary)
                            }
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FreeverseSuccess, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Button(
                    onClick = {
                        viewModel.showSnackbar("Squad invitation sent to all 4 team members!")
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Invite Full Team (4 Members)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 5. Notifications Drawer / Dialog
@Composable
fun NotificationsDialog(
    viewModel: FreeverseViewModel,
    onDismiss: () -> Unit
) {
    val notifications by viewModel.allNotifications.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifications",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreeverseTextPrimary
                    )
                    Row {
                        TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
                            Text("Mark all read", fontSize = 11.5.sp, color = FreeversePrimary)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                if (notifications.isEmpty()) {
                    Text(
                        text = "No notifications yet.",
                        fontSize = 13.sp,
                        color = FreeverseTextSecondary,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications) { notif ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (notif.isRead) Color(0xFFF8F9FA) else FreeverseSoftLavender,
                                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = when (notif.type) {
                                            "HIRE" -> Icons.Default.Handshake
                                            "REVIEW" -> Icons.Default.Star
                                            "EVENT" -> Icons.Default.Event
                                            else -> Icons.Default.Notifications
                                        },
                                        contentDescription = null,
                                        tint = FreeversePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = notif.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FreeverseTextPrimary
                                        )
                                        Text(
                                            text = notif.message,
                                            fontSize = 11.5.sp,
                                            color = FreeverseTextSecondary
                                        )
                                        Text(
                                            text = notif.timeAgo,
                                            fontSize = 10.sp,
                                            color = Color(0xFF94A3B8),
                                            fontWeight = FontWeight.Medium
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
}

// 6. Freelancer Profile Dialog
@Composable
fun FreelancerProfileDialog(
    user: UserEntity,
    viewModel: FreeverseViewModel,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Partnered Logo Verification Ribbon
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = FreeverseSoftLavender,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6D7FB))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.img_freeverse_logo),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "FREEVERSE VERIFIED STUDENT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FreeversePrimary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = FreeverseSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Header
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
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = user.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FreeversePrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FreeverseTextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = "Verified Student",
                                    tint = FreeversePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "${user.department} • ${user.year}",
                                fontSize = 11.5.sp,
                                color = FreeverseTextSecondary
                            )
                            Text(
                                text = user.organization,
                                fontSize = 10.5.sp,
                                color = FreeverseSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Stats Bar
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FreeverseSoftLavender
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("★ ${user.rating}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FreeverseWarning)
                            Text("Rating", fontSize = 10.sp, color = FreeverseTextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${user.projectsCompleted}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FreeversePrimary)
                            Text("Completed", fontSize = 10.sp, color = FreeverseTextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("₹${user.hourlyRate}/hr", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FreeverseSuccess)
                            Text("Rate", fontSize = 10.sp, color = FreeverseTextSecondary)
                        }
                    }
                }

                // Bio
                Text(
                    text = user.bio,
                    fontSize = 12.5.sp,
                    color = FreeverseTextPrimary,
                    lineHeight = 17.sp
                )

                // Verified Skill Passport
                Text(
                    text = "VERIFIED SKILL PASSPORT",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreeversePrimary,
                    letterSpacing = 0.5.sp
                )

                val skillsList = user.skills.split(",").map { it.trim() }
                skillsList.forEach { skill ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = skill, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text(text = "✓ Project Verified", fontSize = 10.5.sp, color = FreeverseSuccess, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        LinearProgressIndicator(
                            progress = { 0.88f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = FreeversePrimary,
                            trackColor = Color(0xFFE2E8F0)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }

                // Links
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = FreeverseTextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("GitHub", fontSize = 11.sp, color = FreeverseTextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Link, contentDescription = null, tint = FreeverseTextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Portfolio", fontSize = 11.sp, color = FreeverseTextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.showSnackbar("Opening chat with ${user.name}...")
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Message")
                    }

                    Button(
                        onClick = {
                            viewModel.showSnackbar("Direct project invitation sent to ${user.name}!")
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary)
                    ) {
                        Text("Hire Me", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
