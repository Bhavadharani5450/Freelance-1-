package com.example.ui.components

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
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CoordinatorMode
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel
import com.example.ui.viewmodel.ScreenNav

/**
 * Public Landing Page Header
 * Minimal, fast, and does not duplicate authenticated navigation.
 */
@Composable
fun PublicLandingHeader(
    viewModel: FreeverseViewModel,
    onNavigateSection: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Logo + Single-line Brand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { viewModel.navigateTo(ScreenNav.HOME) }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_freeverse_logo),
                    contentDescription = "FREEVERSE Logo",
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "FREEVERSE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = FreeversePrimary,
                        letterSpacing = 0.5.sp,
                        maxLines = 1,
                        softWrap = false
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
            }

            // Right: Primary Actions (Login & Join)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(ScreenNav.LOGIN_HUB) },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, FreeversePrimary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Login,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = FreeversePrimary
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Login",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreeversePrimary
                    )
                }

                Button(
                    onClick = { viewModel.navigateTo(ScreenNav.LOGIN_MEMBER) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Join FREEVERSE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Authenticated Top Bar (Mobile / Compact View)
 * Displays ONE clean hamburger button, logo + title, and profile menu.
 */
@Composable
fun AuthenticatedTopBar(
    viewModel: FreeverseViewModel,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val userRole = currentUser?.let { UserRole.fromKey(it.role) } ?: UserRole.MEMBER
    val roleColor = Color(userRole.badgeColorHex)
    val notifications by viewModel.roleNotifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    var isProfileMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Hamburger + Logo + Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Navigation Menu",
                        tint = FreeverseTextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Image(
                    painter = painterResource(id = R.drawable.img_freeverse_logo),
                    contentDescription = "FREEVERSE",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "FREEVERSE",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = FreeversePrimary,
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = roleColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (userRole == UserRole.CLUB_COORDINATOR && currentUser?.coordinatorDesignation?.contains("President", ignoreCase = true) == true) "PRESIDENT" else userRole.displayName.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = roleColor,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = when (userRole) {
                            UserRole.SUPER_ADMIN -> "Platform Administration"
                            UserRole.FACULTY_COORDINATOR -> "Faculty Oversight"
                            UserRole.CLUB_COORDINATOR -> "President Workspace"
                            UserRole.MEMBER -> "Freelancer Workspace"
                            UserRole.CLIENT -> "Client Hiring Portal"
                        },
                        fontSize = 10.5.sp,
                        color = FreeverseTextSecondary,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }

            // Right: Notifications + User Profile Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Notifications Icon
                IconButton(
                    onClick = { viewModel.isNotificationsOpen.value = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(
                                    containerColor = FreeverseError,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = "$unreadCount",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = FreeverseTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Profile Pill Menu
                Box {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = roleColor.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, roleColor.copy(alpha = 0.35f)),
                        modifier = Modifier.clickable { isProfileMenuExpanded = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(roleColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (currentUser?.name ?: "U").take(1).uppercase(),
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = (currentUser?.name?.split(" ")?.firstOrNull() ?: "User"),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = roleColor,
                                maxLines = 1
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = roleColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = isProfileMenuExpanded,
                        onDismissRequest = { isProfileMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = roleColor)
                            },
                            text = {
                                Column {
                                    Text(currentUser?.name ?: "User", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        text = currentUser?.coordinatorDesignation ?: userRole.displayName,
                                        fontSize = 10.5.sp,
                                        color = roleColor
                                    )
                                }
                            },
                            onClick = {
                                viewModel.goToMyDashboard()
                                isProfileMenuExpanded = false
                            }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFDC2626))
                            },
                            text = {
                                Text(
                                    text = "Log Out",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626)
                                )
                            },
                            onClick = {
                                isProfileMenuExpanded = false
                                viewModel.isLogoutConfirmOpen.value = true
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Role-Specific Sidebar & Drawer Content
 * Used in both desktop left-rail and mobile slide-out drawer.
 */
@Composable
fun RoleSpecificSidebarContent(
    viewModel: FreeverseViewModel,
    onCloseDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser ?: return
    val userRole = UserRole.fromKey(user.role)
    val roleColor = Color(userRole.badgeColorHex)
    val coordinatorMode by viewModel.coordinatorMode.collectAsState()

    val currentAdminTab by viewModel.selectedAdminTab.collectAsState()
    val currentFacultyTab by viewModel.selectedFacultyTab.collectAsState()
    val currentCoordTab by viewModel.selectedCoordinatorTab.collectAsState()
    val currentMemberTab by viewModel.selectedMemberTab.collectAsState()
    val currentClientTab by viewModel.selectedClientTab.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Sidebar Branding Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(FreeversePrimary, FreeverseSecondary)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_freeverse_logo),
                            contentDescription = "FREEVERSE Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .padding(3.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "KIT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "FREEVERSE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = "“Your Campus. Your Skills. Your Opportunities.”",
                    fontSize = 10.sp,
                    color = Color(0xFFE0E7FF),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Authenticated Identity Profile Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(12.dp),
            color = roleColor.copy(alpha = 0.08f),
            border = androidx.compose.foundation.BorderStroke(1.dp, roleColor.copy(alpha = 0.25f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(roleColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = roleColor
                            ) {
                                Text(
                                    text = user.coordinatorDesignation ?: userRole.displayName,
                                    fontSize = 9.5.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = user.organization,
                    fontSize = 10.sp,
                    color = FreeverseTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // President / Club Coordinator Dual Mode Switcher
        if (userRole == UserRole.CLUB_COORDINATOR) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF3E8FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD6FE)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp)
            ) {
                Row(modifier = Modifier.padding(3.dp)) {
                    Surface(
                        shape = RoundedCornerShape(7.dp),
                        color = if (coordinatorMode == CoordinatorMode.COORDINATOR) Color(0xFF8B5CF6) else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.setCoordinatorMode(CoordinatorMode.COORDINATOR)
                                viewModel.setCoordinatorTab(0)
                            }
                    ) {
                        Text(
                            text = "Club Mgmt",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (coordinatorMode == CoordinatorMode.COORDINATOR) Color.White else Color(0xFF6D28D9),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(7.dp),
                        color = if (coordinatorMode == CoordinatorMode.FREELANCER) Color(0xFF8B5CF6) else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.setCoordinatorMode(CoordinatorMode.FREELANCER)
                                viewModel.setCoordinatorTab(5)
                            }
                    ) {
                        Text(
                            text = "Freelancer",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (coordinatorMode == CoordinatorMode.FREELANCER) Color.White else Color(0xFF6D28D9),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // ROLE-SPECIFIC MENU ITEMS
        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            when (userRole) {
                UserRole.SUPER_ADMIN -> {
                    SidebarSectionHeader("PLATFORM MANAGEMENT", roleColor)
                    val adminItems = listOf(
                        Triple(0, "Dashboard Overview", Icons.Default.Dashboard),
                        Triple(1, "User Management", Icons.Default.People),
                        Triple(2, "Roles & Permissions", Icons.Default.Security),
                        Triple(3, "Club Coordinators", Icons.Default.Groups),
                        Triple(4, "Faculty Coordinators", Icons.Default.School),
                        Triple(5, "Members & Talent", Icons.Default.Person),
                        Triple(6, "Clients Directory", Icons.Default.BusinessCenter),
                        Triple(7, "Projects & Contracts", Icons.Default.Work),
                        Triple(8, "Events & Hackathons", Icons.Default.Event),
                        Triple(9, "Service Categories", Icons.Default.Category),
                        Triple(10, "Payments & Escrow", Icons.Default.Payments),
                        Triple(11, "Complaints & Reports", Icons.Default.Report),
                        Triple(12, "Notifications", Icons.Default.Notifications),
                        Triple(13, "System Settings", Icons.Default.Settings)
                    )
                    adminItems.forEach { (tabIdx, label, icon) ->
                        val isSelected = currentAdminTab == tabIdx
                        SidebarItemButton(
                            label = label,
                            icon = icon,
                            isSelected = isSelected,
                            selectedColor = roleColor,
                            onClick = {
                                viewModel.setAdminTab(tabIdx)
                                onCloseDrawer()
                            }
                        )
                    }
                }

                UserRole.FACULTY_COORDINATOR -> {
                    SidebarSectionHeader("FACULTY OVERSIGHT", roleColor)
                    val facultyItems = listOf(
                        Triple(0, "Club Overview", Icons.Default.Dashboard),
                        Triple(1, "Events & Oversight", Icons.Default.Event),
                        Triple(2, "Event Registrations", Icons.Default.Assignment),
                        Triple(3, "Student Participation", Icons.Default.Analytics),
                        Triple(4, "Club Coordinators", Icons.Default.Groups),
                        Triple(5, "Community Activities", Icons.Default.Forum),
                        Triple(6, "Freelancing Activity", Icons.Default.TrendingUp),
                        Triple(7, "Project Monitoring", Icons.Default.Work),
                        Triple(8, "Club Reports", Icons.Default.Assessment),
                        Triple(9, "Notifications", Icons.Default.Notifications),
                        Triple(10, "Faculty Profile", Icons.Default.AccountCircle)
                    )
                    facultyItems.forEach { (tabIdx, label, icon) ->
                        val isSelected = currentFacultyTab == tabIdx
                        SidebarItemButton(
                            label = label,
                            icon = icon,
                            isSelected = isSelected,
                            selectedColor = roleColor,
                            onClick = {
                                viewModel.setFacultyTab(tabIdx)
                                onCloseDrawer()
                            }
                        )
                    }
                }

                UserRole.CLUB_COORDINATOR -> {
                    if (coordinatorMode == CoordinatorMode.COORDINATOR) {
                        SidebarSectionHeader("CLUB MANAGEMENT", roleColor)
                        val clubItems = listOf(
                            Triple(0, "Club Overview", Icons.Default.Dashboard),
                            Triple(1, "Events & Create", Icons.Default.Event),
                            Triple(2, "Event Registrations", Icons.Default.Assignment),
                            Triple(3, "Community Members", Icons.Default.People),
                            Triple(4, "Announcements", Icons.Default.Campaign),
                            Triple(10, "Club Reports", Icons.Default.Assessment),
                            Triple(11, "Coordinator Profile", Icons.Default.AccountCircle)
                        )
                        clubItems.forEach { (tabIdx, label, icon) ->
                            val isSelected = currentCoordTab == tabIdx
                            SidebarItemButton(
                                label = label,
                                icon = icon,
                                isSelected = isSelected,
                                selectedColor = roleColor,
                                onClick = {
                                    viewModel.setCoordinatorTab(tabIdx)
                                    onCloseDrawer()
                                }
                            )
                        }
                    } else {
                        SidebarSectionHeader("FREELANCER WORKSPACE", roleColor)
                        val freelanceItems = listOf(
                            Triple(5, "Explore Projects", Icons.Default.Work),
                            Triple(6, "My Services", Icons.Default.Build),
                            Triple(7, "My Proposals & Work", Icons.Default.Assignment),
                            Triple(8, "My Earnings", Icons.Default.Payments),
                            Triple(9, "Project Messages", Icons.Default.Chat),
                            Triple(11, "Portfolio & Skills", Icons.Default.Verified)
                        )
                        freelanceItems.forEach { (tabIdx, label, icon) ->
                            val isSelected = currentCoordTab == tabIdx
                            SidebarItemButton(
                                label = label,
                                icon = icon,
                                isSelected = isSelected,
                                selectedColor = roleColor,
                                onClick = {
                                    viewModel.setCoordinatorTab(tabIdx)
                                    onCloseDrawer()
                                }
                            )
                        }
                    }
                }

                UserRole.MEMBER -> {
                    SidebarSectionHeader("FREELANCER WORKSPACE", roleColor)
                    val memberItems = listOf(
                        Triple(0, "Explore Projects", Icons.Default.Work),
                        Triple(1, "My Profile & Skills", Icons.Default.Person),
                        Triple(2, "My Portfolio", Icons.Default.Verified),
                        Triple(3, "My Services", Icons.Default.Build),
                        Triple(4, "My Proposals", Icons.Default.Send),
                        Triple(5, "My Active Contracts", Icons.Default.AssignmentTurnedIn),
                        Triple(6, "Messages & Chat", Icons.Default.Chat),
                        Triple(7, "Student Community", Icons.Default.Groups),
                        Triple(8, "Events & Hackathons", Icons.Default.Event),
                        Triple(9, "Earnings & Invoices", Icons.Default.Payments),
                        Triple(10, "Notifications", Icons.Default.Notifications),
                        Triple(11, "Settings", Icons.Default.Settings)
                    )
                    memberItems.forEach { (tabIdx, label, icon) ->
                        val isSelected = currentMemberTab == tabIdx
                        SidebarItemButton(
                            label = label,
                            icon = icon,
                            isSelected = isSelected,
                            selectedColor = roleColor,
                            onClick = {
                                viewModel.setMemberTab(tabIdx)
                                onCloseDrawer()
                            }
                        )
                    }
                }

                UserRole.CLIENT -> {
                    SidebarSectionHeader("CLIENT HIRING", roleColor)
                    val clientItems = listOf(
                        Triple(0, "Post a Project", Icons.Default.AddCircle),
                        Triple(1, "My Projects & Listings", Icons.Default.Work),
                        Triple(2, "Proposals Received", Icons.Default.Inbox),
                        Triple(3, "Find Student Talent", Icons.Default.People),
                        Triple(4, "Hired Freelancers", Icons.Default.VerifiedUser),
                        Triple(5, "Active Contracts", Icons.Default.AssignmentTurnedIn),
                        Triple(6, "Completed Projects", Icons.Default.CheckCircle),
                        Triple(7, "Messages & Chat", Icons.Default.Chat),
                        Triple(8, "Payments & Escrow", Icons.Default.Payments),
                        Triple(11, "Client Profile", Icons.Default.BusinessCenter)
                    )
                    clientItems.forEach { (tabIdx, label, icon) ->
                        val isSelected = currentClientTab == tabIdx
                        SidebarItemButton(
                            label = label,
                            icon = icon,
                            isSelected = isSelected,
                            selectedColor = roleColor,
                            onClick = {
                                viewModel.setClientTab(tabIdx)
                                onCloseDrawer()
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))
        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp), color = FreeverseBorder)

        // ACCOUNT & SECURE LOGOUT SECTION
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Button(
                onClick = {
                    onCloseDrawer()
                    viewModel.isLogoutConfirmOpen.value = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEE2E2),
                    contentColor = Color(0xFFDC2626)
                ),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFFDC2626)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log Out",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SidebarSectionHeader(title: String, color: Color) {
    Text(
        text = title,
        fontSize = 10.sp,
        fontWeight = FontWeight.Black,
        color = color,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
    )
}

@Composable
private fun SidebarItemButton(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) selectedColor else FreeverseTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        },
        label = {
            Text(
                text = label,
                fontSize = 12.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        selected = isSelected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = selectedColor.copy(alpha = 0.12f),
            selectedTextColor = selectedColor,
            unselectedTextColor = FreeverseTextPrimary
        ),
        modifier = Modifier.padding(vertical = 1.5.dp)
    )
}

/**
 * Logout Confirmation Dialog
 */
@Composable
fun LogoutConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out of FREEVERSE", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Text(
                "Are you sure you want to log out? You will return to the public landing page.",
                fontSize = 13.5.sp,
                color = FreeverseTextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Log Out", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel", color = FreeverseTextPrimary)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(14.dp)
    )
}
