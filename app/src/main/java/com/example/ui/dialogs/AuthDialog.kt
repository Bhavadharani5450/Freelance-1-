package com.example.ui.dialogs

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel

@Composable
fun AuthDialog(
    viewModel: FreeverseViewModel,
    onDismiss: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }

    // Form fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("Kangeyam Institute of Technology") }
    var department by remember { mutableStateOf("Computer Science & Engineering") }
    var skills by remember { mutableStateOf("React, Node.js, UI/UX") }
    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header with Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_freeverse_logo),
                            contentDescription = "FREEVERSE Logo",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "FREEVERSE",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FreeversePrimary
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Surface(shape = RoundedCornerShape(3.dp), color = Color(0xFFEBF3FF)) {
                                    Text("KIT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = FreeverseSecondary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp))
                                }
                            }
                            Text(
                                text = "“Your Campus. Your Skills. Your Opportunities.”",
                                fontSize = 9.5.sp,
                                color = FreeverseTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Sign In / Register Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(FreeverseSoftLavender)
                        .padding(4.dp)
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
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                if (isRegisterMode) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("College / Organization Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = organization,
                        onValueChange = { organization = it },
                        label = { Text("College or Organization Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Role Selection (Student, College Client, External Client)
                    Text("Select Your Account Role:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = FreeverseTextPrimary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(UserRole.STUDENT, UserRole.COLLEGE_CLIENT, UserRole.EXTERNAL_CLIENT).forEach { role ->
                            val isSelected = selectedRole == role
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) FreeversePrimary else FreeverseSoftLavender,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedRole = role }
                            ) {
                                Text(
                                    text = when (role) {
                                        UserRole.STUDENT -> "Student"
                                        UserRole.COLLEGE_CLIENT -> "College"
                                        else -> "Company"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else FreeversePrimary,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    if (selectedRole == UserRole.STUDENT) {
                        OutlinedTextField(
                            value = department,
                            onValueChange = { department = it },
                            label = { Text("Department & Year (e.g. CSE 4th Year)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = skills,
                            onValueChange = { skills = it },
                            label = { Text("Core Skills (e.g. React, Kotlin, UI/UX)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            if (name.isNotBlank() && email.isNotBlank() && password.length >= 6) {
                                viewModel.registerNewUser(
                                    name = name,
                                    email = email,
                                    role = selectedRole.name,
                                    organization = organization,
                                    department = department,
                                    skills = skills
                                )
                                onDismiss()
                            } else {
                                viewModel.showSnackbar("Please fill all required fields (Password min 6 chars).")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary)
                    ) {
                        Text("Create Verified Account", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Sign In Form
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("bhavadharani@freeverse.kit.ac.in") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.showSnackbar("Signed in successfully! Welcome back.")
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary)
                    ) {
                        Text("Sign In to Freeverse", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Demo Test Accounts - 1-Tap Login
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FreeverseSoftLavender,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "QUICK DEMO ACCOUNTS (1-TAP LOGIN)",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = FreeversePrimary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val demoAccounts = listOf(
                                Triple("Student / Freelancer", "student@freeverse.com" to "Student@123", UserRole.STUDENT),
                                Triple("🏢 Client", "client@freeverse.com" to "Client@123", UserRole.CLIENT),
                                Triple("🛡️ Admin", "admin@freeverse.com" to "Admin@123", UserRole.ADMIN),
                                Triple("👑 Super Admin", "superadmin@freeverse.com" to "SuperAdmin@123", UserRole.SUPER_ADMIN),
                                Triple("🎪 Event Manager", "eventmanager@freeverse.com" to "Event@123", UserRole.EVENT_MANAGER)
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                demoAccounts.forEach { (roleName, creds, roleEnum) ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.White,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                email = creds.first
                                                password = creds.second
                                                viewModel.switchUserByRole(roleEnum)
                                                onDismiss()
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp, vertical = 7.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = roleName,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = FreeverseTextPrimary
                                                )
                                                Text(
                                                    text = "${creds.first}  •  ${creds.second}",
                                                    fontSize = 9.5.sp,
                                                    color = FreeverseTextSecondary
                                                )
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = FreeverseSoftLavender
                                            ) {
                                                Text(
                                                    text = "Login",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = FreeversePrimary,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
