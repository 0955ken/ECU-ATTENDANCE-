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
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.biometrics.ScannerConnectionState
import com.example.data.model.AuditLog
import com.example.ui.theme.EcuGold
import com.example.ui.theme.EcuGoldContainer
import com.example.ui.theme.EcuGoldLight
import com.example.ui.theme.EcuGreen
import com.example.ui.theme.EcuNavy
import com.example.ui.theme.EcuNavyDark
import com.example.ui.theme.EcuNavySurface
import com.example.ui.theme.EcuRed
import com.example.ui.theme.Slate200
import com.example.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    scannerState: ScannerConnectionState,
    auditLogs: List<AuditLog>,
    onToggleDiagnosticScanner: (Boolean) -> Unit,
    onRefreshHardwareScanner: () -> Unit,
    onChangePassword: (newPass: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    var passwordChangeMessage by remember { mutableStateOf<String?>(null) }
    val isConnected = scannerState is ScannerConnectionState.Connected
    val isDiagnostic = isConnected && !(scannerState as ScannerConnectionState.Connected).isPhysicalUsb

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EcuNavyDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EcuNavy),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "ADMINISTRATOR SYSTEM CONFIGURATION",
                        color = EcuGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "ECU DATA ATTENDANCE Settings",
                        color = White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Manage university identity, external USB biometric hardware adapters, password security, and system audit trail.",
                        color = White.copy(alpha = 0.7f),
                        fontSize = 11.5.sp
                    )
                }
            }
        }

        // University Metadata Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EcuNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "UNIVERSITY IDENTITY & ACADEMIC SESSION", color = EcuGoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    SettingRow(label = "Institution Name", value = "EMMANUEL CHRISTIAN UNIVERSITY (ECU)")
                    SettingRow(label = "Application Name", value = "ECU DATA ATTENDANCE")
                    SettingRow(label = "Official Tagline", value = "Smart Biometric Attendance Management System")
                    SettingRow(label = "Academic Year", value = "2026")
                    SettingRow(label = "Active Semester", value = "Semester 1")
                    SettingRow(label = "Working Days Policy", value = "Monday to Friday (5 Days Strict)")
                    SettingRow(label = "Attendance Formula", value = "(Days Present / Total Applicable Days) × 100")
                }
            }
        }

        // Hardware Scanner Configuration
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EcuNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "BIOMETRIC SCANNER HARDWARE (USB)", color = EcuGoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedButton(
                            onClick = onRefreshHardwareScanner,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EcuGoldLight),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Detect USB", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingRow(
                        label = "Hardware Model",
                        value = "SecuGen Hamster Pro 20 (USB Optical Biometric)"
                    )
                    SettingRow(
                        label = "Vendor ID / PID Support",
                        value = "0x1162 (SecuGen) • ISO/IEC 19794-2 standard"
                    )
                    SettingRow(
                        label = "Current Status",
                        value = if (isConnected) "SCANNER CONNECTED" else "SCANNER NOT CONNECTED"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Diagnostic mode switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "OTG Diagnostic & Hardware Test Mode", color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Enables virtual USB device emulation for tablet/emulator testing when no physical OTG cable is attached.",
                                color = White.copy(alpha = 0.65f),
                                fontSize = 11.sp
                            )
                        }

                        Switch(
                            checked = isDiagnostic,
                            onCheckedChange = { onToggleDiagnosticScanner(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = EcuNavyDark,
                                checkedTrackColor = EcuGold
                            ),
                            modifier = Modifier.testTag("diagnostic_mode_switch")
                        )
                    }
                }
            }
        }

        // Security & Administrator Password
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EcuNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "ADMINISTRATOR SECURITY & ACCESS", color = EcuGoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    SettingRow(label = "Account Level", value = "ADMINISTRATOR (Exclusive Authority)")
                    SettingRow(label = "Student Access", value = "Disabled (No student accounts allowed)")
                    SettingRow(label = "Password Hashing", value = "SHA-256 with University Salt")
                    SettingRow(label = "Local Storage", value = "Encrypted SQLite Room Database")

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showPasswordDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("change_password_button")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Change Admin Password", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    if (passwordChangeMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = passwordChangeMessage!!, color = EcuGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // System Audit Trail (Master prompt requirement 18)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ADMINISTRATOR AUDIT LOGS (${auditLogs.size})",
                    color = EcuGoldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tamper-evident system activity trail",
                    color = White.copy(alpha = 0.6f),
                    fontSize = 10.5.sp
                )
            }
        }

        items(auditLogs.take(25)) { log ->
            val timeStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(log.timestamp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = EcuNavy),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "[${log.action}] ${log.entityType} • ${log.entityId}",
                            color = EcuGoldLight,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = timeStr, color = White.copy(alpha = 0.5f), fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = log.details, color = White, fontSize = 11.sp)
                }
            }
        }
    }

    if (showPasswordDialog) {
        var newPass by remember { mutableStateOf("") }
        var confirmPass by remember { mutableStateOf("") }
        var passError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Change Administrator Password", color = EcuGoldLight, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (passError != null) {
                        Text(text = passError!!, color = EcuRed, fontSize = 11.sp)
                    }

                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("New Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it },
                        label = { Text("Confirm New Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPass.length < 6) {
                            passError = "Password must be at least 6 characters."
                            return@Button
                        }
                        if (newPass != confirmPass) {
                            passError = "Passwords do not match."
                            return@Button
                        }
                        onChangePassword(newPass, {
                            showPasswordDialog = false
                            passwordChangeMessage = "Administrator password updated successfully."
                        }, { err ->
                            passError = err
                        })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark)
                ) {
                    Text("Update Password")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("Cancel", color = White)
                }
            },
            containerColor = EcuNavy,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun SettingRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = White.copy(alpha = 0.7f), fontSize = 11.5.sp)
        Text(text = value, color = White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
    }
}
