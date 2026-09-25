package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.UserRole
import com.example.ui.components.AuthenticatedTopBar
import com.example.ui.components.LogoutConfirmDialog
import com.example.ui.components.PublicLandingHeader
import com.example.ui.components.RoleSpecificSidebarContent
import com.example.ui.dialogs.*
import com.example.ui.screens.*
import com.example.ui.theme.FreeverseBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FreeverseViewModel
import com.example.ui.viewmodel.ScreenNav
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: FreeverseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = false) {
                FreeverseApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FreeverseApp(viewModel: FreeverseViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Modals state
    val isNotificationsOpen by viewModel.isNotificationsOpen.collectAsState()
    val isPostProjectOpen by viewModel.isPostProjectOpen.collectAsState()
    val isSkillAssessmentOpen by viewModel.isSkillAssessmentOpen.collectAsState()
    val isTeamBuilderOpen by viewModel.isTeamBuilderOpen.collectAsState()
    val isLogoutConfirmOpen by viewModel.isLogoutConfirmOpen.collectAsState()
    val selectedFreelancer by viewModel.selectedFreelancerForProfile.collectAsState()
    val selectedProjectForApply by viewModel.selectedProjectForApply.collectAsState()

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    // Determine if current screen is Public vs Authenticated Portal
    val isPublicScreen = currentScreen == ScreenNav.HOME ||
            currentScreen == ScreenNav.LOGIN_HUB ||
            currentScreen == ScreenNav.LOGIN_ADMIN ||
            currentScreen == ScreenNav.LOGIN_FACULTY ||
            currentScreen == ScreenNav.LOGIN_COORDINATOR ||
            currentScreen == ScreenNav.LOGIN_MEMBER ||
            currentScreen == ScreenNav.LOGIN_CLIENT ||
            currentScreen == ScreenNav.ABOUT_KIT

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isDesktop = maxWidth >= 840.dp

        if (isPublicScreen || currentUser == null) {
            // ==========================================
            // PUBLIC LAYOUT (Landing Page & Login Portals)
            // ==========================================
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = FreeverseBackground,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    if (currentScreen == ScreenNav.HOME) {
                        PublicLandingHeader(viewModel = viewModel)
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(FreeverseBackground)
                ) {
                    ScreenContent(screen = currentScreen, viewModel = viewModel)
                }
            }
        } else {
            // ==========================================
            // AUTHENTICATED ROLE PORTAL (Single Navigation System)
            // ==========================================
            if (isDesktop) {
                // Desktop: Fixed Left Sidebar + Main Content Area
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = FreeverseBackground,
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Sticky Left Sidebar
                        Surface(
                            modifier = Modifier
                                .width(280.dp)
                                .fillMaxHeight(),
                            color = Color.White,
                            shadowElevation = 3.dp
                        ) {
                            RoleSpecificSidebarContent(
                                viewModel = viewModel,
                                onCloseDrawer = {}
                            )
                        }

                        // Main Content Area
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(FreeverseBackground)
                        ) {
                            ScreenContent(screen = currentScreen, viewModel = viewModel)
                        }
                    }
                }
            } else {
                // Mobile: Compact Top Bar + Slide-Out Drawer (closes on selection)
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier.width(310.dp),
                            drawerContainerColor = Color.White
                        ) {
                            RoleSpecificSidebarContent(
                                viewModel = viewModel,
                                onCloseDrawer = {
                                    coroutineScope.launch { drawerState.close() }
                                }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = FreeverseBackground,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            AuthenticatedTopBar(
                                viewModel = viewModel,
                                onOpenDrawer = {
                                    coroutineScope.launch { drawerState.open() }
                                }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(FreeverseBackground)
                        ) {
                            ScreenContent(screen = currentScreen, viewModel = viewModel)
                        }
                    }
                }
            }
        }

        // Global Modals & Dialogs
        if (isLogoutConfirmOpen) {
            LogoutConfirmDialog(
                onConfirm = {
                    viewModel.isLogoutConfirmOpen.value = false
                    viewModel.logout()
                },
                onDismiss = { viewModel.isLogoutConfirmOpen.value = false }
            )
        }

        if (isNotificationsOpen) {
            NotificationsDialog(viewModel = viewModel, onDismiss = { viewModel.isNotificationsOpen.value = false })
        }

        if (isPostProjectOpen) {
            PostProjectDialog(viewModel = viewModel, onDismiss = { viewModel.isPostProjectOpen.value = false })
        }

        if (isSkillAssessmentOpen) {
            SkillAssessmentDialog(viewModel = viewModel, onDismiss = { viewModel.isSkillAssessmentOpen.value = false })
        }

        if (isTeamBuilderOpen) {
            TeamBuilderDialog(viewModel = viewModel, onDismiss = { viewModel.isTeamBuilderOpen.value = false })
        }

        selectedFreelancer?.let { student ->
            FreelancerProfileDialog(
                user = student,
                viewModel = viewModel,
                onDismiss = { viewModel.selectedFreelancerForProfile.value = null }
            )
        }

        selectedProjectForApply?.let { project ->
            ProposalDialog(
                project = project,
                viewModel = viewModel,
                onDismiss = { viewModel.selectedProjectForApply.value = null }
            )
        }
    }
}

