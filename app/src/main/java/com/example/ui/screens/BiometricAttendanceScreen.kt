package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biometrics.ScannerConnectionState
import com.example.data.model.AcademicClass
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceSession
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
import com.example.ui.theme.Slate200
import com.example.ui.theme.White
import com.example.ui.viewmodel.VerificationStatus
import com.example.ui.viewmodel.VerificationUiState
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometricAttendanceScreen(
    scannerState: ScannerConnectionState,
    verificationUiState: VerificationUiState,
    students: List<Student>,
    classes: List<AcademicClass>,
    sessions: List<AttendanceSession>,
    todayAttendanceRecords: List<AttendanceRecord>,
    selectedClass: String,
    onClassChange: (String) -> Unit,
    academicYear: String,
    onYearChange: (String) -> Unit,
    selectedSession: String,
    onSessionChange: (String) -> Unit,
    onScanFingerprint: (Long?) -> Unit,
    onResetBanner: () -> Unit,
    onEnableDiagnosticScanner: (Boolean) -> Unit
) {
    // Real-time ticking clock
    var currentTimeString by remember {
        mutableStateOf(SimpleDateFormat("HH:mm:ss", Locale.US).format(Date()))
    }
    val currentDateString = remember {
        SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US).format(Date())
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTimeString = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
            delay(1000)
        }
    }

    val isConnected = scannerState is ScannerConnectionState.Connected
    val isScanning = verificationUiState.status == VerificationStatus.SCANNING

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EcuNavyDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Dedicated Header Banner: ECU DATA ATTENDANCE, Date, Time, Class, Year, Session
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EcuNavy),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "EMMANUEL CHRISTIAN UNIVERSITY",
                                color = EcuGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "ECU DATA ATTENDANCE",
                                color = White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "1:N Automated Biometric Verification",
                                color = White.copy(alpha = 0.7f),
                                fontSize = 11.5.sp
                            )
                        }

                        // Live Clock Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EcuNavyDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = EcuGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentTimeString,
                                    color = White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Session, Class & Date Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = EcuGoldLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = currentDateString, color = White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Class & Session Pickers
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AttendanceDropdownChip(
                                label = "Class",
                                value = selectedClass,
                                options = listOf("All Classes") + classes.map { it.className },
                                onSelect = onClassChange
                            )

                            AttendanceDropdownChip(
                                label = "Session",
                                value = selectedSession,
                                options = listOf("Morning Session", "Afternoon Session", "Evening Session"),
                                onSelect = onSessionChange
                            )
                        }
                    }
                }
            }
        }

        // 2. Hardware Scanner Status Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EcuNavySurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isConnected) EcuGreen.copy(alpha = 0.5f) else EcuRed.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (isConnected) EcuGreen else EcuRed)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isConnected) "SCANNER CONNECTED" else "SCANNER NOT CONNECTED",
                                color = if (isConnected) EcuGreen else EcuRed,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isConnected) {
                                    val conn = scannerState as ScannerConnectionState.Connected
                                    "${conn.deviceName} • VID: 0x${Integer.toHexString(conn.vendorId).uppercase()} PID: 0x${Integer.toHexString(conn.productId).uppercase()}"
                                } else {
                                    "Attach SecuGen Hamster Pro 20 via USB OTG or enable Diagnostic Test Mode below."
                                },
                                color = White.copy(alpha = 0.65f),
                                fontSize = 10.5.sp
                            )
                        }
                    }

                    if (!isConnected) {
                        Button(
                            onClick = { onEnableDiagnosticScanner(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("enable_diagnostic_button")
                        ) {
                            Text("Enable Test Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. Verification Notification Banners
        item {
            AnimatedVisibility(
                visible = verificationUiState.status != VerificationStatus.IDLE,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                when (verificationUiState.status) {
                    VerificationStatus.SUCCESS -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EcuGreen.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, EcuGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("attendance_success_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcuGreen, modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "ATTENDANCE SUCCESSFULLY RECORDED",
                                            color = EcuGreen,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Student successfully identified and marked PRESENT for today's session.",
                                            color = White,
                                            fontSize = 11.5.sp
                                        )
                                    }
                                }
                                IconButton(onClick = onResetBanner) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = White)
                                }
                            }
                        }
                    }
                    VerificationStatus.ALREADY_RECORDED -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EcuGold.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, EcuGold),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("attendance_already_recorded_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = EcuGold, modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "ATTENDANCE ALREADY RECORDED",
                                            color = EcuGoldLight,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "This student was already marked PRESENT today in $selectedSession.",
                                            color = White,
                                            fontSize = 11.5.sp
                                        )
                                    }
                                }
                                IconButton(onClick = onResetBanner) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = White)
                                }
                            }
                        }
                    }
                    VerificationStatus.NOT_RECOGNIZED -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EcuRed.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, EcuRed),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("attendance_not_recognized_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Error, contentDescription = null, tint = EcuRed, modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "FINGERPRINT NOT RECOGNIZED. PLEASE TRY AGAIN.",
                                            color = EcuRed,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "No matching biometric template found in the university database.",
                                            color = White,
                                            fontSize = 11.5.sp
                                        )
                                    }
                                }
                                IconButton(onClick = onResetBanner) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = White)
                                }
                            }
                        }
                    }
                    VerificationStatus.ERROR -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EcuRed.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, EcuRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = verificationUiState.message,
                                    color = EcuRed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = onResetBanner) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = White)
                                }
                            }
                        }
                    }
                    VerificationStatus.SCANNING -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EcuGoldContainer,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, EcuGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(color = EcuGoldLight, strokeWidth = 2.5.dp, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Capturing fingerprint and executing 1:N database identification...",
                                    color = EcuGoldLight,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }
        }

        // 4. Student Identification Card (Displays upon match)
        if (verificationUiState.student != null) {
            val st = verificationUiState.student!!
            val verifiedTime = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(verificationUiState.timestamp.takeIf { it > 0 } ?: System.currentTimeMillis()))

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EcuNavy),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, EcuGold),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "IDENTIFIED STUDENT PROFILE",
                                color = EcuGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EcuGreen,
                                contentColor = EcuNavyDark
                            ) {
                                Text(
                                    text = "STATUS: PRESENT",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar Badge
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(EcuNavyContainer)
                                    .border(2.dp, EcuGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = st.fullName.take(2).uppercase(),
                                    color = EcuGoldLight,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = st.fullName,
                                    color = White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Registration Number: ${st.registrationNumber}",
                                    color = EcuGoldLight,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Faculty: ${st.faculty}",
                                    color = White.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Programme: ${st.programme} • Class: ${st.classLevel}",
                                    color = White.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Academic Year: ${st.academicYear} • Verified At: $verifiedTime (${verificationUiState.fingerPosition})",
                                    color = EcuGreen,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Biometric Scanner Action & Simulation Console
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EcuNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "FINGERPRINT SCANNING INSTRUCTIONS",
                        color = EcuGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Direct student to place registered thumb or index finger on the USB scanner window.",
                        color = White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Big Scanner Trigger Button
                    Button(
                        onClick = { onScanFingerprint(null) },
                        enabled = !isScanning && isConnected,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EcuGold,
                            contentColor = EcuNavyDark
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(50.dp)
                            .testTag("scan_fingerprint_button")
                    ) {
                        Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isScanning) "IDENTIFYING..." else "SCAN FINGERPRINT (1:N)",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Simulated Student Finger Tap (For testing verification on tablet/emulator)
                    Text(
                        text = "Or tap an enrolled student below to simulate scanner touch:",
                        color = EcuGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val registeredStudents = students.filter { it.fingerprintRegistrationStatus == "REGISTERED" }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        registeredStudents.take(4).forEach { st ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EcuNavy,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onScanFingerprint(st.id) }
                                    .testTag("simulate_finger_${st.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = EcuGoldLight, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = st.fullName.split(" ").firstOrNull() ?: st.fullName,
                                        color = White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = st.registrationNumber, color = EcuGoldLight, fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Today's Live Attendance Feed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S VERIFIED ATTENDANCE LOG",
                    color = EcuGoldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${todayAttendanceRecords.size} Records Today",
                    color = White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        }

        if (todayAttendanceRecords.isEmpty()) {
            item {
                Text(
                    text = "No attendance recorded yet for today's session.",
                    color = White.copy(alpha = 0.5f),
                    fontSize = 11.5.sp
                )
            }
        } else {
            items(todayAttendanceRecords) { rec ->
                val matchingStudent = students.find { it.id == rec.studentId }
                val formattedTime = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(rec.verifiedAt))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = EcuNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcuGreen.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcuGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = matchingStudent?.fullName ?: "Student #${rec.studentId}",
                                    color = White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Reg: ${matchingStudent?.registrationNumber ?: "-"} • Class: ${matchingStudent?.classLevel ?: "-"}",
                                    color = EcuGoldLight,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EcuGreen.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, EcuGreen)
                            ) {
                                Text(
                                    text = rec.status,
                                    color = EcuGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = formattedTime, color = White.copy(alpha = 0.6f), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceDropdownChip(
    label: String,
    value: String,
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
            color = EcuNavyDark,
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
                    text = value,
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
