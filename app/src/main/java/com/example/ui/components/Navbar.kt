package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel
import com.example.ui.viewmodel.ScreenNav

/**
 * Premium Responsive Navbar for FREEVERSE
 * Kangeyam Institute of Technology
 * Displays authentic Freeverse and KIT logos side-by-side,
 * quick role switcher for presentation, notifications bell, and navigation tabs.
 */
@Composable
fun FreeverseNavbar(
    viewModel: FreeverseViewModel,
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val notifications by viewModel.allNotifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    var isRoleMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = FreeverseSurface,
        shadowElevation = 3.dp,
        border = null
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Main Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Menu Icon + Partnered Logos (Freeverse + KIT)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Navigation Menu",
                            tint = FreeverseTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Brand & Logos container
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.navigateTo(ScreenNav.HOME) }
                            .padding(4.dp)
                    ) {
                        // FREEVERSE Logo
                        Image(
                            painter = painterResource(id = R.drawable.img_freeverse_logo),
                            contentDescription = "FREEVERSE Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "FREEVERSE",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FreeversePrimary,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFEBF3FF)
                                ) {
                                    Text(
                                        text = "KIT",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FreeverseSecondary,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Kangeyam Institute of Technology",
                                fontSize = 9.5.sp,
                                color = FreeverseTextSecondary,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Right: Role Switcher & Action Icons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Quick Role Switcher Pill
                    Box {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = FreeverseSoftLavender,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6D7FB)),
                            modifier = Modifier.clickable { isRoleMenuExpanded = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (currentUser.role) {
                                                "STUDENT" -> FreeversePrimary
                                                "CLIENT" -> FreeverseSecondary
                                                "ADMIN" -> Color(0xFF10B981)
                                                "SUPER_ADMIN" -> FreeverseWarning
                                                "EVENT_MANAGER" -> Color(0xFFEC4899)
                                                else -> FreeversePrimary
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = when (currentUser.role) {
                                        "STUDENT" -> "Student"
                                        "CLIENT" -> "Client"
                                        "ADMIN" -> "Admin"
                                        "SUPER_ADMIN" -> "Super Admin"
                                        "EVENT_MANAGER" -> "Event Mgr"
                                        else -> "Student"
                                    },
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FreeversePrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Switch Role",
                                    tint = FreeversePrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = isRoleMenuExpanded,
                            onDismissRequest = { isRoleMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(Icons.Default.School, contentDescription = null, tint = FreeversePrimary)
                                },
                                text = {
                                    Column {
                                        Text("Student / Freelancer", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("student@freeverse.com • Student@123", fontSize = 11.sp, color = FreeverseTextSecondary)
                                    }
                                },
                                onClick = {
                                    viewModel.switchUserByRole(UserRole.STUDENT)
                                    isRoleMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(Icons.Default.Work, contentDescription = null, tint = FreeverseSecondary)
                                },
                                text = {
                                    Column {
                                        Text("🏢 Client", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("client@freeverse.com • Client@123", fontSize = 11.sp, color = FreeverseTextSecondary)
                                    }
                                },
                                onClick = {
                                    viewModel.switchUserByRole(UserRole.CLIENT)
                                    isRoleMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF10B981))
                                },
                                text = {
                                    Column {
                                        Text("🛡️ Admin", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("admin@freeverse.com • Admin@123", fontSize = 11.sp, color = FreeverseTextSecondary)
                                    }
                                },
                                onClick = {
                                    viewModel.switchUserByRole(UserRole.ADMIN)
                                    isRoleMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = FreeverseWarning)
                                },
                                text = {
                                    Column {
                                        Text("👑 Super Admin", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("superadmin@freeverse.com • SuperAdmin@123", fontSize = 11.sp, color = FreeverseTextSecondary)
                                    }
                                },
                                onClick = {
                                    viewModel.switchUserByRole(UserRole.SUPER_ADMIN)
                                    isRoleMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFFEC4899))
                                },
                                text = {
                                    Column {
                                        Text("🎪 Event Manager", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("eventmanager@freeverse.com • Event@123", fontSize = 11.sp, color = FreeverseTextSecondary)
                                    }
                                },
                                onClick = {
                                    viewModel.switchUserByRole(UserRole.EVENT_MANAGER)
                                    isRoleMenuExpanded = false
                                }
                            )
                        }
                    }

                    // Notification Bell
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(containerColor = FreeversePrimary) {
                                    Text(text = "$unreadCount", fontSize = 9.sp)
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = { viewModel.isNotificationsOpen.value = true },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = FreeverseTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Dashboard Icon Button
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenNav.DASHBOARD) },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (currentScreen == ScreenNav.DASHBOARD) FreeversePrimary else FreeverseSoftLavender)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "User Dashboard",
                            tint = if (currentScreen == ScreenNav.DASHBOARD) Color.White else FreeversePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Mobile Bottom Navigation Bar for rapid thumb access
 */
