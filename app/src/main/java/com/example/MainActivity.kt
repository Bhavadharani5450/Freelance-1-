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
import com.example.ui.components.FreeverseBottomNav
import com.example.ui.components.FreeverseDrawerContent
import com.example.ui.components.FreeverseNavbar
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
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Modals state
    val isAuthOpen by viewModel.isAuthDialogOpen.collectAsState()
    val isNotificationsOpen by viewModel.isNotificationsOpen.collectAsState()
    val isPostProjectOpen by viewModel.isPostProjectOpen.collectAsState()
    val isSkillAssessmentOpen by viewModel.isSkillAssessmentOpen.collectAsState()
    val isTeamBuilderOpen by viewModel.isTeamBuilderOpen.collectAsState()
    val selectedFreelancer by viewModel.selectedFreelancerForProfile.collectAsState()
    val selectedProjectForApply by viewModel.selectedProjectForApply.collectAsState()

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            FreeverseDrawerContent(
                viewModel = viewModel,
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = FreeverseBackground,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                FreeverseNavbar(
                    viewModel = viewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
            },
            bottomBar = {
                FreeverseBottomNav(viewModel = viewModel)
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(FreeverseBackground)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        (fadeIn() + slideInHorizontally { width -> width / 4 })
                            .togetherWith(fadeOut() + slideOutHorizontally { width -> -width / 4 })
                    },
                    label = "ScreenTransition"
                ) { screen ->
                    when (screen) {
                        ScreenNav.HOME -> HomeScreen(viewModel = viewModel)
                        ScreenNav.FREELANCERS -> FreelancersScreen(viewModel = viewModel)
                        ScreenNav.PROJECTS -> ProjectsScreen(viewModel = viewModel)
                        ScreenNav.SERVICES -> ServicesScreen(viewModel = viewModel)
                        ScreenNav.GIGS -> CampusGigsScreen(viewModel = viewModel)
                        ScreenNav.EVENTS -> EventsScreen(viewModel = viewModel)
                        ScreenNav.ABOUT_KIT -> AboutKitScreen(viewModel = viewModel)
                        ScreenNav.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                        ScreenNav.MESSAGES -> MessagesScreen(viewModel = viewModel)
                    }
                }
            }

            // Active Dialogs & Sheets
            if (isAuthOpen) {
                AuthDialog(viewModel = viewModel, onDismiss = { viewModel.isAuthDialogOpen.value = false })
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
}
