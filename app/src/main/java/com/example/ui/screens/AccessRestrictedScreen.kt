package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel
import kotlinx.coroutines.delay

@Composable
fun AccessRestrictedScreen(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val restrictionInfo by viewModel.restrictionInfo.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val userRole = currentUser?.let { UserRole.fromKey(it.role) } ?: UserRole.MEMBER
    val userName = currentUser?.name ?: "Guest / Anonymous"

    var countdown by remember { mutableStateOf(5) }

    // Auto redirect timer
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
        viewModel.goToMyDashboard()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FreeverseBackground)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Warning / Lock Circle
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEE2E2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LockPerson,
                contentDescription = "Access Restricted",
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Access Restricted",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF991B1B)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You don't have permission to access this page.",
            fontSize = 14.sp,
            color = FreeverseTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Security Incident Info Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RBAC Security Policy Violation",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB91C1C)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Your Account:", fontSize = 11.5.sp, color = FreeverseTextSecondary)
                    Text(
                        text = "$userName (${userRole.displayName})",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreeverseTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                restrictionInfo?.let { info ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Required Role:", fontSize = 11.5.sp, color = FreeverseTextSecondary)
                        Text(
                            text = info.requiredRole.displayName,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(info.requiredRole.badgeColorHex)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Auto-redirecting to your dashboard in $countdown seconds...",
                    fontSize = 10.5.sp,
                    color = FreeverseTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.goToMyDashboard() },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Go to My Dashboard", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
            }

            OutlinedButton(
                onClick = { viewModel.navigateBack() },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
            }
        }
    }
}
