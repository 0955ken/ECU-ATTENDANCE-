package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.BiometricTemplate
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FingerprintRegistrationScreen(
    students: List<Student>,
    templates: List<BiometricTemplate>,
    scannerState: ScannerConnectionState,
    initialSelectedStudent: Student? = null,
    onEnroll: (Student, String, (Boolean) -> Unit) -> Unit,
    onDeleteTemplate: (Long, String) -> Unit,
    isCapturing: Boolean,
    statusMessage: String?
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStudent by remember { mutableStateOf(initialSelectedStudent ?: students.firstOrNull()) }
    var selectedFinger by remember { mutableStateOf("Right Thumb") }

    LaunchedEffect(initialSelectedStudent) {
        if (initialSelectedStudent != null) {
            selectedStudent = initialSelectedStudent
        }
    }

    val studentTemplates = remember(selectedStudent, templates) {
        if (selectedStudent != null) {
            templates.filter { it.studentId == selectedStudent!!.id }
        } else emptyList()
    }

    val fingerOptions = listOf(
        "Right Thumb",
        "Right Index",
        "Left Thumb",
        "Left Index",
        "Right Middle",
        "Left Middle"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EcuNavyDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EcuNavy),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "BIOMETRIC ENROLLMENT MODULE",
                        color = EcuGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Fingerprint & Thumbprint Template Registration",
                        color = White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Secure biometric capture with ISO-19794 template encryption and quality assessment for SecuGen Hamster Pro 20 USB hardware.",
                        color = White.copy(alpha = 0.7f),
                        fontSize = 11.5.sp
                    )
                }
            }
        }

        // 2. Select Student Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EcuNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "STEP 1: SELECT STUDENT TO ENROLL",
                        color = EcuGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by student name or registration number...", fontSize = 12.sp, color = White.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EcuGold) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EcuGold,
                            unfocusedBorderColor = EcuGold.copy(alpha = 0.3f),
                            focusedTextColor = White,
                            unfocusedTextColor = White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Candidate chips
                    val matchingStudents = students.filter {
                        it.fullName.contains(searchQuery, ignoreCase = true) ||
                                it.registrationNumber.contains(searchQuery, ignoreCase = true)
                    }.take(6)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        matchingStudents.forEach { st ->
                            val isSelected = selectedStudent?.id == st.id
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) EcuGoldContainer else EcuNavy,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) EcuGold else EcuGold.copy(alpha = 0.25f)
                                ),
                                modifier = Modifier
                                    .clickable { selectedStudent = st }
                                    .testTag("select_student_${st.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (st.fingerprintRegistrationStatus == "REGISTERED") EcuGreen else EcuRed
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${st.fullName} (${st.registrationNumber})",
                                        color = if (isSelected) EcuGoldLight else White,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Enrollment Scanner & Capture Console
        if (selectedStudent != null) {
            val student = selectedStudent!!

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EcuNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "STEP 2: BIOMETRIC SCANNING & CAPTURE",
                                    color = EcuGoldLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Enrolling: ${student.fullName} • Reg: ${student.registrationNumber}",
                                    color = White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${student.faculty} • ${student.programme} (${student.classLevel})",
                                    color = White.copy(alpha = 0.65f),
                                    fontSize = 11.sp
                                )
                            }

                            // Enrolled count badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EcuNavyContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold)
                            ) {
                                Text(
                                    text = "${studentTemplates.size} FINGER(S) REGISTERED",
                                    color = EcuGoldLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Finger Selection Buttons
                        Text(
                            text = "Select Finger to Capture:",
                            color = White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            fingerOptions.forEach { finger ->
                                val isSelected = selectedFinger == finger
                                val alreadyEnrolled = studentTemplates.any { it.fingerPosition == finger }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when {
                                        isSelected -> EcuGold
                                        alreadyEnrolled -> EcuGreen.copy(alpha = 0.15f)
                                        else -> EcuNavySurface
                                    },
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        when {
                                            isSelected -> EcuGoldLight
                                            alreadyEnrolled -> EcuGreen
                                            else -> EcuGold.copy(alpha = 0.25f)
                                        }
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedFinger = finger }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Fingerprint,
                                            contentDescription = null,
                                            tint = when {
                                                isSelected -> EcuNavyDark
                                                alreadyEnrolled -> EcuGreen
                                                else -> White.copy(alpha = 0.7f)
                                            },
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = finger,
                                            color = if (isSelected) EcuNavyDark else White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // High-tech Biometric Scanner Pad & Capture Action
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(EcuNavyDark)
                                .border(1.dp, EcuGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .padding(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Scanner Glass Plate
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(EcuNavySurface)
                                        .border(2.dp, if (isCapturing) EcuGreen else EcuGold, RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = "Scanner Surface",
                                        tint = if (isCapturing) EcuGreen else EcuGoldLight,
                                        modifier = Modifier.size(64.dp)
                                    )

                                    if (isCapturing) {
                                        CircularProgressIndicator(
                                            color = EcuGreen,
                                            strokeWidth = 3.dp,
                                            modifier = Modifier.size(88.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                val isConnected = scannerState is ScannerConnectionState.Connected
                                Text(
                                    text = if (isConnected) "Place student's $selectedFinger firmly on the optical scanner" else "USB Scanner not connected",
                                    color = if (isConnected) White else EcuRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        onEnroll(student, selectedFinger) { _ -> }
                                    },
                                    enabled = !isCapturing,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EcuGold,
                                        contentColor = EcuNavyDark
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(44.dp)
                                        .testTag("capture_fingerprint_button")
                                ) {
                                    Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isCapturing) "CAPTURING SAMPLE..." else "CAPTURE & REGISTER $selectedFinger",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        if (statusMessage != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EcuGoldContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGoldLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = statusMessage,
                                    color = EcuGoldLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Currently Enrolled Templates for this student
            item {
                Text(
                    text = "REGISTERED BIOMETRIC TEMPLATES (${studentTemplates.size})",
                    color = EcuGoldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (studentTemplates.isEmpty()) {
                item {
                    Text(
                        text = "No biometric templates registered for this student yet.",
                        color = White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            } else {
                items(studentTemplates) { tmpl ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = EcuNavy),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EcuGreen.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcuGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = tmpl.fingerPosition, color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "Quality: ${tmpl.qualityScore}% • Standard: ${tmpl.templateVersion} • ID #${tmpl.id}",
                                        color = EcuGoldLight,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "Encrypted Minutiae: ${tmpl.encryptedTemplate.take(30)}...",
                                        color = White.copy(alpha = 0.5f),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            IconButton(onClick = { onDeleteTemplate(tmpl.studentId, tmpl.fingerPosition) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Template", tint = EcuRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
