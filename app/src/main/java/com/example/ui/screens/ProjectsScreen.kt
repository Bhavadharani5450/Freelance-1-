package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.ProjectEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel

@Composable
fun ProjectsScreen(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.filteredProjects.collectAsState()
    val searchQuery by viewModel.projectSearchQuery.collectAsState()
    val selectedCategory by viewModel.projectSelectedCategory.collectAsState()
    val selectedType by viewModel.projectSelectedType.collectAsState()

    val categories = listOf("All", "Web Development", "UI/UX Design", "AI / ML", "Video Editing")
    val types = listOf("All", "CAMPUS", "INDUSTRY", "CHALLENGE")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FreeverseBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "OPPORTUNITY FEED",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreeversePrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Find Your Next Project",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = FreeverseTextPrimary
                )
            }
            Button(
                onClick = { viewModel.isPostProjectOpen.value = true },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Post", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.projectSearchQuery.value = it },
            placeholder = { Text("Search projects by skill or title...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = FreeversePrimary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.projectSearchQuery.value = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Categories Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) FreeversePrimary else Color.White,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                    modifier = Modifier.clickable { viewModel.projectSelectedCategory.value = cat }
                ) {
                    Text(
                        text = cat,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else FreeverseTextPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Project Types Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            types.forEach { type ->
                val isSelected = selectedType == type
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) FreeverseSoftLavender else Color.Transparent,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6D7FB)) else null,
                    modifier = Modifier.clickable { viewModel.projectSelectedType.value = type }
                ) {
                    Text(
                        text = if (type == "All") "All Types" else type,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) FreeversePrimary else FreeverseTextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("${projects.size} Active Projects Available", fontSize = 11.5.sp, color = FreeverseTextSecondary, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(8.dp))

        // Project Cards
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(projects) { proj ->
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_freeverse_logo),
                                    contentDescription = "FREEVERSE",
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.width(6.dp))
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
                            }
                            Text("₹${proj.budget}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = FreeverseSuccess)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(proj.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(proj.description, fontSize = 12.sp, color = FreeverseTextSecondary, lineHeight = 16.sp)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Skills: ${proj.requiredSkills}", fontSize = 11.sp, color = FreeversePrimary, fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Client: ${proj.clientName}", fontSize = 11.sp, color = FreeverseTextSecondary)
                                Text("Deadline: ${proj.deadline} • ${proj.proposalCount} proposals", fontSize = 10.5.sp, color = Color(0xFF94A3B8))
                            }

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
}
