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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EcuGold
import com.example.ui.theme.EcuGoldContainer
import com.example.ui.theme.EcuGoldLight
import com.example.ui.theme.EcuNavy
import com.example.ui.theme.EcuNavyDark
import com.example.ui.theme.EcuNavySurface
import com.example.ui.theme.EcuRed
import com.example.ui.theme.White
import com.example.ui.viewmodel.AppScreen

data class NavItem(
    val screen: AppScreen,
    val icon: ImageVector,
    val label: String,
    val tag: String
)

val ECU_NAV_ITEMS = listOf(
    NavItem(AppScreen.DASHBOARD, Icons.Default.Dashboard, "Dashboard", "nav_dashboard"),
    NavItem(AppScreen.STUDENTS, Icons.Default.People, "Student Management", "nav_students"),
    NavItem(AppScreen.UPLOAD_STUDENTS, Icons.Default.UploadFile, "Upload Student Lists", "nav_upload"),
    NavItem(AppScreen.FINGERPRINT_ENROLLMENT, Icons.Default.Fingerprint, "Fingerprint Registration", "nav_fingerprint"),
    NavItem(AppScreen.BIOMETRIC_ATTENDANCE, Icons.Default.CheckCircle, "Biometric Attendance", "nav_attendance"),
    NavItem(AppScreen.WEEKLY_REGISTER, Icons.Default.CalendarViewWeek, "Weekly Attendance Register", "nav_weekly"),
    NavItem(AppScreen.CLASS_MANAGEMENT, Icons.Default.School, "Class Management", "nav_classes"),
    NavItem(AppScreen.FACULTY_MANAGEMENT, Icons.Default.AccountBalance, "Faculty Management", "nav_faculties"),
    NavItem(AppScreen.ATTENDANCE_REPORTS, Icons.Default.Assessment, "Attendance Reports", "nav_reports"),
    NavItem(AppScreen.BACKUP_RESTORE, Icons.Default.Backup, "Backup & Restore", "nav_backup"),
    NavItem(AppScreen.SETTINGS, Icons.Default.Settings, "Settings", "nav_settings")
)

@Composable
fun EcuSidebar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    onLogout: () -> Unit,
    adminFullName: String = "Dr. ECU Registrar",
    modifier: Modifier = Modifier
) {
    Surface(
        color = EcuNavyDark,
        modifier = modifier
            .width(280.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp, horizontal = 12.dp)
        ) {
            // University Badge & Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, EcuGold, CircleShape)
                        .background(EcuNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ecu_logo),
                        contentDescription = "ECU Crest",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "ECU ATTENDANCE",
                        color = EcuGoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Admin: $adminFullName",
                        color = White.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                    Text(
                        text = "Offline Biometric System",
                        color = EcuGold.copy(alpha = 0.8f),
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = EcuGold.copy(alpha = 0.25f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Navigation Items List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ECU_NAV_ITEMS.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    val bgColor = if (isSelected) EcuGoldContainer else Color.Transparent
                    val contentColor = if (isSelected) EcuGoldLight else White.copy(alpha = 0.85f)
                    val iconTint = if (isSelected) EcuGoldLight else EcuGold.copy(alpha = 0.75f)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgColor)
                            .clickable { onNavigate(item.screen) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag(item.tag),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = item.label,
                            color = contentColor,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = White.copy(alpha = 0.1f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Item 12: Logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onLogout() }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .testTag("nav_logout"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = EcuRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Logout",
                    color = EcuRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