@Composable
fun FreeverseBottomNav(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val gigs by viewModel.allGigs.collectAsState()

    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            Triple(ScreenNav.HOME, "Home", Icons.Default.Home),
            Triple(ScreenNav.FREELANCERS, "Freelancers", Icons.Default.Person),
            Triple(ScreenNav.PROJECTS, "Projects", Icons.Default.Work),
            Triple(ScreenNav.GIGS, "Gigs", Icons.Default.Bolt),
            Triple(ScreenNav.DASHBOARD, "Dashboard", Icons.Default.Dashboard)
        )

        navItems.forEach { (screen, label, icon) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { viewModel.navigateTo(screen) },
                icon = {
                    if (screen == ScreenNav.GIGS) {
                        BadgedBox(badge = {
                            Badge(containerColor = FreeversePrimary) {
                                Text("${gigs.size}", fontSize = 8.sp)
                            }
                        }) {
                            Icon(icon, contentDescription = label)
                        }
                    } else {
                        Icon(icon, contentDescription = label)
                    }
                },
                label = { Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FreeversePrimary,
                    selectedTextColor = FreeversePrimary,
                    indicatorColor = FreeverseSoftLavender,
                    unselectedIconColor = FreeverseTextSecondary,
                    unselectedTextColor = FreeverseTextSecondary
                )
            )
        }
    }
}

/**
 * Branded Navigation Drawer Content featuring authentic logos,
 * active user info, role switcher, navigation destinations, and KIT credentials.
 */
@Composable
fun FreeverseDrawerContent(
    viewModel: FreeverseViewModel,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val notifications by viewModel.allNotifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    ModalDrawerSheet(
        modifier = modifier.width(310.dp),
        drawerContainerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Gradient & Authentic Logos
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(FreeversePrimary, FreeverseSecondary)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // FREEVERSE Logo
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_freeverse_logo),
                                contentDescription = "FREEVERSE Logo",
                                modifier = Modifier
                                    .size(46.dp)
                                    .padding(3.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        IconButton(
                            onClick = onCloseDrawer,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Menu",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "FREEVERSE",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "“Your Campus. Your Skills. Your Opportunities.”",
                        fontSize = 11.sp,
                        color = Color(0xFFE0E7FF),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Student Freelancing & Creative Hub",
                            fontSize = 9.5.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // User Info Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                color = FreeverseSoftLavender,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6D7FB))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(FreeversePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser.name.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(currentUser.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = when (currentUser.role) {
                                        "STUDENT" -> "Student Freelancer"
                                        "COLLEGE_CLIENT" -> "College Department Client"
                                        "EXTERNAL_CLIENT" -> "Industry Partner Client"
                                        else -> "Platform Administrator"
                                    },
                                    fontSize = 10.sp,
                                    color = FreeversePrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentUser.organization,
                        fontSize = 10.sp,
                        color = FreeverseTextSecondary
                    )
                }
            }

            // Navigation Items List
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(
                    text = "EXPLORE PLATFORM",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreeverseTextSecondary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )

                val menuItems = listOf(
                    Triple(ScreenNav.HOME, "Home", Icons.Default.Home),
                    Triple(ScreenNav.FREELANCERS, "Freelancers Marketplace", Icons.Default.Person),
                    Triple(ScreenNav.PROJECTS, "Projects & Contracts", Icons.Default.Work),
                    Triple(ScreenNav.SERVICES, "Student Services", Icons.Default.Build),
                    Triple(ScreenNav.GIGS, "Campus Micro-Gigs", Icons.Default.Bolt),
                    Triple(ScreenNav.EVENTS, "Events & Hackathons", Icons.Default.Event),
                    Triple(ScreenNav.ABOUT_KIT, "About KIT College", Icons.Default.AccountBalance),
                    Triple(ScreenNav.DASHBOARD, "My Dashboard", Icons.Default.Dashboard),
                    Triple(ScreenNav.MESSAGES, "Project Messages", Icons.Default.Chat)
                )

                menuItems.forEach { (screen, label, icon) ->
                    val isSelected = currentScreen == screen
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                icon,
                                contentDescription = null,
                                tint = if (isSelected) FreeversePrimary else FreeverseTextSecondary
                            )
                        },
                        label = {
                            Text(
                                label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            viewModel.navigateTo(screen)
                            onCloseDrawer()
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = FreeverseSoftLavender,
                            selectedTextColor = FreeversePrimary,
                            unselectedTextColor = FreeverseTextPrimary
                        ),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Quick Links
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedButton(
                    onClick = {
                        viewModel.isPostProjectOpen.value = true
                        onCloseDrawer()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FreeversePrimary)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Post a Project", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.isSkillAssessmentOpen.value = true
                        onCloseDrawer()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FreeverseSecondary)
                ) {
                    Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Skill Assessment", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer in Drawer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "FREEVERSE PLATFORM",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreeversePrimary
                )
                Text(
                    text = "Kangeyam Institute of Technology • Autonomous",
                    fontSize = 9.sp,
                    color = FreeverseTextSecondary
                )
            }
        }
    }
}
