package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AcademicClass
import com.example.data.model.AttendanceRecord
import com.example.data.model.Faculty
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class WorkingDay(
    val name: String, // Monday, Tuesday, Wednesday, Thursday, Friday
    val dateString: String, // YYYY-MM-DD
    val dayOfMonth: String
)

@Composable
fun WeeklyRegisterScreen(
    students: List<Student>,
    attendanceRecords: List<AttendanceRecord>,
    faculties: List<Faculty>,
    classes: List<AcademicClass>,
    onManualAttendanceUpdate: (studentId: Long, date: String, weekday: String, session: String, newStatus: String, reason: String) -> Unit
) {
    var selectedMondayDate by remember { mutableStateOf("2026-11-02") }
    var selectedFacultyFilter by remember { mutableStateOf("All Faculties") }
    var selectedClassFilter by remember { mutableStateOf("All Classes") }
    var selectedSession by remember { mutableStateOf("Morning Session") }

    // Dialog state for manual attendance override
    var overrideStudent by remember { mutableStateOf<Student?>(null) }
    var overrideDay by remember { mutableStateOf<WorkingDay?>(null) }
    var overrideCurrentStatus by remember { mutableStateOf<String?>("NOT_RECORDED") }
    var showPrintPreview by remember { mutableStateOf(false) }

    // Calculate the 5 working days (Monday to Friday ONLY)
    val workingDays = remember(selectedMondayDate) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        try {
            cal.time = sdf.parse(selectedMondayDate) ?: Date()
        } catch (_: Exception) {
            cal.time = Date()
        }
        // Ensure starting at Monday
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }

        val days = mutableListOf<WorkingDay>()
        val dayNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
        for (i in 0 until 5) {
            val dStr = sdf.format(cal.time)
            val dom = SimpleDateFormat("d MMM", Locale.US).format(cal.time)
            days.add(WorkingDay(dayNames[i], dStr, dom))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        days
    }

    val filteredStudents = remember(students, selectedFacultyFilter, selectedClassFilter) {
        students.filter { st ->
            (selectedFacultyFilter == "All Faculties" || st.faculty == selectedFacultyFilter) &&
            (selectedClassFilter == "All Classes" || st.classLevel == selectedClassFilter)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EcuNavyDark)
            .padding(16.dp)
    ) {
        // 1. Header with Title & Action Controls
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
                    text = "WEEKLY ATTENDANCE REGISTER",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Strict 5 Working Days: Monday to Friday (Saturday & Sunday Excluded)",
                    color = White.copy(alpha = 0.7f),
                    fontSize = 11.5.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { showPrintPreview = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EcuGoldLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("print_register_button")
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Print / Export Register", fontSize = 11.5.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Week Navigation & Filters Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = EcuNavy),
            border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Week Switcher
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            val cal = Calendar.getInstance()
                            cal.time = sdf.parse(selectedMondayDate) ?: Date()
                            cal.add(Calendar.DAY_OF_MONTH, -7)
                            selectedMondayDate = sdf.format(cal.time)
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Previous Week", tint = EcuGoldLight)
                    }

                    Text(
                        text = "Week of: ${workingDays.first().dayOfMonth} - ${workingDays.last().dayOfMonth}, 2026",
                        color = White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            val cal = Calendar.getInstance()
                            cal.time = sdf.parse(selectedMondayDate) ?: Date()
                            cal.add(Calendar.DAY_OF_MONTH, 7)
                            selectedMondayDate = sdf.format(cal.time)
                        }
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next Week", tint = EcuGoldLight)
                    }
                }

                // Filters
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChipDropdown(
                        label = "Class",
                        selectedValue = selectedClassFilter,
                        options = listOf("All Classes") + classes.map { it.className },
                        onSelect = { selectedClassFilter = it }
                    )

                    FilterChipDropdown(
                        label = "Faculty",
                        selectedValue = selectedFacultyFilter,
                        options = listOf("All Faculties") + faculties.map { it.facultyName },
                        onSelect = { selectedFacultyFilter = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Legend:", color = White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(EcuGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = White, modifier = Modifier.size(10.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "PRESENT", color = EcuGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(EcuRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = White, modifier = Modifier.size(10.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "ABSENT", color = EcuRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(EcuSlate),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null, tint = White, modifier = Modifier.size(10.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "NOT YET RECORDED", color = EcuSlate, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Tap cell for manual override (Logged to audit)",
                color = EcuGoldLight,
                fontSize = 10.5.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Weekly Register Table (Scrollable horizontally for wide screens)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = EcuNavy),
            border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.25f))
        ) {
            val horizontalScrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(horizontalScrollState)
            ) {
                // Table Header
                Row(
                    modifier = Modifier
                        .background(EcuNavyContainer)
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "No.", color = EcuGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
                    Text(text = "Student Name", color = EcuGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(180.dp))
                    Text(text = "Reg Number", color = EcuGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(110.dp))
                    Text(text = "Class", color = EcuGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))

                    workingDays.forEach { day ->
                        Column(
                            modifier = Modifier.width(90.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = day.name.take(3), color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = day.dayOfMonth, color = EcuGoldLight, fontSize = 9.sp)
                        }
                    }

                    Text(text = "Pres.", color = EcuGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(50.dp), textAlign = TextAlign.Center)
                    Text(text = "%", color = EcuGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(55.dp), textAlign = TextAlign.Center)
                }

                HorizontalDivider(color = EcuGold.copy(alpha = 0.3f), thickness = 1.dp)

                // Table Rows
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(filteredStudents) { index, student ->
                        var presentCount = 0
                        val rowBg = if (index % 2 == 0) EcuNavy else EcuNavySurface

                        Row(
                            modifier = Modifier
                                .background(rowBg)
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${index + 1}", color = White.copy(alpha = 0.7f), fontSize = 11.sp, modifier = Modifier.width(36.dp))
                            Text(text = student.fullName, color = White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(180.dp))
                            Text(text = student.registrationNumber, color = EcuGoldLight, fontSize = 11.sp, modifier = Modifier.width(110.dp))
                            Text(text = student.classLevel, color = White.copy(alpha = 0.8f), fontSize = 11.sp, modifier = Modifier.width(100.dp))

                            workingDays.forEach { day ->
                                val rec = attendanceRecords.find {
                                    it.studentId == student.id && it.attendanceDate == day.dateString
                                }
                                val status = rec?.status ?: "NOT_RECORDED"
                                if (status == "PRESENT") presentCount++

                                Box(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .clickable {
                                            overrideStudent = student
                                            overrideDay = day
                                            overrideCurrentStatus = status
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (status) {
                                        "PRESENT" -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(EcuGreen),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = "Present", tint = White, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                        "ABSENT" -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(EcuRed),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Absent", tint = White, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                        else -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(EcuSlate.copy(alpha = 0.35f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Remove, contentDescription = "Not recorded", tint = White.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            val percentage = (presentCount.toFloat() / 5f) * 100f
                            Text(
                                text = "$presentCount/5",
                                color = if (presentCount >= 4) EcuGreen else EcuGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(50.dp),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = String.format(Locale.US, "%.0f%%", percentage),
                                color = if (percentage >= 80f) EcuGreen else if (percentage >= 50f) EcuGold else EcuRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(55.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        HorizontalDivider(color = White.copy(alpha = 0.05f), thickness = 0.5.dp)
                    }
                }
            }
        }
    }

    // Manual Attendance Override Dialog
    if (overrideStudent != null && overrideDay != null) {
        val student = overrideStudent!!
        val day = overrideDay!!
        var selectedNewStatus by remember { mutableStateOf(if (overrideCurrentStatus == "PRESENT") "ABSENT" else "PRESENT") }
        var justificationReason by remember { mutableStateOf("") }
        var justificationError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { overrideStudent = null },
            title = {
                Text(
                    text = "Manual Attendance Correction",
                    color = EcuGoldLight,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Student: ${student.fullName} (${student.registrationNumber})\nDate: ${day.name}, ${day.dayOfMonth} (2026)",
                        color = White,
                        fontSize = 12.5.sp
                    )
                    Text(
                        text = "Current Status: ${overrideCurrentStatus ?: "NOT_RECORDED"}",
                        color = EcuGoldLight,
                        fontSize = 11.5.sp
                    )

                    HorizontalDivider(color = EcuGold.copy(alpha = 0.2f))

                    Text(text = "Select New Status:", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedNewStatus == "PRESENT",
                            onClick = { selectedNewStatus = "PRESENT" },
                            colors = RadioButtonDefaults.colors(selectedColor = EcuGreen)
                        )
                        Text(text = "PRESENT", color = EcuGreen, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.width(16.dp))

                        RadioButton(
                            selected = selectedNewStatus == "ABSENT",
                            onClick = { selectedNewStatus = "ABSENT" },
                            colors = RadioButtonDefaults.colors(selectedColor = EcuRed)
                        )
                        Text(text = "ABSENT", color = EcuRed, fontWeight = FontWeight.Bold)
                    }

                    OutlinedTextField(
                        value = justificationReason,
                        onValueChange = { justificationReason = it },
                        label = { Text("Justification / Reason (Mandatory for Audit)") },
                        placeholder = { Text("e.g., Authorized university medical certificate submitted", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EcuGold,
                            unfocusedBorderColor = EcuGold.copy(alpha = 0.4f),
                            focusedTextColor = White,
                            unfocusedTextColor = White
                        )
                    )

                    if (justificationError != null) {
                        Text(text = justificationError!!, color = EcuRed, fontSize = 11.sp)
                    }

                    Text(
                        text = "Note: All manual corrections are automatically stamped into the Administrator Audit Log.",
                        color = White.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (justificationReason.isBlank()) {
                            justificationError = "Please enter a justification reason for the audit log."
                            return@Button
                        }
                        onManualAttendanceUpdate(
                            student.id,
                            day.dateString,
                            day.name,
                            selectedSession,
                            selectedNewStatus,
                            justificationReason
                        )
                        overrideStudent = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark)
                ) {
                    Text("Save Correction & Audit")
                }
            },
            dismissButton = {
                TextButton(onClick = { overrideStudent = null }) {
                    Text("Cancel", color = White)
                }
            },
            containerColor = EcuNavy,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Print / Export Register Preview Dialog
    if (showPrintPreview) {
        AlertDialog(
            onDismissRequest = { showPrintPreview = false },
            title = {
                Text(
                    text = "Official Weekly Register Report",
                    color = EcuGoldLight,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "EMMANUEL CHRISTIAN UNIVERSITY\nECU DATA ATTENDANCE • WEEKLY REGISTER",
                        color = EcuGoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Academic Year: 2026 • Class: $selectedClassFilter • Faculty: $selectedFacultyFilter",
                        color = White,
                        fontSize = 11.5.sp
                    )
                    Text(
                        text = "Week Period: ${workingDays.first().dateString} to ${workingDays.last().dateString}",
                        color = White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Total Enrolled Students: ${filteredStudents.size}\nStrict working days: Mon - Fri",
                        color = White,
                        fontSize = 11.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EcuGreen.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EcuGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Report generated and formatted for PDF / Excel printing with administrator security watermark.",
                            color = EcuGreen,
                            fontSize = 10.5.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrintPreview = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark)
                ) {
                    Text("Close Preview")
                }
            },
            containerColor = EcuNavy,
            shape = RoundedCornerShape(12.dp)
        )
    }
}
