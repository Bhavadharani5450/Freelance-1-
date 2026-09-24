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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.EventEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel

@Composable
fun EventsScreen(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val events by viewModel.allEvents.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Hackathon", "Workshop", "Technical")
    val filteredEvents = if (selectedCategory == "All") events else events.filter { it.category == selectedCategory }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FreeverseBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "EVENTS & ACTIVITIES",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = FreeversePrimary,
            letterSpacing = 1.sp
        )
        Text(
            text = "Campus Events & Hackathons",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = FreeverseTextPrimary
        )
        Text(
            text = "Level up your freelancing toolkit through hands-on hackathons, workshops, and technical sprints.",
            fontSize = 12.5.sp,
            color = FreeverseTextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Category Tabs
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
                    modifier = Modifier.clickable { selectedCategory = cat }
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

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredEvents) { event ->
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
                                color = if (event.category == "Competition") Color(0xFFFEF2F2) else FreeverseSoftLavender
                            ) {
                                Text(
                                    text = event.category.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (event.category == "Competition") FreeverseError else FreeversePrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(event.date, fontSize = 11.5.sp, color = FreeverseTextSecondary, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(event.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = FreeverseTextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(event.time, fontSize = 11.sp, color = FreeverseTextSecondary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = FreeverseTextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(event.venue, fontSize = 11.sp, color = FreeverseTextSecondary)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(event.description, fontSize = 12.sp, color = FreeverseTextSecondary, lineHeight = 16.sp)

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Rules: ${event.rules}", fontSize = 10.5.sp, color = Color(0xFF64748B))

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${event.registeredCount} Students Registered", fontSize = 11.sp, color = FreeversePrimary, fontWeight = FontWeight.SemiBold)

                            Button(
                                onClick = { viewModel.registerForEvent(event) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (event.isRegistered) Color(0xFFE2E8F0) else FreeversePrimary
                                ),
                                enabled = !event.isRegistered,
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (event.isRegistered) "✓ Registered" else "Register Now",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (event.isRegistered) FreeverseTextSecondary else Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Past Events Gallery section at bottom of events
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("PAST EVENT HIGHLIGHTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FreeversePrimary)
                        Text("Freeverse Hackathon & Design Gallery", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_students),
                            contentDescription = "Event Gallery",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Students participating in the 24-Hour Design Jam at KIT Campus.", fontSize = 11.sp, color = FreeverseTextSecondary)
                    }
                }
            }
        }
    }
}
