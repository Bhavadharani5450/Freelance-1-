package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel

@Composable
fun CampusGigsScreen(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val gigs by viewModel.allGigs.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FreeverseBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "CAMPUS GIGS",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = FreeversePrimary,
            letterSpacing = 1.sp
        )
        Text(
            text = "Quick Tasks & Club Opportunities",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = FreeverseTextPrimary
        )
        Text(
            text = "Earn quick stipends and build portfolio credentials by helping KIT clubs and departments with bite-sized tasks.",
            fontSize = 12.5.sp,
            color = FreeverseTextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(gigs) { gig ->
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
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEBF3FF)
                                ) {
                                    Text(
                                        text = gig.clubOrDepartment,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FreeverseSecondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Text("₹${gig.reward}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = FreeverseSuccess)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(gig.task, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Deadline: ${gig.deadline} • ${gig.applicantsCount} applicants", fontSize = 11.sp, color = FreeverseTextSecondary)

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Skills Required: ${gig.skills}", fontSize = 11.sp, color = FreeversePrimary, fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.applyForGig(gig) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (gig.isApplied) Color(0xFFE2E8F0) else FreeversePrimary
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !gig.isApplied
                        ) {
                            if (gig.isApplied) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = FreeverseSuccess, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Applied Successfully", color = FreeverseTextSecondary, fontWeight = FontWeight.Bold)
                            } else {
                                Text("1-Tap Apply for Gig", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
