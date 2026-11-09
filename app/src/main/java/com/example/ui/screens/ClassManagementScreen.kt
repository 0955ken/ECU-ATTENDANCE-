package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AcademicClass
import com.example.data.model.Programme
import com.example.ui.theme.EcuGold
import com.example.ui.theme.EcuGoldLight
import com.example.ui.theme.EcuNavy
import com.example.ui.theme.EcuNavyDark
import com.example.ui.theme.EcuRed
import com.example.ui.theme.White

@Composable
fun ClassManagementScreen(
    classes: List<AcademicClass>,
    programmes: List<Programme>,
    onAddClass: (name: String, progId: Long, progCode: String, year: String, sem: String, level: String) -> Unit,
    onDeleteClass: (AcademicClass) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EcuNavyDark)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ACADEMIC ARCHITECTURE",
                    color = EcuGoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Class & Cohort Management (${classes.size})",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Configure university academic classes, cohorts and level progression.",
                    color = White.copy(alpha = 0.7f),
                    fontSize = 11.5.sp
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_class_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Academic Class", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(classes) { cls ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = EcuNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, tint = EcuGold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = cls.className, color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Programme Code: ${cls.programmeCode} • Academic Year: ${cls.academicYear} (${cls.semester})",
                                    color = EcuGoldLight,
                                    fontSize = 11.5.sp
                                )
                                Text(text = "Cohort Level: ${cls.level}", color = White.copy(alpha = 0.65f), fontSize = 10.5.sp)
                            }
                        }

                        IconButton(onClick = { onDeleteClass(cls) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EcuRed, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var className by remember { mutableStateOf("BBA Year 5") }
        var programmeCode by remember { mutableStateOf("BBA") }
        var year by remember { mutableStateOf("2026") }
        var sem by remember { mutableStateOf("Semester 1") }
        var level by remember { mutableStateOf("Year 5") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Academic Class", color = EcuGoldLight, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = className, onValueChange = { className = it }, label = { Text("Class Name (e.g. BBA Year 1)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = programmeCode, onValueChange = { programmeCode = it }, label = { Text("Programme Code (e.g. BBA, BSc IT)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("Academic Year") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = sem, onValueChange = { sem = it }, label = { Text("Semester") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = level, onValueChange = { level = it }, label = { Text("Class Level (e.g. Year 1)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (className.isNotBlank()) {
                            onAddClass(className, 1, programmeCode, year, sem, level)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark)
                ) {
                    Text("Save Class")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = White)
                }
            },
            containerColor = EcuNavy,
            shape = RoundedCornerShape(12.dp)
        )
    }
}