@Composable
private fun ScreenContent(screen: ScreenNav, viewModel: FreeverseViewModel) {
    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            fadeIn().togetherWith(fadeOut())
        },
        label = "ScreenTransition"
    ) { current ->
        when (current) {
            ScreenNav.HOME -> HomeScreen(viewModel = viewModel)
            ScreenNav.FREELANCERS -> FreelancersScreen(viewModel = viewModel)
            ScreenNav.PROJECTS -> ProjectsScreen(viewModel = viewModel)
            ScreenNav.SERVICES -> ServicesScreen(viewModel = viewModel)
            ScreenNav.GIGS -> CampusGigsScreen(viewModel = viewModel)
            ScreenNav.EVENTS -> EventsScreen(viewModel = viewModel)
            ScreenNav.ABOUT_KIT -> AboutKitScreen(viewModel = viewModel)
            ScreenNav.MESSAGES -> MessagesScreen(viewModel = viewModel)

            // Public Dedicated Role Login Portals
            ScreenNav.LOGIN_HUB -> LoginHubScreen(viewModel = viewModel)
            ScreenNav.LOGIN_ADMIN -> RoleLoginScreen(targetRole = UserRole.SUPER_ADMIN, viewModel = viewModel)
            ScreenNav.LOGIN_FACULTY -> RoleLoginScreen(targetRole = UserRole.FACULTY_COORDINATOR, viewModel = viewModel)
            ScreenNav.LOGIN_COORDINATOR -> RoleLoginScreen(targetRole = UserRole.CLUB_COORDINATOR, viewModel = viewModel)
            ScreenNav.LOGIN_MEMBER -> RoleLoginScreen(targetRole = UserRole.MEMBER, viewModel = viewModel)
            ScreenNav.LOGIN_CLIENT -> RoleLoginScreen(targetRole = UserRole.CLIENT, viewModel = viewModel)

            // 5 Separate Authenticated Role Dashboards
            ScreenNav.SUPER_ADMIN_DASHBOARD -> SuperAdminDashboard(viewModel = viewModel)
            ScreenNav.FACULTY_DASHBOARD -> FacultyDashboard(viewModel = viewModel)
            ScreenNav.COORDINATOR_DASHBOARD -> CoordinatorDashboard(viewModel = viewModel)
            ScreenNav.MEMBER_DASHBOARD -> MemberDashboard(viewModel = viewModel)
            ScreenNav.CLIENT_DASHBOARD -> ClientDashboard(viewModel = viewModel)

            // Access Restricted Guard
            ScreenNav.ACCESS_RESTRICTED -> AccessRestrictedScreen(viewModel = viewModel)
        }
    }
}
