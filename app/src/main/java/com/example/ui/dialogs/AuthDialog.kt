package com.example.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel
import com.example.ui.viewmodel.ScreenNav

@Composable
fun AuthDialog(
    viewModel: FreeverseViewModel,
    onDismiss: () -> Unit
) {
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

                Text(
                    text = "Select Your Role Login Portal",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreeverseTextPrimary
                )

                val roleOptions = listOf(
                    Triple(UserRole.SUPER_ADMIN, "Super Admin", ScreenNav.LOGIN_ADMIN),
                    Triple(UserRole.FACULTY_COORDINATOR, "Faculty Coordinator", ScreenNav.LOGIN_FACULTY),
                    Triple(UserRole.CLUB_COORDINATOR, "Club Coordinator", ScreenNav.LOGIN_COORDINATOR),
                    Triple(UserRole.MEMBER, "Member / Freelancer", ScreenNav.LOGIN_MEMBER),
                    Triple(UserRole.CLIENT, "Client", ScreenNav.LOGIN_CLIENT)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    roleOptions.forEach { (role, title, screen) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = FreeverseSoftLavender,
                            border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDismiss()
                                    viewModel.navigateTo(screen)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Color(role.badgeColorHex))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(role.description, fontSize = 10.5.sp, color = FreeverseTextSecondary)
                                    }
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Go",
                                    tint = FreeversePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        onDismiss()
                        viewModel.navigateTo(ScreenNav.LOGIN_HUB)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary)
                ) {
                    Text("Open Full Login Hub", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
