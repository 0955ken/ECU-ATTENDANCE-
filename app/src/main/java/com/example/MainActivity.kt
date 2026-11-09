package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.Student
import com.example.ui.components.EcuHeader
import com.example.ui.components.EcuSidebar
import com.example.ui.screens.AdminLoginScreen
import com.example.ui.screens.AttendanceReportsScreen
import com.example.ui.screens.BackupRestoreScreen
import com.example.ui.screens.BiometricAttendanceScreen
import com.example.ui.screens.ClassManagementScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FacultyManagementScreen
import com.example.ui.screens.FingerprintRegistrationScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudentManagementScreen
import com.example.ui.screens.UploadStudentsScreen
import com.example.ui.screens.WeeklyRegisterScreen
import com.example.ui.theme.ECUTheme
import com.example.ui.theme.EcuNavyDark
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.EcuViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: EcuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ECUTheme(darkTheme = true) {
                EcuAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun EcuAppRoot(viewModel: EcuViewModel) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val currentAdmin by viewModel.currentAdmin.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val scannerState by viewModel.scannerState.collectAsState()

    // Data streams
    val students by viewModel.students.collectAsState()
    val totalStudentsCount by viewModel.totalStudentsCount.collectAsState()
    val activeStudentsCount by viewModel.activeStudentsCount.collectAsState()
    val faculties by viewModel.faculties.collectAsState()
    val programmes by viewModel.programmes.collectAsState()
    val classes by viewModel.classes.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val allTemplates by viewModel.allTemplates.collectAsState()
    val allAttendance by viewModel.allAttendance.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val syncRecords by viewModel.syncRecords.collectAsState()

    // Biometric scanning state
    val scanningClass by viewModel.scanningSelectedClass.collectAsState()
    val scanningYear by viewModel.scanningAcademicYear.collectAsState()
    val scanningSession by viewModel.scanningSession.collectAsState()
    val verificationUiState by viewModel.verificationUiState.collectAsState()

    // Enrollment state
    val enrollmentIsCapturing by viewModel.enrollmentIsCapturing.collectAsState()
    val enrollmentStatusMessage by viewModel.enrollmentStateMessage.collectAsState()

    // Backup state
    val backupSuccessMsg by viewModel.backupSuccessMessage.collectAsState()

    // Firebase Cloud Sync state
    val firebaseUser by viewModel.firebaseUser.collectAsState()
    val cloudBackups by viewModel.cloudBackups.collectAsState()
    val cloudSyncStatus by viewModel.cloudSyncStatus.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    var studentForDirectEnrollment by remember { mutableStateOf<Student?>(null) }

    if (!isAuthenticated) {
        AdminLoginScreen(
            onLoginSuccess = { username, pass -> viewModel.login(username, pass) },
            onRecoverPassword = { user, answer, newPass, onSuccess, onError ->
                viewModel.recoverPassword(user, answer, newPass, onSuccess, onError)
            },
            errorMessage = authError
        )
    } else {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        // BackHandler: Return to Dashboard if on a sub-screen
        BackHandler(enabled = currentScreen != AppScreen.DASHBOARD || drawerState.isOpen) {
            if (drawerState.isOpen) {
                scope.launch { drawerState.close() }
            } else {
                viewModel.navigateTo(AppScreen.DASHBOARD)
            }
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = EcuNavyDark,
                    drawerContentColor = Color.White
                ) {
                    EcuSidebar(
                        currentScreen = currentScreen,
                        onNavigate = { screen ->
                            viewModel.navigateTo(screen)
                            scope.launch { drawerState.close() }
                        },
                        onLogout = {
                            viewModel.logout()
                            scope.launch { drawerState.close() }
                        },
                        adminFullName = currentAdmin?.fullName ?: "Administrator"
                    )
                }
            }
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                topBar = {
                    EcuHeader(
                        scannerState = scannerState,
                        onMenuClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        },
                        onScannerClick = {
                            viewModel.navigateTo(AppScreen.SETTINGS)
                        },
                        onLogoutClick = {
                            viewModel.logout()
                        },
                        adminName = currentAdmin?.fullName ?: "Admin"
                    )
                },
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(EcuNavyDark)
                ) {
                    when (currentScreen) {
                        AppScreen.DASHBOARD -> {
                            DashboardScreen(
                                students = students,
                                totalStudentsCount = totalStudentsCount,
                                activeStudentsCount = activeStudentsCount,
                                attendanceList = allAttendance,
                                faculties = faculties,
                                programmes = programmes,
                                classes = classes,
                                onNavigate = { screen -> viewModel.navigateTo(screen) }
                            )
                        }

                        AppScreen.STUDENTS -> {
                            StudentManagementScreen(
                                students = students,
                                faculties = faculties,
                                classes = classes,
                                onAddOrUpdateStudent = { student, isEdit ->
                                    viewModel.addOrUpdateStudent(student, isEdit)
                                },
                                onDeleteStudent = { student ->
                                    viewModel.deleteStudent(student)
                                },
                                onNavigateToEnrollment = { student ->
                                    studentForDirectEnrollment = student
                                    viewModel.navigateTo(AppScreen.FINGERPRINT_ENROLLMENT)
                                },
                                onNavigateToUpload = {
                                    viewModel.navigateTo(AppScreen.UPLOAD_STUDENTS)
                                }
                            )
                        }

                        AppScreen.UPLOAD_STUDENTS -> {
                            UploadStudentsScreen(
                                existingStudents = students,
                                onImportCsv = { csvContent, onDone ->
                                    viewModel.importCsv(csvContent, onDone)
                                },
                                onNavigateBack = { viewModel.navigateTo(AppScreen.STUDENTS) }
                            )
                        }

                        AppScreen.FINGERPRINT_ENROLLMENT -> {
                            FingerprintRegistrationScreen(
                                students = students,
                                templates = allTemplates,
                                scannerState = scannerState,
                                initialSelectedStudent = studentForDirectEnrollment,
                                onEnroll = { student, finger, onDone ->
                                    viewModel.enrollFingerprintForStudent(student, finger, onDone)
                                },
                                onDeleteTemplate = { studentId, finger ->
                                    viewModel.deleteEnrolledFinger(studentId, finger)
                                },
                                isCapturing = enrollmentIsCapturing,
                                statusMessage = enrollmentStatusMessage
                            )
                        }

                        AppScreen.BIOMETRIC_ATTENDANCE -> {
                            BiometricAttendanceScreen(
                                scannerState = scannerState,
                                verificationUiState = verificationUiState,
                                students = students,
                                classes = classes,
                                sessions = sessions,
                                todayAttendanceRecords = allAttendance,
                                selectedClass = scanningClass,
                                onClassChange = { viewModel.scanningSelectedClass.value = it },
                                academicYear = scanningYear,
                                onYearChange = { viewModel.scanningAcademicYear.value = it },
                                selectedSession = scanningSession,
                                onSessionChange = { viewModel.scanningSession.value = it },
                                onScanFingerprint = { studentId ->
                                    viewModel.performBiometricScan(studentId)
                                },
                                onResetBanner = { viewModel.resetVerificationBanner() },
                                onEnableDiagnosticScanner = { enable ->
                                    viewModel.toggleDiagnosticScanner(enable)
                                }
                            )
                        }

                        AppScreen.WEEKLY_REGISTER -> {
                            WeeklyRegisterScreen(
                                students = students,
                                attendanceRecords = allAttendance,
                                faculties = faculties,
                                classes = classes,
                                onManualAttendanceUpdate = { sId, d, wd, s, status, r ->
                                    viewModel.updateAttendanceManual(sId, d, wd, s, status, r)
                                }
                            )
                        }

                        AppScreen.CLASS_MANAGEMENT -> {
                            ClassManagementScreen(
                                classes = classes,
                                programmes = programmes,
                                onAddClass = { name, progId, progCode, yr, sem, lvl ->
                                    viewModel.addClass(name, progId, progCode, yr, sem, lvl)
                                },
                                onDeleteClass = { cls ->
                                    viewModel.deleteClass(cls)
                                }
                            )
                        }

                        AppScreen.FACULTY_MANAGEMENT -> {
                            FacultyManagementScreen(
                                faculties = faculties,
                                onAddFaculty = { name, code, dean, desc ->
                                    viewModel.addFaculty(name, code, dean, desc)
                                },
                                onDeleteFaculty = { fac ->
                                    viewModel.deleteFaculty(fac)
                                }
                            )
                        }

                        AppScreen.ATTENDANCE_REPORTS -> {
                            AttendanceReportsScreen(
                                students = students,
                                attendanceRecords = allAttendance,
                                faculties = faculties,
                                classes = classes
                            )
                        }

                        AppScreen.BACKUP_RESTORE -> {
                            BackupRestoreScreen(
                                syncRecords = syncRecords,
                                onCreateBackup = { viewModel.createBackup() },
                                statusMessage = backupSuccessMsg,
                                firebaseUser = firebaseUser,
                                cloudBackups = cloudBackups,
                                cloudSyncStatus = cloudSyncStatus,
                                onSignInGoogle = {
                                    (context as? android.app.Activity)?.let { act ->
                                        viewModel.signInGoogle(act, {}, {})
                                    }
                                },
                                onSignOutGoogle = { viewModel.signOutGoogle() },
                                onSyncToCloud = { viewModel.syncToCloud() }
                            )
                        }

                        AppScreen.SETTINGS -> {
                            SettingsScreen(
                                scannerState = scannerState,
                                auditLogs = auditLogs,
                                onToggleDiagnosticScanner = { enable ->
                                    viewModel.toggleDiagnosticScanner(enable)
                                },
                                onRefreshHardwareScanner = {
                                    viewModel.refreshScannerDetection()
                                },
                                onChangePassword = { newPass, onSuccess, onError ->
                                    viewModel.changePassword(newPass, onSuccess, onError)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
