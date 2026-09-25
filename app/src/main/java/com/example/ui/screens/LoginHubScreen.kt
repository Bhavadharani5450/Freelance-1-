package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel
import com.example.ui.viewmodel.ScreenNav

data class LoginCardItem(
    val role: UserRole,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val targetScreen: ScreenNav
)

@Composable
fun LoginHubScreen(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val loginOptions = listOf(
        LoginCardItem(
            role = UserRole.CLUB_COORDINATOR,
            title = "Club Coordinator",
            subtitle = "Manage FREEVERSE + Freelance",
            icon = Icons.Default.Groups,
            accentColor = Color(0xFF8B5CF6),
            targetScreen = ScreenNav.LOGIN_COORDINATOR
        ),
        LoginCardItem(
            role = UserRole.FACULTY_COORDINATOR,
            title = "Faculty Coordinator",
            subtitle = "Monitor FREEVERSE activities",
            icon = Icons.Default.School,
            accentColor = Color(0xFF10B981),
            targetScreen = ScreenNav.LOGIN_FACULTY
        ),
        LoginCardItem(
            role = UserRole.MEMBER,
            title = "Member / Freelancer",
            subtitle = "Find projects • Build • Earn",
            icon = Icons.Default.Person,
            accentColor = Color(0xFF3B82F6),
            targetScreen = ScreenNav.LOGIN_MEMBER
        ),
        LoginCardItem(
            role = UserRole.CLIENT,
            title = "Client",
            subtitle = "Hire student talent",
            icon = Icons.Default.BusinessCenter,
            accentColor = Color(0xFF0D9488),
            targetScreen = ScreenNav.LOGIN_CLIENT
        ),
        LoginCardItem(
            role = UserRole.SUPER_ADMIN,
            title = "Super Admin",
            subtitle = "Platform administration",
            icon = Icons.Default.AdminPanelSettings,
            accentColor = Color(0xFFF59E0B),
            targetScreen = ScreenNav.LOGIN_ADMIN
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFEDE9FE), FreeverseBackground, Color.White)
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Platform Logo & Header
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_freeverse_logo),
                contentDescription = "FREEVERSE Logo",
                modifier = Modifier
                    .size(68.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
        }

        Text(
            text = "FREEVERSE",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = FreeversePrimary,
            letterSpacing = 1.sp
        )

        Text(
            text = "CREATE • COLLABORATE • EARN",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = FreeverseSecondary,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Welcome Box
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome back to FREEVERSE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = FreeverseTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose how you access the FREEVERSE community.",
                    fontSize = 13.sp,
                    color = FreeverseTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5 Role Login Cards
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            loginOptions.forEach { item ->
                LoginCard(
                    item = item,
                    onClick = { viewModel.navigateTo(item.targetScreen) }
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Security Notice Footer
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = FreeverseSoftLavender,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6D7FB)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = FreeversePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Zero-Trust Role-Based Authentication",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = FreeversePrimary
                    )
                    Text(
                        text = "Access is cryptographically validated. Each role has a separate entry and restricted permissions.",
                        fontSize = 10.sp,
                        color = FreeverseTextSecondary,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginCard(
    item: LoginCardItem,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, FreeverseBorder, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(item.accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = item.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FreeverseTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.subtitle,
                        fontSize = 11.5.sp,
                        color = FreeverseTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = CircleShape,
                color = item.accentColor,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Login",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
