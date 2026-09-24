package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun AboutKitScreen(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FreeverseBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.img_kit_campus),
                    contentDescription = "KIT Campus",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.img_freeverse_logo),
                        contentDescription = "FREEVERSE Logo",
                        modifier = Modifier.size(50.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "FREEVERSE PLATFORM",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = FreeversePrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(4.dp), color = FreeverseSecondary) {
                                Text("STUDENT ECOSYSTEM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Empowering Student Minds", fontSize = 11.sp, color = FreeverseTextSecondary, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Freeverse is the official campus student freelancing platform fostering technical excellence, creative skills incubation, and peer-to-peer real-world project collaboration.",
                    fontSize = 12.5.sp,
                    color = FreeverseTextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        // Institutional Pillars
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "CAMPUS EXCELLENCE PILLARS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreeversePrimary,
                    letterSpacing = 0.5.sp
                )

                val pillars = listOf(
                    Pair("Autonomous Curriculum", "Industry-aligned syllabus updated in real time with AI, Cloud, and Full-Stack tracks."),
                    Pair("Freeverse Club Incubation", "Student-run innovation collective connecting technical talent directly to paid gigs and open-source campus tools."),
                    Pair("State-of-the-Art Computing Labs", "High-performance GPU labs, IoT development kits, and dedicated symposium arenas."),
                    Pair("Placement & Career Cell", "Top tier recruiters, mock interview preparation, and real-world freelance portfolio vetting.")
                )

                pillars.forEach { (title, desc) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = FreeverseSoftLavender,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FreeversePrimary)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(desc, fontSize = 11.5.sp, color = FreeverseTextPrimary, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }

        // Freeverse Student Community Chapter Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.img_freeverse_logo),
                        contentDescription = "FREEVERSE Logo",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("FREEVERSE STUDENT CHAPTER", fontWeight = FontWeight.Black, fontSize = 13.sp, color = FreeversePrimary)
                        Text("Kangeyam Institute of Technology • Autonomous", fontSize = 10.sp, color = FreeverseTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "“Your Campus. Your Skills. Your Opportunities.”",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreeversePrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "FREEVERSE serves as KIT's centralized bridge between student creative potential and real client opportunities across engineering, design, and development domains.",
                    fontSize = 11.5.sp,
                    color = FreeverseTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
