package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeverseViewModel

@Composable
fun ServicesScreen(
    viewModel: FreeverseViewModel,
    modifier: Modifier = Modifier
) {
    val services by viewModel.allServices.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FreeverseBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "SERVICES MARKETPLACE",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = FreeversePrimary,
            letterSpacing = 1.sp
        )
        Text(
            text = "Packaged Student Services",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = FreeverseTextPrimary
        )
        Text(
            text = "Fixed-price creative & technical services offered directly by verified KIT students.",
            fontSize = 12.5.sp,
            color = FreeverseTextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(services) { srv ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FreeverseBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = FreeverseSoftLavender
                            ) {
                                Text(
                                    text = srv.category,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FreeversePrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = FreeverseWarning, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("${srv.rating} (${srv.reviewsCount})", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = FreeverseTextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(srv.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Offered by ${srv.freelancerName} • ${srv.freelancerRole}", fontSize = 11.sp, color = FreeverseTextSecondary)

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(srv.description, fontSize = 12.sp, color = FreeverseTextSecondary, lineHeight = 16.sp)

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Starting Price", fontSize = 10.sp, color = FreeverseTextSecondary)
                                Text("₹${srv.startingPrice}", fontWeight = FontWeight.Black, fontSize = 17.sp, color = FreeversePrimary)
                                Text("⏱ ${srv.deliveryDays} Days Turnaround", fontSize = 10.5.sp, color = Color(0xFF64748B))
                            }

                            Button(
                                onClick = {
                                    viewModel.showSnackbar("Service inquiry sent to ${srv.freelancerName}!")
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FreeversePrimary),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Order Service", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
