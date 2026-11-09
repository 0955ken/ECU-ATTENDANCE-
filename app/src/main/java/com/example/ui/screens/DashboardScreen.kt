package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AcademicClass
import com.example.data.model.AttendanceRecord
import com.example.data.model.Faculty
import com.example.data.model.Programme
import com.example.data.model.Student
import com.example.ui.theme.EcuGold
import com.example.ui.theme.EcuGoldContainer
import com.example.ui.theme.EcuGoldLight
import com.example.ui.theme.EcuGreen
import com.example.ui.theme.EcuNavy
import com.example.ui.theme.EcuNavyContainer
import com.example.ui.theme.EcuNavyDark
import com.example.ui.theme.EcuNavySurface
import com.example.ui.theme.EcuRed
import com.example.ui.theme.EcuSlate
import com.example.ui.theme.Slate200
import com.example.ui.theme.White
import com.example.ui.viewmodel.AppScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    students: List<Student>,
    totalStudentsCount: Int,
    activeStudentsCount: Int,
    attendanceList: List<AttendanceRecord>,
    faculties: List<Faculty>,
    programmes: List<Programme>,
    classes: List<AcademicClass>,
    onNavigate: (AppScreen) -> Unit
) {
    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val formattedDisplayDate = remember { SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US).format(Date()) }

    var selectedFacultyFilter by remember { mutableStateOf("All Faculties") }
    var selectedClassFilter by remember { mutableStateOf("All Classes") }
    var selectedYearFilter by remember { mutableStateOf("2026") }
    var selectedSemesterFilter by remember { mutableStateOf("Semester 1") }

    // Filter students
    val filteredStudents = remember(students, selectedFacultyFilter, selectedClassFilter, selectedYearFilter) {
        students.filter { student ->
            (selectedFacultyFilter == "All Faculties" || student.faculty == selectedFacultyFilter) &&
            (selectedClassFilter == "All Classes" || student.classLevel == selectedClassFilter) &&
            (selectedYearFilter == "All Years" || student.academicYear == selectedYearFilter)
        }
    }

    // Attendance stats for today
    val todayRecords = attendanceList.filter { it.attendanceDate == todayDate }
    val presentTodayCount = todayRecords.count { it.status == "PRESENT" }
    val absentTodayCount = if (filteredStudents.isNotEmpty()) {
        (filteredStudents.size - presentTodayCount).coerceAtLeast(0)
    } else {
        0
    }

    val dailyPercentage = if (filteredStudents.isNotEmpty()) {
        ((presentTodayCount.toFloat() / filteredStudents.size.toFloat()) * 100f).coerceIn(0f, 100f)
    } else 0f

    val totalRecordsCount = attendanceList.size
    val registeredBiometricCount = filteredStudents.count { it.fingerprintRegistrationStatus == "REGISTERED" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EcuNavyDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Dashboard Banner Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EcuNavy),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ADMINISTRATOR DASHBOARD",
                            color = EcuGoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Executive Attendance & Biometric Analytics",
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Date: $formattedDisplayDate • Academic Year $selectedYearFilter ($selectedSemesterFilter)",
                            color = White.copy(alpha = 0.7f),
                            fontSize = 11.5.sp
                        )
                    }

                    Button(
                        onClick = { onNavigate(AppScreen.BIOMETRIC_ATTENDANCE) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EcuGold,
                            contentColor = EcuNavyDark
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("dashboard_start_scanning_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Start Live Attendance", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // 2. Filter Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EcuNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "FILTERS (Real-Time Update)",
                        color = EcuGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Faculty filter chip
                        FilterChipDropdown(
                            label = "Faculty",
                            selectedValue = selectedFacultyFilter,
                            options = listOf("All Faculties") + faculties.map { it.facultyName },
                            onSelect = { selectedFacultyFilter = it }
                        )

                        // Class filter chip
                        FilterChipDropdown(
                            label = "Class",
                            selectedValue = selectedClassFilter,
                            options = listOf("All Classes") + classes.map { it.className },
                            onSelect = { selectedClassFilter = it }
                        )

                        // Academic Year
                        FilterChipDropdown(
                            label = "Year",
                            selectedValue = selectedYearFilter,
                            options = listOf("2026", "2025", "2024", "All Years"),
                            onSelect = { selectedYearFilter = it }
                        )

                        // Semester
                        FilterChipDropdown(
                            label = "Semester",
                            selectedValue = selectedSemesterFilter,
                            options = listOf("Semester 1", "Semester 2"),
                            onSelect = { selectedSemesterFilter = it }
                        )
                    }
                }
            }
        }

        // 3. Stat Cards Grid (Total Students, Present Today, Absent Today, Daily Attendance %)
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                maxItemsInEachRow = 4
            ) {
                StatCard(
                    title = "Total Students",
                    value = filteredStudents.size.toString(),
                    subtitle = "Active: $activeStudentsCount",
                    icon = Icons.Default.People,
                    color = EcuGoldLight,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Present Today",
                    value = presentTodayCount.toString(),
                    subtitle = "Biometric verified",
                    icon = Icons.Default.CheckCircle,
                    color = EcuGreen,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Absent Today",
                    value = absentTodayCount.toString(),
                    subtitle = "Unverified/Pending",
                    icon = Icons.Default.EventBusy,
                    color = EcuRed,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Daily Attendance",
                    value = String.format(Locale.US, "%.1f%%", dailyPercentage),
                    subtitle = "Formula: (Pres/Total)×100",
                    icon = Icons.Default.Assessment,
                    color = EcuGold,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Secondary Stat Row (Biometric Enrolled, Total Records)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Fingerprint Enrolled",
                    value = "$registeredBiometricCount / ${filteredStudents.size}",
                    subtitle = "Enrolled Biometric Profiles",
                    icon = Icons.Default.Fingerprint,
                    color = EcuGoldLight,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Total Attendance Records",
                    value = totalRecordsCount.toString(),
                    subtitle = "Saved in Encrypted SQLite",
                    icon = Icons.Default.CalendarViewWeek,
                    color = Slate200,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 5. Visual Charts (Canvas Attendance Bar Chart + Circular Progress Ring)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Faculty Distribution Bar Chart
                Card(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(260.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EcuNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "STUDENTS BY FACULTY",
                            color = EcuGoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        val facultyCounts = remember(filteredStudents, faculties) {
                            faculties.map { fac ->
                                val count = filteredStudents.count { it.faculty.contains(fac.code, ignoreCase = true) || it.faculty == fac.facultyName }
                                Pair(fac.code, count)
                            }
                        }

                        val maxCount = (facultyCounts.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            facultyCounts.forEach { (code, count) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = code,
                                        color = White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(44.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(18.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(EcuNavySurface)
                                    ) {
                                        val fillFraction = (count.toFloat() / maxCount.toFloat()).coerceIn(0.05f, 1f)
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(fillFraction)
                                                .height(18.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    Brush.horizontalGradient(listOf(EcuNavyContainer, EcuGold))
                                                )
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "$count",
                                        color = EcuGoldLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Attendance Gauge Progress Ring
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(260.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EcuNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "DAILY ATTENDANCE RATE",
                            color = EcuGoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier.size(130.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val animatedSweep by animateFloatAsState(
                                targetValue = (dailyPercentage / 100f) * 360f,
                                animationSpec = tween(durationMillis = 800),
                                label = "gaugeSweep"
                            )

                            Canvas(modifier = Modifier.size(120.dp)) {
                                val strokeWidth = 12.dp.toPx()
                                // Background ring
                                drawArc(
                                    color = Color(0xFF1E293B),
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth)
                                )
                                // Active arc
                                drawArc(
                                    color = if (dailyPercentage >= 75f) EcuGreen else EcuGold,
                                    startAngle = -90f,
                                    sweepAngle = animatedSweep,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = String.format(Locale.US, "%.0f%%", dailyPercentage),
                                    color = White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Present",
                                    color = EcuGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Text(
                            text = "$presentTodayCount Present • $absentTodayCount Absent",
                            color = White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 6. Quick Action Shortcuts
        item {
            Text(
                text = "QUICK ADMINISTRATIVE ACTIONS",
                color = EcuGoldLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionItem(
                    label = "Live Attendance",
                    icon = Icons.Default.CheckCircle,
                    color = EcuGreen,
                    onClick = { onNavigate(AppScreen.BIOMETRIC_ATTENDANCE) },
                    modifier = Modifier.weight(1f)
                )

                QuickActionItem(
                    label = "Register Fingerprint",
                    icon = Icons.Default.Fingerprint,
                    color = EcuGold,
                    onClick = { onNavigate(AppScreen.FINGERPRINT_ENROLLMENT) },
                    modifier = Modifier.weight(1f)
                )

                QuickActionItem(
                    label = "Weekly Register",
                    icon = Icons.Default.CalendarViewWeek,
                    color = EcuGoldLight,
                    onClick = { onNavigate(AppScreen.WEEKLY_REGISTER) },
                    modifier = Modifier.weight(1f)
                )

                QuickActionItem(
                    label = "Upload Student List",
                    icon = Icons.Default.UploadFile,
                    color = Slate200,
                    onClick = { onNavigate(AppScreen.UPLOAD_STUDENTS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(115.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EcuNavy),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = White.copy(alpha = 0.8f),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = value,
                color = White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = subtitle,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun QuickActionItem(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
        color = EcuNavy
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                color = White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = EcuNavy,
            border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.4f)),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$label: ",
                    color = EcuGoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = selectedValue,
                    color = White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(EcuNavy)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option, color = White, fontSize = 12.sp) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
