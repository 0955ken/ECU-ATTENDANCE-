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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Student
import com.example.ui.theme.EcuGold
import com.example.ui.theme.EcuGoldContainer
import com.example.ui.theme.EcuGoldLight
import com.example.ui.theme.EcuGreen
import com.example.ui.theme.EcuNavy
import com.example.ui.theme.EcuNavyDark
import com.example.ui.theme.EcuNavySurface
import com.example.ui.theme.EcuRed
import com.example.ui.theme.White

data class ParsedStudentPreview(
    val fullName: String,
    val regNumber: String,
    val faculty: String,
    val programme: String,
    val classLevel: String,
    val isDuplicate: Boolean
)

@Composable
fun UploadStudentsScreen(
    existingStudents: List<Student>,
    onImportCsv: (String, (Int, Int) -> Unit) -> Unit,
    onNavigateBack: () -> Unit
) {
    val sampleCsv = remember {
        """Full Name,Registration Number,Faculty,Programme,Class Level,Academic Year
David Mayen Chol,24/BBA/015,Faculty of Business Administration,Bachelor of Business Administration,BBA Year 2,2026
Esther Nyibol Garang,24/BBA/016,Faculty of Business Administration,Bachelor of Business Administration,BBA Year 2,2026
Simon Deng Kuol,24/IT/025,Faculty of Science & Information Technology,Bachelor of Science in Information Technology,BSc IT Year 2,2026
Tabitha Achol Peter,24/THEO/009,Faculty of Theology & Biblical Studies,Bachelor of Theology,BTh Year 1,2026"""
    }

    var csvText by remember { mutableStateOf(sampleCsv) }
    var importResultMsg by remember { mutableStateOf<String?>(null) }
    var parsedPreview by remember { mutableStateOf<List<ParsedStudentPreview>>(emptyList()) }

    fun parseAndPreview() {
        val existingRegs = existingStudents.map { it.registrationNumber.trim().lowercase() }.toSet()
        val lines = csvText.lines().filter { it.isNotBlank() }
        val previews = mutableListOf<ParsedStudentPreview>()

        for (line in lines) {
            val parts = line.split(",").map { it.trim().removeSurrounding("\"") }
            if (parts.size >= 2) {
                val name = parts[0]
                val reg = parts[1]
                if (name.equals("Full Name", ignoreCase = true) || name.equals("Student Name", ignoreCase = true)) {
                    continue
                }
                val fac = if (parts.size > 2) parts[2] else "Faculty of Business Administration"
                val prog = if (parts.size > 3) parts[3] else "Bachelor of Business Administration"
                val cls = if (parts.size > 4) parts[4] else "BBA Year 2"
                val isDup = existingRegs.contains(reg.lowercase())

                previews.add(
                    ParsedStudentPreview(
                        fullName = name,
                        regNumber = reg,
                        faculty = fac,
                        programme = prog,
                        classLevel = cls,
                        isDuplicate = isDup
                    )
                )
            }
        }
        parsedPreview = previews
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EcuNavyDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EcuNavy),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "UPLOAD STUDENT LISTS (EXCEL / CSV)",
                        color = EcuGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Batch Import Student Enrollment Records",
                        color = White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Validate columns, detect duplicate registration numbers, and preview before saving to local encrypted SQLite.",
                        color = White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // CSV / Excel Input Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EcuNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CSV / Excel Data Input",
                            color = EcuGoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { csvText = sampleCsv },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EcuGoldLight),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.testTag("load_sample_csv_button")
                            ) {
                                Text("Load Sample Template", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { parseAndPreview() },
                                colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.testTag("validate_preview_button")
                            ) {
                                Text("Validate & Preview", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = csvText,
                        onValueChange = {
                            csvText = it
                            parsedPreview = emptyList()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("csv_input_field"),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = White
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EcuGold,
                            unfocusedBorderColor = EcuGold.copy(alpha = 0.4f),
                            focusedContainerColor = EcuNavyDark,
                            unfocusedContainerColor = EcuNavyDark
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Format: Full Name, Registration Number, Faculty, Programme, Class Level, Academic Year",
                        color = White.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Import Status Result Alert
        if (importResultMsg != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EcuGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcuGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcuGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = importResultMsg!!, color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Preview Table
        if (parsedPreview.isNotEmpty()) {
            val duplicateCount = parsedPreview.count { it.isDuplicate }
            val validCount = parsedPreview.size - duplicateCount

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PREVIEW UPLOADED RECORDS",
                            color = EcuGoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$validCount valid records to import • $duplicateCount duplicates detected",
                            color = if (duplicateCount > 0) EcuGold else EcuGreen,
                            fontSize = 11.5.sp
                        )
                    }

                    Button(
                        onClick = {
                            onImportCsv(csvText) { inserted, skipped ->
                                importResultMsg = "SUCCESS: Imported $inserted new students into database ($skipped duplicates skipped)."
                                parseAndPreview()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EcuGreen, contentColor = EcuNavyDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("confirm_import_button")
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save & Commit to Database", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            items(parsedPreview) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = EcuNavy),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (item.isDuplicate) EcuRed.copy(alpha = 0.5f) else EcuGreen.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = item.fullName, color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Reg No: ${item.regNumber} • ${item.classLevel}", color = EcuGoldLight, fontSize = 11.sp)
                            Text(text = "${item.faculty} • ${item.programme}", color = White.copy(alpha = 0.65f), fontSize = 10.sp)
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (item.isDuplicate) EcuRed.copy(alpha = 0.15f) else EcuGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(
                                0.5.dp,
                                if (item.isDuplicate) EcuRed else EcuGreen
                            )
                        ) {
                            Text(
                                text = if (item.isDuplicate) "DUPLICATE (Will Skip)" else "VALID",
                                color = if (item.isDuplicate) EcuRed else EcuGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
