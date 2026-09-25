package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.data.model.UserRole
import com.example.ui.viewmodel.FreeverseViewModel

@Composable
fun DashboardScreen(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser ?: return
    val userRole = UserRole.fromKey(user.role)

    when (userRole) {
        UserRole.SUPER_ADMIN -> SuperAdminDashboard(viewModel = viewModel, modifier = modifier)
        UserRole.FACULTY_COORDINATOR -> FacultyDashboard(viewModel = viewModel, modifier = modifier)
        UserRole.CLUB_COORDINATOR -> CoordinatorDashboard(viewModel = viewModel, modifier = modifier)
        UserRole.MEMBER -> MemberDashboard(viewModel = viewModel, modifier = modifier)
        UserRole.CLIENT -> ClientDashboard(viewModel = viewModel, modifier = modifier)
    }
}
