package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.biometrics.ScannerConnectionState
import com.example.ui.theme.EcuGold
import com.example.ui.theme.EcuGoldLight
import com.example.ui.theme.EcuGreen
import com.example.ui.theme.EcuNavy
import com.example.ui.theme.EcuNavyDark
import com.example.ui.theme.EcuNavySurface
import com.example.ui.theme.EcuRed
import com.example.ui.theme.White

@Composable
fun EcuHeader(
    scannerState: ScannerConnectionState,
    onMenuClick: () -> Unit,
    onScannerClick: () -> Unit,
    onLogoutClick: () -> Unit,
    adminName: String = "Admin",
    modifier: Modifier = Modifier
) {
    Surface(
        color = EcuNavy,
        shadowElevation = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Hamburger + University Crest & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("nav_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Toggle Menu",
                        tint = EcuGoldLight
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // University Crest Logo
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, EcuGold, CircleShape)
                        .background(EcuNavyDark),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ecu_logo),
                        contentDescription = "ECU Crest",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "EMMANUEL CHRISTIAN UNIVERSITY",
                        color = EcuGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "ECU DATA ATTENDANCE",
                        color = White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Smart Biometric Attendance Management System",
                        color = White.copy(alpha = 0.7f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // Right: Scanner Status Pill & Logout
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isConnected = scannerState is ScannerConnectionState.Connected
                val statusText = if (isConnected) "SCANNER CONNECTED" else "SCANNER NOT CONNECTED"
                val statusColor = if (isConnected) EcuGreen else EcuRed

                // Scanner status pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isConnected) EcuGreen.copy(alpha = 0.15f) else EcuRed.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .clickable(onClick = onScannerClick)
                        .testTag("scanner_status_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Logout button
                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("logout_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout Administrator",
                        tint = White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
