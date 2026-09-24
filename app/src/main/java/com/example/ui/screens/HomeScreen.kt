package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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

@Composable
fun HomeScreen(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val freelancers by viewModel.filteredFreelancers.collectAsState()
    val projects by viewModel.allProjects.collectAsState()
    val services by viewModel.allServices.collectAsState()
    val gigs by viewModel.allGigs.collectAsState()
    val events by viewModel.allEvents.collectAsState()

    var showMatchSimulator by remember { mutableStateOf(false) }
    var selectedEventTab by remember { mutableStateOf("All") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FreeverseBackground)
            .verticalScroll(scrollState)
    ) {

        // ==========================================
        // 2. HERO SECTION
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFEDE9FE), FreeverseBackground)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Community Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6D7FB))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = FreeverseWarning,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "FREEVERSE • STUDENT FREELANCING PLATFORM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FreeversePrimary,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Headline
                Text(
                    text = "Turn Your Skills Into\nReal-World Opportunities",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    color = FreeverseTextPrimary,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Tagline & Supporting text
                Text(
                    text = "“Your Campus. Your Skills. Your Opportunities.”",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreeversePrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "FREEVERSE connects student talent with real projects, campus opportunities and clients looking for creative skills.",
                    fontSize = 13.sp,
                    color = FreeverseTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Floating Skill Chips
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("✦ Skills", "✦ Opportunities", "✦ Freelance", "✦ Verified Passports", "✦ Campus Gigs").forEach { chip ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.9f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Text(
                                text = chip,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FreeversePrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Hero CTAs
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.navigateTo(ScreenNav.FREELANCERS) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Explore Freelancers", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(ScreenNav.PROJECTS) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, FreeversePrimary)
                        ) {
                            Text("Find a Project", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FreeversePrimary)
                        }

                        Button(
                            onClick = { viewModel.isAuthDialogOpen.value = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FreeverseSecondary)
                        ) {
                            Text("Join Freeverse", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Hero Collaborative Image Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_students),
                            contentDescription = "Students Collaborating",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Vibrant Campus Talent", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Verified portfolios across 6 engineering streams", fontSize = 11.sp, color = FreeverseTextSecondary)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = FreeverseSoftLavender
                            ) {
                                Text("50+ Active Gigs", fontSize = 11.sp, color = FreeversePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // 3. STUDENT WORKFLOW (QUICK WORKFLOW)
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "HOW FREEVERSE WORKS",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = FreeversePrimary,
                letterSpacing = 1.sp
            )
            Text(
                text = "From Skill to Verified Portfolio",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = FreeverseTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            val workflowSteps = listOf(
                Triple("1. Profile", "Setup student profile with verified college department", Icons.Default.Person),
                Triple("2. Skills", "Add skills & complete Proof-of-Skill assessments", Icons.Default.Psychology),
                Triple("3. Project", "Discover campus gigs and industry client requirements", Icons.Default.Work),
                Triple("4. Payment", "Deliver work, get approved and receive stipends", Icons.Default.Payments),
                Triple("5. Portfolio", "Automatic case-study generated for your Skill Passport", Icons.Default.Verified)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                workflowSteps.forEach { (title, desc, icon) ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                        modifier = Modifier.width(160.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(FreeverseSoftLavender),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = FreeversePrimary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FreeverseTextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(desc, fontSize = 11.sp, color = FreeverseTextSecondary, lineHeight = 15.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ==========================================
        // 4. ABOUT FREEVERSE (WITH KIT CAMPUS PHOTO)
        // ==========================================
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // College Campus Photo
                Image(
                    painter = painterResource(id = R.drawable.img_kit_campus),
                    contentDescription = "Kangeyam Institute of Technology Campus",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "ABOUT FREEVERSE",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreeversePrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Your Campus. Your Skills. Your Opportunities.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreeverseTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Freeverse is a student-driven community designed to connect campus talent with meaningful opportunities at Kangeyam Institute of Technology.",
                    fontSize = 12.5.sp,
                    color = FreeverseTextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Three Pillar Cards: VISION, MISSION, OBJECTIVE
                val pillars = listOf(
                    Triple("VISION", "Build a campus where every student can turn their skills into real opportunities.", Icons.Default.Visibility),
                    Triple("MISSION", "Connect students, clients, clubs and organizations through skills, projects and collaboration.", Icons.Default.Flag),
                    Triple("OBJECTIVE", "Help students build experience, portfolios, professional connections and confidence.", Icons.Default.TrendingUp)
                )

                pillars.forEach { (heading, text, icon) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = FreeverseSoftLavender,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(icon, contentDescription = null, tint = FreeversePrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(heading, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FreeversePrimary)
                                Text(text, fontSize = 11.5.sp, color = FreeverseTextPrimary, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ==========================================
        // 5. WHY STUDENTS JOIN FREEVERSE
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "WHY STUDENTS JOIN FREEVERSE",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = FreeversePrimary,
                letterSpacing = 1.sp
            )
            Text(
                text = "Empowering Next-Gen Creators",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = FreeverseTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            val benefits = listOf(
                Pair("Build Your Portfolio", "Verified case-studies from real projects"),
                Pair("Find Real Projects", "Campus and industry client demands"),
                Pair("Earn Through Skills", "Get rewarded for design, coding & media"),
                Pair("Meet Other Creators", "Collaborate with talented campus peers"),
                Pair("Learn in Workshops", "Masterclasses on freelancing & modern tech"),
                Pair("Build Reputation", "Verified reviews on your Skill Passport"),
                Pair("Connect With Clients", "Startups, alumni, and local businesses"),
                Pair("Discover Campus Gigs", "Micro-tasks from college clubs & departments")
            )

            // Grid of benefits (2 columns)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in benefits.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FreeverseSuccess, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(benefits[i].first, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = FreeverseTextPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(benefits[i].second, fontSize = 10.5.sp, color = FreeverseTextSecondary, lineHeight = 14.sp)
                            }
                        }

                        if (i + 1 < benefits.size) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FreeverseSuccess, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(benefits[i + 1].first, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = FreeverseTextPrimary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(benefits[i + 1].second, fontSize = 10.5.sp, color = FreeverseTextSecondary, lineHeight = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ==========================================
        // 6. FREEVERSE MATCH (SIGNATURE FEATURE)
        // ==========================================
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD6D7FB)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Hub, contentDescription = null, tint = FreeversePrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "FREEVERSE MATCH",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = FreeversePrimary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = FreeverseSoftLavender
                    ) {
                        Text(
                            text = "Transparent Rules",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = FreeversePrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Find the right student for the right project.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreeverseTextPrimary
                )
                Text(
                    text = "Rule-based transparent scoring matching students directly with project needs.",
                    fontSize = 11.5.sp,
                    color = FreeverseTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 5 Pillars Breakdown
                val scoringRules = listOf(
                    Pair("Required Skill Match", "40%"),
                    Pair("Portfolio Relevance", "25%"),
                    Pair("Availability", "15%"),
                    Pair("Completed Projects", "10%"),
                    Pair("Reputation / Rating", "10%")
                )

                scoringRules.forEach { (metric, weight) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(metric, fontSize = 12.sp, color = FreeverseTextPrimary)
                        Text(weight, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FreeversePrimary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { showMatchSimulator = !showMatchSimulator },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary)
                ) {
                    Icon(Icons.Default.ManageSearch, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (showMatchSimulator) "Hide Matching Simulator" else "Find My Freelancer", fontWeight = FontWeight.Bold)
                }

                if (showMatchSimulator) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FreeverseSoftLavender,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("FREEVERSE MATCH RESULTS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = FreeversePrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Top Match: 95% Match • Bhavadharani S (Full-Stack React + Node)", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            Text("✓ 100% skill match (React, Node) • 14 completed projects • 4.95★ rating", fontSize = 11.sp, color = FreeverseTextSecondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val user = freelancers.find { it.name.contains("Bhavadharani") }
                                    if (user != null) viewModel.selectedFreelancerForProfile.value = user
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("View Matched Profile", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ==========================================
        // 7. FEATURED FREELANCERS
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DISCOVER STUDENT TALENT",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreeversePrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Featured Freelancers",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreeverseTextPrimary
                    )
                }
                TextButton(onClick = { viewModel.navigateTo(ScreenNav.FREELANCERS) }) {
                    Text("View All", color = FreeversePrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(freelancers.take(5)) { student ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                        modifier = Modifier
                            .width(220.dp)
                            .clickable { viewModel.selectedFreelancerForProfile.value = student }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = FreeverseSoftLavender,
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, FreeversePrimary),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = student.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FreeversePrimary
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFE8F8F5)
                                ) {
                                    Text(
                                        text = "✓ Verified",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FreeverseSuccess,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(student.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                            Text("${student.department} • ${student.year}", fontSize = 11.sp, color = FreeverseTextSecondary, maxLines = 1)

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Skills: ${student.skills}", fontSize = 10.5.sp, color = FreeversePrimary, maxLines = 1)

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("★ ${student.rating}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = FreeverseWarning)
                                Text("${student.projectsCompleted} Projects", fontSize = 11.sp, color = FreeverseTextSecondary)
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.selectedFreelancerForProfile.value = student },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Text("View Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ==========================================
        // 8. STUDENT SERVICES
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "STUDENT SERVICES",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreeversePrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Popular Packaged Services",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreeverseTextPrimary
                    )
                }
                TextButton(onClick = { viewModel.navigateTo(ScreenNav.SERVICES) }) {
                    Text("Explore", color = FreeversePrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                services.take(3).forEach { srv ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = FreeverseSoftLavender
                                ) {
                                    Text(
                                        text = srv.category,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FreeversePrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(srv.title, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                Text("By ${srv.freelancerName} (${srv.freelancerRole})", fontSize = 11.sp, color = FreeverseTextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Starting at", fontSize = 9.5.sp, color = FreeverseTextSecondary)
                                Text("₹${srv.startingPrice}", fontWeight = FontWeight.Black, fontSize = 15.sp, color = FreeversePrimary)
                                Text("⏱ ${srv.deliveryDays} Days", fontSize = 10.sp, color = FreeverseTextSecondary)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ==========================================
        // 9. PROJECT MARKETPLACE
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PROJECT MARKETPLACE",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreeversePrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Find Your Next Opportunity",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreeverseTextPrimary
                    )
                }
                TextButton(onClick = { viewModel.navigateTo(ScreenNav.PROJECTS) }) {
                    Text("View All", color = FreeversePrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                projects.take(3).forEach { proj ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (proj.projectType == "CAMPUS") Color(0xFFEBF3FF) else Color(0xFFF3E8FF)
                                ) {
                                    Text(
                                        text = if (proj.projectType == "CAMPUS") "CAMPUS PROJECT" else "INDUSTRY PROJECT",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (proj.projectType == "CAMPUS") FreeverseSecondary else FreeversePrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                                Text("Budget: ₹${proj.budget}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FreeverseSuccess)
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(proj.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(proj.description, fontSize = 11.5.sp, color = FreeverseTextSecondary, maxLines = 2, lineHeight = 16.sp)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Required: ${proj.requiredSkills}", fontSize = 11.sp, color = FreeversePrimary, fontWeight = FontWeight.SemiBold)

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Client: ${proj.clientName}", fontSize = 11.sp, color = FreeverseTextSecondary)
                                Button(
                                    onClick = { viewModel.selectedProjectForApply.value = proj },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text("Apply Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ==========================================
        // 10. CAMPUS GIGS (UNIQUE FEATURE)
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "CAMPUS GIGS",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = FreeversePrimary,
                letterSpacing = 1.sp
            )
            Text(
                text = "Quick Tasks From College Clubs",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = FreeverseTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                gigs.take(3).forEach { gig ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(gig.task, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Club: ${gig.clubOrDepartment} • ${gig.deadline}", fontSize = 11.sp, color = FreeverseTextSecondary)
                                Text("Skills: ${gig.skills}", fontSize = 10.5.sp, color = FreeversePrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("₹${gig.reward}", fontWeight = FontWeight.Black, fontSize = 15.sp, color = FreeverseSuccess)
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = { viewModel.applyForGig(gig) },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (gig.isApplied) Color(0xFFE2E8F0) else FreeversePrimary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    enabled = !gig.isApplied
                                ) {
                                    Text(
                                        text = if (gig.isApplied) "Applied" else "Apply",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (gig.isApplied) FreeverseTextSecondary else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ==========================================
        // 11. EVENTS & ACTIVITIES (FEATURED WORKSHOPS & HACKATHONS)
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "EVENTS & ACTIVITIES",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = FreeversePrimary,
                letterSpacing = 1.sp
            )
            Text(
                text = "Workshops, Hackathons & Contests",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = FreeverseTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Featured Workshop & Accelerator Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = FreeverseAccentCyan
                        ) {
                            Text("FEATURED WORKSHOP", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                        Text("21 August 2026", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "FREEVERSE FULL-STACK & FREELANCE ACCELERATOR",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "“Master high-impact portfolio building, client proposals, and modern app engineering with campus peers.”",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(shape = RoundedCornerShape(6.dp), color = Color.White.copy(alpha = 0.1f), modifier = Modifier.weight(1f)) {
                            Text("Time: 3:00 - 5:00 PM", fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(6.dp), textAlign = TextAlign.Center)
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = Color.White.copy(alpha = 0.1f), modifier = Modifier.weight(1f)) {
                            Text("Venue: Central Lab 3", fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(6.dp), textAlign = TextAlign.Center)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    val logoEvent = events.find { it.id == "evt_1" }
                    Button(
                        onClick = {
                            if (logoEvent != null) viewModel.registerForEvent(logoEvent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary)
                    ) {
                        Text(if (logoEvent?.isRegistered == true) "✓ Registration Confirmed" else "Register For Workshop", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ==========================================
        // 12. FREEVERSE CORE TEAM (EXACT AS SPECIFIED)
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "MEET THE TEAM",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = FreeversePrimary,
                letterSpacing = 1.sp
            )
            Text(
                text = "Freeverse Club Core Office Bearers",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = FreeverseTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // EXACT 6 NAMES & ROLES:
            val team = listOf(
                Pair("Bhavadharani S", "President"),
                Pair("Nevitha S V", "Secretary"),
                Pair("Keerthana B.K", "Vice President"),
                Pair("KAVYASSRI G", "Joint Secretary"),
                Pair("Dyanidhi N", "Treasurer"),
                Pair("Vaishnav M N", "Joint Treasurer")
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in team.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = FreeverseSoftLavender,
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, FreeversePrimary),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = team[i].first.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FreeversePrimary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(team[i].first, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                                Text(team[i].second, fontSize = 11.sp, color = FreeversePrimary, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Share, contentDescription = "LinkedIn", tint = FreeverseTextSecondary, modifier = Modifier.size(16.dp))
                                    Icon(Icons.Default.CameraAlt, contentDescription = "Instagram", tint = FreeverseTextSecondary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        if (i + 1 < team.size) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = FreeverseSoftLavender,
                                        border = androidx.compose.foundation.BorderStroke(1.5.dp, FreeversePrimary),
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = team[i + 1].first.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = FreeversePrimary
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(team[i + 1].first, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                                    Text(team[i + 1].second, fontSize = 11.sp, color = FreeversePrimary, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Share, contentDescription = "LinkedIn", tint = FreeverseTextSecondary, modifier = Modifier.size(16.dp))
                                        Icon(Icons.Default.CameraAlt, contentDescription = "Instagram", tint = FreeverseTextSecondary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ==========================================
        // 13. JOIN FREEVERSE CTA
        // ==========================================
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FreeversePrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ready to Turn Your Skills Into Real Experience?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Join hundreds of student creators, department clients, and industry partners on Freeverse.",
                    fontSize = 12.sp,
                    color = Color(0xFFE0E7FF),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { viewModel.isAuthDialogOpen.value = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Join Freeverse Now", fontWeight = FontWeight.Bold, color = FreeversePrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ==========================================
        // 14. FOOTER
        // ==========================================
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Freeverse Logo
                Image(
                    painter = painterResource(id = R.drawable.img_freeverse_logo),
                    contentDescription = "FREEVERSE Logo",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("FREEVERSE", fontWeight = FontWeight.Black, fontSize = 17.sp, color = FreeversePrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEBF3FF)) {
                        Text("CAMPUS ECOSYSTEM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = FreeverseSecondary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "“Your Campus. Your Skills. Your Opportunities.”",
                    fontSize = 12.sp,
                    color = FreeversePrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Footer Quick Navigation
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf(
                        "Freelancers" to ScreenNav.FREELANCERS,
                        "Projects" to ScreenNav.PROJECTS,
                        "Campus Gigs" to ScreenNav.GIGS,
                        "Services" to ScreenNav.SERVICES,
                        "Events" to ScreenNav.EVENTS
                    ).forEach { (label, screen) ->
                        Text(
                            text = label,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FreeverseTextSecondary,
                            modifier = Modifier.clickable { viewModel.navigateTo(screen) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = FreeverseBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Freeverse Student Community • Campus Freelancing Platform\nAll rights reserved © 2026",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
