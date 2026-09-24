package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.UserEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel

@Composable
fun FreelancersScreen(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val freelancers by viewModel.filteredFreelancers.collectAsState()
    val searchQuery by viewModel.freelancerSearchQuery.collectAsState()
    val selectedSkill by viewModel.freelancerSelectedSkill.collectAsState()
    val selectedSort by viewModel.freelancerSelectedSort.collectAsState()

    val skillFilters = listOf("All", "React", "Node.js", "Python", "UI/UX", "Figma", "Kotlin", "Flutter", "Video")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FreeverseBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "FREELANCER MARKETPLACE",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = FreeversePrimary,
            letterSpacing = 1.sp
        )
        Text(
            text = "Discover Student Talent",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = FreeverseTextPrimary
        )
        Text(
            text = "Find students at Kangeyam Institute of Technology who can turn your ideas into reality.",
            fontSize = 12.5.sp,
            color = FreeverseTextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.freelancerSearchQuery.value = it },
            placeholder = { Text("Search by name, role, or skill...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = FreeversePrimary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.freelancerSearchQuery.value = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
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

        // Skill Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            skillFilters.forEach { skill ->
                val isSelected = selectedSkill == skill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) FreeversePrimary else Color.White,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                    modifier = Modifier.clickable { viewModel.freelancerSelectedSkill.value = skill }
                ) {
                    Text(
                        text = skill,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else FreeverseTextPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sort Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${freelancers.size} Verified Students Found", fontSize = 12.sp, color = FreeverseTextSecondary, fontWeight = FontWeight.SemiBold)

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Recommended", "Highest Rated", "Most Projects").forEach { sort ->
                    val isSelected = selectedSort == sort
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) FreeverseSoftLavender else Color.Transparent,
                        modifier = Modifier.clickable { viewModel.freelancerSelectedSort.value = sort }
                    ) {
                        Text(
                            text = sort,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) FreeversePrimary else FreeverseTextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Freelancer List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(freelancers) { student ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectedFreelancerForProfile.value = student }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = FreeverseSoftLavender,
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, FreeversePrimary),
                                    modifier = Modifier.size(46.dp)
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
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(student.name, fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = FreeversePrimary, modifier = Modifier.size(14.dp))
                                    }
                                    Text("${student.department} • ${student.year}", fontSize = 11.sp, color = FreeverseTextSecondary)
                                }
                            }

                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFEF3C7)) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = FreeverseWarning, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("${student.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(student.bio, fontSize = 12.sp, color = FreeverseTextPrimary, maxLines = 2, lineHeight = 16.sp)

                        Spacer(modifier = Modifier.height(8.dp))
                        // Skills Chips
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            student.skills.split(",").map { it.trim() }.forEach { sk ->
                                Surface(shape = RoundedCornerShape(6.dp), color = FreeverseSoftLavender) {
                                    Text(sk, fontSize = 10.sp, color = FreeversePrimary, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Rate: ₹${student.hourlyRate}/hr • ${student.projectsCompleted} completed", fontSize = 11.sp, color = FreeverseTextSecondary)

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.selectedFreelancerForProfile.value = student },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Profile", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = {
                                        viewModel.showSnackbar("Direct project invitation sent to ${student.name}!")
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("Hire", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
