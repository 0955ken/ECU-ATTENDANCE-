package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.model.Faculty
import com.example.ui.theme.EcuGold
import com.example.ui.theme.EcuGoldLight
import com.example.ui.theme.EcuNavy
import com.example.ui.theme.EcuNavyDark
import com.example.ui.theme.EcuRed
import com.example.ui.theme.White

@Composable
fun FacultyManagementScreen(
    faculties: List<Faculty>,
    onAddFaculty: (name: String, code: String, dean: String, desc: String) -> Unit,
    onDeleteFaculty: (Faculty) -> Unit
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
                    text = "University Faculties (${faculties.size})",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage university faculties, dean leadership and academic departments.",
                    color = White.copy(alpha = 0.7f),
                    fontSize = 11.5.sp
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_faculty_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Faculty", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(faculties) { fac ->
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
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = EcuGold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = fac.facultyName, color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Code: ${fac.code} • Dean: ${fac.deanName.ifBlank { "Appointed Dean" }}",
                                    color = EcuGoldLight,
                                    fontSize = 11.5.sp
                                )
                                Text(text = fac.description, color = White.copy(alpha = 0.65f), fontSize = 10.5.sp)
                            }
                        }

                        IconButton(onClick = { onDeleteFaculty(fac) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EcuRed, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var dean by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add University Faculty", color = EcuGoldLight, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Faculty Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Code (e.g. FBA, FSIT)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = dean, onValueChange = { dean = it }, label = { Text("Dean Full Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && code.isNotBlank()) {
                            onAddFaculty(name, code, dean, desc)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark)
                ) {
                    Text("Save Faculty")
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
