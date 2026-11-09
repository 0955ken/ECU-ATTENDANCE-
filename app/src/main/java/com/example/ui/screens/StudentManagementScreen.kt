package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AcademicClass
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
import com.example.ui.theme.Slate200
import com.example.ui.theme.White
import com.example.ui.viewmodel.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentManagementScreen(
    students: List<Student>,
    faculties: List<Faculty>,
    classes: List<AcademicClass>,
    onAddOrUpdateStudent: (Student, Boolean) -> Unit,
    onDeleteStudent: (Student) -> Unit,
    onNavigateToEnrollment: (Student) -> Unit,
    onNavigateToUpload: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFacultyFilter by remember { mutableStateOf("All Faculties") }
    var selectedClassFilter by remember { mutableStateOf("All Classes") }

    var studentToEdit by remember { mutableStateOf<Student?>(null) }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var studentToDelete by remember { mutableStateOf<Student?>(null) }
    var showExportConfirmation by remember { mutableStateOf(false) }

    val filteredStudents = remember(students, searchQuery, selectedFacultyFilter, selectedClassFilter) {
        students.filter { student ->
            val matchesQuery = student.fullName.contains(searchQuery, ignoreCase = true) ||
                    student.registrationNumber.contains(searchQuery, ignoreCase = true) ||
                    student.studentIdentificationNumber.contains(searchQuery, ignoreCase = true)
            val matchesFaculty = selectedFacultyFilter == "All Faculties" || student.faculty == selectedFacultyFilter
            val matchesClass = selectedClassFilter == "All Classes" || student.classLevel == selectedClassFilter
            matchesQuery && matchesFaculty && matchesClass
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EcuNavyDark)
            .padding(16.dp)
    ) {
        // Header Row: Title & Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "STUDENT MANAGEMENT MODULE",
                    color = EcuGoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Enrolled Students (${filteredStudents.size} of ${students.size})",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onNavigateToUpload,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EcuGoldLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("student_upload_excel_button")
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Import Excel/CSV", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        studentToEdit = null
                        showAddEditDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_student_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Register Student", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search & Filter Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = EcuNavy),
            border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by student name or Reg No (e.g. 24/BBA/006)...", fontSize = 12.sp, color = White.copy(alpha = 0.5f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EcuGold) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = White.copy(alpha = 0.7f))
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EcuGold,
                        unfocusedBorderColor = EcuGold.copy(alpha = 0.3f),
                        focusedTextColor = White,
                        unfocusedTextColor = White
                    ),
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("student_search_input")
                )

                FilterChipDropdown(
                    label = "Faculty",
                    selectedValue = selectedFacultyFilter,
                    options = listOf("All Faculties") + faculties.map { it.facultyName },
                    onSelect = { selectedFacultyFilter = it }
                )

                FilterChipDropdown(
                    label = "Class",
                    selectedValue = selectedClassFilter,
                    options = listOf("All Classes") + classes.map { it.className },
                    onSelect = { selectedClassFilter = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Student Table / Cards List
        if (filteredStudents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = White.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No students found matching your criteria",
                        color = White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredStudents, key = { it.id }) { student ->
                    StudentItemCard(
                        student = student,
                        onEdit = {
                            studentToEdit = student
                            showAddEditDialog = true
                        },
                        onDelete = { studentToDelete = student },
                        onEnrollFingerprint = { onNavigateToEnrollment(student) }
                    )
                }
            }
        }
    }

    // Add / Edit Student Dialog
    if (showAddEditDialog) {
        AddEditStudentDialog(
            student = studentToEdit,
            faculties = faculties,
            classes = classes,
            onDismiss = { showAddEditDialog = false },
            onSave = { updatedStudent ->
                onAddOrUpdateStudent(updatedStudent, studentToEdit != null)
                showAddEditDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (studentToDelete != null) {
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            title = { Text("Delete Student Record", color = EcuRed, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete student '${studentToDelete?.fullName}' (${studentToDelete?.registrationNumber})? Enrolled biometric templates will also be removed.",
                    color = White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        studentToDelete?.let { onDeleteStudent(it) }
                        studentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EcuRed, contentColor = White)
                ) {
                    Text("Delete Record")
                }
            },
            dismissButton = {
                TextButton(onClick = { studentToDelete = null }) {
                    Text("Cancel", color = White)
                }
            },
            containerColor = EcuNavy,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun StudentItemCard(
    student: Student,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onEnrollFingerprint: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EcuNavy),
        border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Student Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(EcuNavyContainer)
                        .border(1.dp, EcuGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.fullName.take(2).uppercase(),
                        color = EcuGoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = student.fullName,
                            color = White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Status badge
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (student.studentStatus == "ACTIVE") EcuGreen.copy(alpha = 0.15f) else EcuRed.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(
                                0.5.dp,
                                if (student.studentStatus == "ACTIVE") EcuGreen else EcuRed
                            )
                        ) {
                            Text(
                                text = student.studentStatus,
                                color = if (student.studentStatus == "ACTIVE") EcuGreen else EcuRed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Reg: ${student.registrationNumber} • ID: ${student.studentIdentificationNumber}",
                        color = EcuGoldLight,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "${student.faculty} • ${student.programme} (${student.classLevel})",
                        color = White.copy(alpha = 0.65f),
                        fontSize = 11.sp
                    )
                }
            }

            // Biometric Status & Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isEnrolled = student.fingerprintRegistrationStatus == "REGISTERED"

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isEnrolled) EcuGreen.copy(alpha = 0.15f) else EcuGoldContainer,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isEnrolled) EcuGreen else EcuGold
                    ),
                    modifier = Modifier.clickable { onEnrollFingerprint() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = if (isEnrolled) EcuGreen else EcuGoldLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isEnrolled) "ENROLLED" else "ENROLL FINGER",
                            color = if (isEnrolled) EcuGreen else EcuGoldLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Student", tint = Slate200, modifier = Modifier.size(18.dp))
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Student", tint = EcuRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditStudentDialog(
    student: Student?,
    faculties: List<Faculty>,
    classes: List<AcademicClass>,
    onDismiss: () -> Unit,
    onSave: (Student) -> Unit
) {
    var fullName by remember { mutableStateOf(student?.fullName ?: "") }
    var regNumber by remember { mutableStateOf(student?.registrationNumber ?: "24/BBA/010") }
    var faculty by remember { mutableStateOf(student?.faculty ?: faculties.firstOrNull()?.facultyName ?: "Faculty of Business Administration") }
    var department by remember { mutableStateOf(student?.department ?: "Accounting & Finance") }
    var programme by remember { mutableStateOf(student?.programme ?: "Bachelor of Business Administration") }
    var classLevel by remember { mutableStateOf(student?.classLevel ?: "BBA Year 2") }
    var academicYear by remember { mutableStateOf(student?.academicYear ?: "2026") }
    var status by remember { mutableStateOf(student?.studentStatus ?: "ACTIVE") }
    var studentIdNumber by remember { mutableStateOf(student?.studentIdentificationNumber ?: "ECU-2024-010") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (student == null) "Manual Student Registration" else "Edit Student Information",
                color = EcuGoldLight,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    if (errorText != null) {
                        Text(text = errorText!!, color = EcuRed, fontSize = 11.sp)
                    }
                }

                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Student Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("student_form_fullname")
                    )
                }

                item {
                    OutlinedTextField(
                        value = regNumber,
                        onValueChange = { regNumber = it },
                        label = { Text("Registration Number (e.g. 24/BBA/006)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("student_form_reg")
                    )
                }

                item {
                    OutlinedTextField(
                        value = studentIdNumber,
                        onValueChange = { studentIdNumber = it },
                        label = { Text("Student Identification Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = faculty,
                        onValueChange = { faculty = it },
                        label = { Text("Faculty") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("Department") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = programme,
                        onValueChange = { programme = it },
                        label = { Text("Programme (e.g. Bachelor of Business Administration)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = classLevel,
                        onValueChange = { classLevel = it },
                        label = { Text("Class Level (e.g. BBA Year 2)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = academicYear,
                        onValueChange = { academicYear = it },
                        label = { Text("Academic Year (e.g. 2026)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isBlank() || regNumber.isBlank()) {
                        errorText = "Student full name and registration number are mandatory."
                        return@Button
                    }
                    val result = (student ?: Student(
                        fullName = fullName,
                        registrationNumber = regNumber,
                        faculty = faculty,
                        department = department,
                        programme = programme,
                        academicYear = academicYear,
                        classLevel = classLevel,
                        studentStatus = status,
                        fingerprintRegistrationStatus = "NOT_REGISTERED",
                        studentIdentificationNumber = studentIdNumber
                    )).copy(
                        fullName = fullName,
                        registrationNumber = regNumber,
                        faculty = faculty,
                        department = department,
                        programme = programme,
                        academicYear = academicYear,
                        classLevel = classLevel,
                        studentStatus = status,
                        studentIdentificationNumber = studentIdNumber
                    )
                    onSave(result)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark),
                modifier = Modifier.testTag("save_student_button")
            ) {
                Text(if (student == null) "Register Student" else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = White)
            }
        },
        containerColor = EcuNavy,
        shape = RoundedCornerShape(12.dp)
    )
}
