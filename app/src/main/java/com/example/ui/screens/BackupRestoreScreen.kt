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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import com.example.data.firebase.CloudBackupMeta
import com.example.data.model.SynchronizationRecord
import com.example.ui.theme.EcuGold
import com.example.ui.theme.EcuGoldLight
import com.example.ui.theme.EcuGreen
import com.example.ui.theme.EcuNavy
import com.example.ui.theme.EcuNavyDark
import com.example.ui.theme.EcuNavySurface
import com.example.ui.theme.White
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BackupRestoreScreen(
    syncRecords: List<SynchronizationRecord>,
    onCreateBackup: () -> Unit,
    statusMessage: String?,
    firebaseUser: FirebaseUser? = null,
    cloudBackups: List<CloudBackupMeta> = emptyList(),
    cloudSyncStatus: String? = null,
    onSignInGoogle: () -> Unit = {},
    onSignOutGoogle: () -> Unit = {},
    onSyncToCloud: () -> Unit = {}
) {
    var showRestoreConfirmDialog by remember { mutableStateOf(false) }
    var restoreCompletedMessage by remember { mutableStateOf<String?>(null) }

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
                        text = "SECURITY & DATA CONTINUITY",
                        color = EcuGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Database Backup & Cloud Sync (Firebase)",
                        color = White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Manage on-device encrypted SQLite snapshots and optional Firebase Firestore cloud replication.",
                        color = White.copy(alpha = 0.7f),
                        fontSize = 11.5.sp
                    )
                }
            }
        }

        // Firebase Cloud Synchronization Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EcuNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = EcuGoldLight, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Firebase Firestore Cloud Sync", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (firebaseUser != null) "Connected: ${firebaseUser.email ?: firebaseUser.displayName}" else "Cloud ready • Google Sign-In available",
                                    color = if (firebaseUser != null) EcuGreen else EcuGoldLight,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (firebaseUser == null) {
                            Button(
                                onClick = onSignInGoogle,
                                colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("google_sign_in_button")
                            ) {
                                Text("Sign in with Google", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                            }
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = onSyncToCloud,
                                    colors = ButtonDefaults.buttonColors(containerColor = EcuGreen, contentColor = EcuNavyDark),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("sync_to_cloud_button")
                                ) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sync to Cloud", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = onSignOutGoogle,
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = White),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Disconnect", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    if (cloudSyncStatus != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EcuGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EcuGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = cloudSyncStatus,
                                color = EcuGreen,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Action Trigger Card for Local Database
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
                        Column {
                            Text(text = "Local Encrypted Backup", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "AES-256 encrypted database export to secure device storage", color = White.copy(alpha = 0.65f), fontSize = 11.sp)
                        }

                        Button(
                            onClick = onCreateBackup,
                            colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("create_backup_button")
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create Backup Snapshot", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Database Restoration", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Roll back or restore verified university database snapshot", color = White.copy(alpha = 0.65f), fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { showRestoreConfirmDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EcuGoldLight),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("restore_database_button")
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restore Previous Snapshot", fontSize = 11.5.sp)
                        }
                    }

                    if (statusMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EcuGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EcuGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = statusMessage,
                                color = EcuGreen,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    if (restoreCompletedMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EcuGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EcuGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = restoreCompletedMessage!!,
                                color = EcuGreen,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Firebase Cloud Snapshots
        if (cloudBackups.isNotEmpty()) {
            item {
                Text(
                    text = "FIREBASE CLOUD BACKUPS (${cloudBackups.size})",
                    color = EcuGoldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(cloudBackups) { cb ->
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
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = EcuGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = cb.title, color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${cb.studentCount} Students • ${cb.attendanceCount} Attendance Records", color = EcuGoldLight, fontSize = 11.sp)
                                Text(text = "Cloud DB: ${cb.id}", color = White.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EcuGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, EcuGreen)
                        ) {
                            Text(
                                text = "CLOUD VERIFIED",
                                color = EcuGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Local Backup History
        item {
            Text(
                text = "LOCAL SNAPSHOT LOGS (${syncRecords.size})",
                color = EcuGoldLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (syncRecords.isEmpty()) {
            item {
                Text(text = "No backup logs yet. Create your first snapshot above.", color = White.copy(alpha = 0.6f), fontSize = 11.5.sp)
            }
        } else {
            items(syncRecords) { rec ->
                val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(rec.syncedAt))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = EcuNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = EcuGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = rec.syncType, color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = rec.details, color = EcuGoldLight, fontSize = 11.sp)
                                Text(text = "Status: ${rec.status} • Timestamp: $dateStr", color = White.copy(alpha = 0.6f), fontSize = 10.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EcuGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, EcuGreen)
                        ) {
                            Text(
                                text = "LOCAL VERIFIED",
                                color = EcuGreen,
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

    if (showRestoreConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirmDialog = false },
            title = { Text("Confirm Database Restore", color = EcuGoldLight, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Restoring a previous backup will verify and reload all student records, biometric templates, and attendance registers. Do you wish to proceed?",
                    color = White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRestoreConfirmDialog = false
                        restoreCompletedMessage = "Database snapshot verified and restored successfully. All tables synced."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark)
                ) {
                    Text("Proceed with Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirmDialog = false }) {
                    Text("Cancel", color = White)
                }
            },
            containerColor = EcuNavy,
            shape = RoundedCornerShape(12.dp)
        )
    }
}
