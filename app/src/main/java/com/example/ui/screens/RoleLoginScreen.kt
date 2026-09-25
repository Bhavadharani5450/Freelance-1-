package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.AuthResult
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel
import com.example.ui.viewmodel.ScreenNav
import kotlinx.coroutines.launch

@Composable
fun RoleLoginScreen(
    targetRole: UserRole,
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var isRegisterMode by remember { mutableStateOf(false) }
    var email by remember {
        mutableStateOf(
            when (targetRole) {
                UserRole.SUPER_ADMIN -> "superadmin@freeverse.com"
                UserRole.FACULTY_COORDINATOR -> "jaishimma@freeverse.kit.ac.in"
                UserRole.CLUB_COORDINATOR -> "bhavadharani@freeverse.kit.ac.in"
                UserRole.MEMBER -> "member@freeverse.com"
                UserRole.CLIENT -> "client@freeverse.com"
            }
        )
    }
    var password by remember {
        mutableStateOf(
            when (targetRole) {
                UserRole.SUPER_ADMIN -> "Admin@123"
                UserRole.FACULTY_COORDINATOR -> "Faculty@123"
                UserRole.CLUB_COORDINATOR -> "Coord@123"
                UserRole.MEMBER -> "Student@123"
                UserRole.CLIENT -> "Client@123"
            }
        )
    }
    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Registration fields
    var regName by remember { mutableStateOf("") }
    var regDept by remember { mutableStateOf("Computer Science & Engineering") }
    var regSkills by remember { mutableStateOf("React, UI/UX, Python") }

    val roleColor = Color(targetRole.badgeColorHex)
    val allUsers by viewModel.allUsers.collectAsState()

    // Pre-filter official accounts for this role
    val officialAccounts = remember(allUsers, targetRole) {
        allUsers.filter {
            UserRole.fromKey(it.role) == targetRole
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FreeverseBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Back Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(ScreenNav.LOGIN_HUB) },
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Login Hub",
                    tint = FreeverseTextPrimary
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = roleColor.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, roleColor.copy(alpha = 0.3f))
            ) {
                Text(
                    text = targetRole.displayName.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = roleColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card Container
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, FreeverseBorder, RoundedCornerShape(18.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo & Role Header
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_freeverse_logo),
                        contentDescription = "FREEVERSE Logo",
                        modifier = Modifier
                            .size(52.dp)
                            .padding(6.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${targetRole.displayName} Login",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = FreeverseTextPrimary
                )

                Text(
                    text = targetRole.description,
                    fontSize = 12.sp,
                    color = FreeverseTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mode switch for Member / Client (Register vs Sign In)
                if (targetRole == UserRole.MEMBER || targetRole == UserRole.CLIENT) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(FreeverseSoftLavender)
                            .padding(3.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (!isRegisterMode) Color.White else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isRegisterMode = false }
                        ) {
                            Text(
                                text = "Sign In",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isRegisterMode) FreeversePrimary else FreeverseTextSecondary,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isRegisterMode) Color.White else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isRegisterMode = true }
                        ) {
                            Text(
                                text = "Register",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isRegisterMode) FreeversePrimary else FreeverseTextSecondary,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Error Message Banner if any
                errorMessage?.let { error ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFEF2F2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                fontSize = 11.5.sp,
                                color = Color(0xFFB91C1C),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Form Fields
                if (isRegisterMode) {
                    OutlinedTextField(
                        value = regName,
                        onValueChange = { regName = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = regDept,
                        onValueChange = { regDept = it },
                        label = { Text(if (targetRole == UserRole.CLIENT) "Company / Department" else "Department & Year") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    label = { Text("Official Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = roleColor) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = roleColor) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Login / Submit Button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null
                            val result = viewModel.loginWithRole(email, password, targetRole)
                            isLoading = false
                            if (result is AuthResult.Error) {
                                errorMessage = result.message
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = roleColor),
                    enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (isRegisterMode) "Create Account & Sign In" else "Secure Login",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Official Registered Accounts Helper (For Easy Testing & Demonstration)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OFFICIAL ${targetRole.displayName.uppercase()} ACCOUNTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = roleColor,
                        letterSpacing = 0.5.sp
                    )
                    Text("Tap to autofill", fontSize = 10.sp, color = FreeverseTextSecondary)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    officialAccounts.forEach { acc ->
                        QuickAccountRow(
                            user = acc,
                            roleColor = roleColor,
                            onClick = {
                                email = acc.email
                                password = when (targetRole) {
                                    UserRole.SUPER_ADMIN -> "Admin@123"
                                    UserRole.FACULTY_COORDINATOR -> "Faculty@123"
                                    UserRole.CLUB_COORDINATOR -> "Coord@123"
                                    UserRole.MEMBER -> "Student@123"
                                    UserRole.CLIENT -> "Client@123"
                                }
                                errorMessage = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickAccountRow(
    user: UserEntity,
    roleColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = FreeverseSoftLavender,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreeverseTextPrimary
                    )
                    user.coordinatorDesignation?.let { desig ->
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = roleColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = desig,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = roleColor,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Text(
                    text = user.email,
                    fontSize = 11.sp,
                    color = FreeverseTextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6D7FB))
            ) {
                Text(
                    text = "Select",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = roleColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}
