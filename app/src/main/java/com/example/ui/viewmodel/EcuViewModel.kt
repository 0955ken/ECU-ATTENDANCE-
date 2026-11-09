package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.biometrics.BiometricMatchResult
import com.example.biometrics.ScannerConnectionState
import com.example.biometrics.UsbBiometricScannerManager
import com.example.data.database.EcuDatabase
import com.example.data.model.AcademicClass
import com.example.data.model.Admin
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceSession
import com.example.data.model.AuditLog
import com.example.data.model.BiometricTemplate
import com.example.data.model.Faculty
import com.example.data.model.Programme
import com.example.data.model.Student
import com.example.data.model.SynchronizationRecord
import com.example.data.repository.EcuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppScreen(val title: String, val subtitle: String) {
    DASHBOARD("Dashboard", "ECU Attendance Overview & Analytics"),
    STUDENTS("Student Management", "Manage Enrolled University Students"),
    UPLOAD_STUDENTS("Upload Student Lists", "Import Students from Excel & CSV"),
    FINGERPRINT_ENROLLMENT("Fingerprint Registration", "Biometric Template Enrollment"),
    BIOMETRIC_ATTENDANCE("Biometric Attendance", "Live Biometric Identification (1:N)"),
    WEEKLY_REGISTER("Weekly Attendance Register", "Monday to Friday Register"),
    CLASS_MANAGEMENT("Class Management", "Academic Classes & Cohorts"),
    FACULTY_MANAGEMENT("Faculty Management", "Faculties & Degree Programmes"),
    ATTENDANCE_REPORTS("Attendance Reports", "Generate Official Attendance Reports"),
    BACKUP_RESTORE("Backup & Restore", "Encrypted Database Snapshots"),
    SETTINGS("Settings", "System Configuration & Administrator Tools")
}

data class VerificationUiState(
    val status: VerificationStatus = VerificationStatus.IDLE,
    val student: Student? = null,
    val record: AttendanceRecord? = null,
    val message: String = "",
    val confidence: Double = 0.0,
    val fingerPosition: String = "",
    val timestamp: Long = 0
)

enum class VerificationStatus {
    IDLE,
    SCANNING,
    SUCCESS,
    ALREADY_RECORDED,
    NOT_RECOGNIZED,
    ERROR
}

class EcuViewModel(application: Application) : AndroidViewModel(application) {

    private val database = EcuDatabase.getDatabase(application, viewModelScope)
    val repository = EcuRepository(
        adminDao = database.adminDao(),
        studentDao = database.studentDao(),
        academicDao = database.academicDao(),
        biometricDao = database.biometricDao(),
        attendanceDao = database.attendanceDao(),
        auditLogDao = database.auditLogDao(),
        appSettingDao = database.appSettingDao()
    )

    val scannerManager = UsbBiometricScannerManager(application)
    val firebaseSyncManager = com.example.data.firebase.FirebaseCloudSyncManager(application)

    val firebaseUser = firebaseSyncManager.currentUser
    val cloudBackups = firebaseSyncManager.cloudBackups
    val cloudSyncStatus = firebaseSyncManager.syncStatus

    fun syncToCloud() {
        viewModelScope.launch {
            val studentList = students.value
            val attendanceList = allAttendance.value
            firebaseSyncManager.syncAttendanceToCloud(studentList, attendanceList)
        }
    }

    fun signInGoogle(activity: android.app.Activity, onSuccess: () -> Unit, onError: (String) -> Unit) {
        firebaseSyncManager.signInWithGoogle(activity, viewModelScope, { onSuccess() }, onError)
    }

    fun signOutGoogle() {
        firebaseSyncManager.signOut(viewModelScope) {}
    }

    // ----------------- NAVIGATION -----------------
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // ----------------- AUTHENTICATION -----------------
    private val _isAuthenticated = MutableStateFlow(true) // Start authenticated with default university admin
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentAdmin = MutableStateFlow<Admin?>(
        Admin(
            id = 1,
            username = "admin",
            passwordHash = "",
            salt = "",
            fullName = "System Administrator (Dr. ECU Registrar)"
        )
    )
    val currentAdmin: StateFlow<Admin?> = _currentAdmin.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    fun login(username: String, pass: String) {
        viewModelScope.launch {
            _authError.value = null
            val admin = repository.authenticateAdmin(username, pass)
            if (admin != null) {
                _currentAdmin.value = admin
                _isAuthenticated.value = true
                _currentScreen.value = AppScreen.DASHBOARD
            } else {
                _authError.value = "Invalid username or password. Please try again."
            }
        }
    }

    fun logout() {
        _isAuthenticated.value = false
        _currentScreen.value = AppScreen.DASHBOARD
    }

    fun recoverPassword(username: String, answer: String, newPass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val success = repository.recoverPassword(username, answer, newPass)
            if (success) {
                onSuccess()
            } else {
                onError("Security recovery failed. Incorrect recovery answer.")
            }
        }
    }

    fun changePassword(newPass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val user = _currentAdmin.value?.username ?: "admin"
            val success = repository.changeAdminPassword(user, newPass)
            if (success) {
                onSuccess()
            } else {
                onError("Could not change password.")
            }
        }
    }

    // ----------------- SCANNER HARDWARE -----------------
    val scannerState: StateFlow<ScannerConnectionState> = scannerManager.connectionState

    fun toggleDiagnosticScanner(enable: Boolean) {
        scannerManager.enableDiagnosticScanner(enable)
    }

    fun refreshScannerDetection() {
        scannerManager.detectAttachedUsbScanner()
    }

    // ----------------- STUDENTS & DATA -----------------
    val students: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalStudentsCount: StateFlow<Int> = repository.totalStudentsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeStudentsCount: StateFlow<Int> = repository.activeStudentsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val faculties: StateFlow<List<Faculty>> = repository.allFaculties
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val programmes: StateFlow<List<Programme>> = repository.allProgrammes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val classes: StateFlow<List<AcademicClass>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<AttendanceSession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTemplates: StateFlow<List<BiometricTemplate>> = repository.allBiometricTemplates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttendance: StateFlow<List<AttendanceRecord>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLog>> = repository.recentAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val syncRecords: StateFlow<List<SynchronizationRecord>> = repository.syncHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ----------------- BIOMETRIC ATTENDANCE SCANNING -----------------
    val scanningSelectedClass = MutableStateFlow("All Classes")
    val scanningAcademicYear = MutableStateFlow("2026")
    val scanningSession = MutableStateFlow("Morning Session")

    private val _verificationUiState = MutableStateFlow(VerificationUiState())
    val verificationUiState: StateFlow<VerificationUiState> = _verificationUiState.asStateFlow()

    fun performBiometricScan(targetStudentIdForSimulatedTap: Long? = null) {
        viewModelScope.launch {
            if (scannerState.value is ScannerConnectionState.Disconnected) {
                _verificationUiState.value = VerificationUiState(
                    status = VerificationStatus.ERROR,
                    message = "SCANNER NOT CONNECTED. Please connect SecuGen USB Fingerprint Scanner or enable Diagnostic Mode."
                )
                return@launch
            }

            _verificationUiState.value = VerificationUiState(
                status = VerificationStatus.SCANNING,
                message = "Capturing fingerprint from scanner..."
            )

            val captureResult = scannerManager.captureFingerprint()
            val candidateTemplates = repository.getAllBiometricTemplatesSync()

            val matchResult = scannerManager.identifyStudent1toN(
                capturedTemplate = captureResult.templateString,
                candidateTemplates = candidateTemplates,
                exactMatchStudentId = targetStudentIdForSimulatedTap
            )

            when (matchResult) {
                is BiometricMatchResult.Match -> {
                    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                    val currentWeekday = SimpleDateFormat("EEEE", Locale.US).format(Date())
                    val sessionName = scanningSession.value

                    val recordResult = repository.recordBiometricAttendance(
                        studentId = matchResult.studentId,
                        date = todayDate,
                        weekday = currentWeekday,
                        session = sessionName
                    )

                    when (recordResult) {
                        is EcuRepository.AttendanceRecordResult.Success -> {
                            _verificationUiState.value = VerificationUiState(
                                status = VerificationStatus.SUCCESS,
                                student = recordResult.student,
                                record = recordResult.record,
                                message = "ATTENDANCE SUCCESSFULLY RECORDED",
                                confidence = matchResult.confidenceScore,
                                fingerPosition = matchResult.fingerPosition,
                                timestamp = System.currentTimeMillis()
                            )
                        }
                        is EcuRepository.AttendanceRecordResult.AlreadyRecorded -> {
                            _verificationUiState.value = VerificationUiState(
                                status = VerificationStatus.ALREADY_RECORDED,
                                student = recordResult.student,
                                record = recordResult.record,
                                message = "ATTENDANCE ALREADY RECORDED",
                                confidence = matchResult.confidenceScore,
                                fingerPosition = matchResult.fingerPosition,
                                timestamp = System.currentTimeMillis()
                            )
                        }
                        is EcuRepository.AttendanceRecordResult.Error -> {
                            _verificationUiState.value = VerificationUiState(
                                status = VerificationStatus.ERROR,
                                message = recordResult.message
                            )
                        }
                    }
                }
                is BiometricMatchResult.NoMatch -> {
                    _verificationUiState.value = VerificationUiState(
                        status = VerificationStatus.NOT_RECOGNIZED,
                        message = "FINGERPRINT NOT RECOGNIZED. PLEASE TRY AGAIN."
                    )
                }
                is BiometricMatchResult.LowQuality -> {
                    _verificationUiState.value = VerificationUiState(
                        status = VerificationStatus.ERROR,
                        message = "Fingerprint quality too low (${matchResult.score}%). Please clean sensor and try again."
                    )
                }
                is BiometricMatchResult.Error -> {
                    _verificationUiState.value = VerificationUiState(
                        status = VerificationStatus.ERROR,
                        message = matchResult.message
                    )
                }
            }
        }
    }

    fun resetVerificationBanner() {
        _verificationUiState.value = VerificationUiState(status = VerificationStatus.IDLE)
    }

    // ----------------- FINGERPRINT ENROLLMENT -----------------
    val enrollmentSelectedStudent = MutableStateFlow<Student?>(null)
    val enrollmentFingerPosition = MutableStateFlow("Right Thumb")
    val enrollmentStateMessage = MutableStateFlow<String?>(null)
    val enrollmentIsCapturing = MutableStateFlow(false)

    fun enrollFingerprintForStudent(student: Student, finger: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (scannerState.value is ScannerConnectionState.Disconnected) {
                enrollmentStateMessage.value = "SCANNER NOT CONNECTED. Plug in USB scanner or enable Diagnostic Mode."
                onDone(false)
                return@launch
            }

            enrollmentIsCapturing.value = true
            enrollmentStateMessage.value = "Capturing $finger for ${student.fullName}..."
            val result = scannerManager.captureFingerprint(student.registrationNumber, finger)

            val templateId = repository.enrollBiometricTemplate(
                studentId = student.id,
                fingerPosition = finger,
                encryptedTemplate = result.templateString,
                qualityScore = result.qualityScore
            )

            enrollmentIsCapturing.value = false
            enrollmentStateMessage.value = "Successfully enrolled $finger (Quality: ${result.qualityScore}%). Template ID #$templateId saved."
            onDone(true)
        }
    }

    fun deleteEnrolledFinger(studentId: Long, finger: String) {
        viewModelScope.launch {
            repository.deleteBiometricTemplate(studentId, finger)
        }
    }

    // ----------------- WEEKLY REGISTER -----------------
    val weeklySelectedMonday = MutableStateFlow("2026-11-02")
    val weeklySelectedClass = MutableStateFlow("All")

    fun updateAttendanceManual(
        studentId: Long,
        date: String,
        weekday: String,
        session: String,
        newStatus: String,
        reason: String
    ) {
        viewModelScope.launch {
            repository.updateAttendanceManual(
                studentId = studentId,
                date = date,
                weekday = weekday,
                session = session,
                newStatus = newStatus,
                reason = reason
            )
        }
    }

    // ----------------- STUDENT CRUD -----------------
    fun addOrUpdateStudent(student: Student, isEdit: Boolean) {
        viewModelScope.launch {
            if (isEdit) {
                repository.updateStudent(student)
            } else {
                repository.insertStudent(student)
            }
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.deleteStudent(student)
        }
    }

    fun importCsv(csvContent: String, onComplete: (Int, Int) -> Unit) {
        viewModelScope.launch {
            val lines = csvContent.lines().filter { it.isNotBlank() }
            val importedStudents = mutableListOf<Student>()
            for (line in lines) {
                val parts = line.split(",").map { it.trim().removeSurrounding("\"") }
                if (parts.size >= 3) {
                    val name = parts[0]
                    val reg = parts[1]
                    if (name.equals("Student Name", ignoreCase = true) || name.equals("Full Name", ignoreCase = true)) {
                        continue // Skip CSV header
                    }
                    val faculty = if (parts.size > 2 && parts[2].isNotBlank()) parts[2] else "Faculty of Business Administration"
                    val programme = if (parts.size > 3 && parts[3].isNotBlank()) parts[3] else "Bachelor of Business Administration"
                    val classLevel = if (parts.size > 4 && parts[4].isNotBlank()) parts[4] else "BBA Year 2"
                    val academicYear = if (parts.size > 5 && parts[5].isNotBlank()) parts[5] else "2026"

                    importedStudents.add(
                        Student(
                            fullName = name,
                            registrationNumber = reg,
                            faculty = faculty,
                            department = "General Department",
                            programme = programme,
                            academicYear = academicYear,
                            classLevel = classLevel,
                            studentStatus = "ACTIVE",
                            fingerprintRegistrationStatus = "NOT_REGISTERED",
                            studentIdentificationNumber = "ECU-$reg"
                        )
                    )
                }
            }

            val (inserted, skipped) = repository.importStudentsBatch(importedStudents)
            onComplete(inserted, skipped)
        }
    }

    // ----------------- BACKUP -----------------
    val backupSuccessMessage = MutableStateFlow<String?>(null)

    fun createBackup() {
        viewModelScope.launch {
            val filename = repository.createBackupSnapshot()
            backupSuccessMessage.value = "Database backup created successfully: $filename"
        }
    }

    // ----------------- ACADEMICS -----------------
    fun addFaculty(name: String, code: String, dean: String, desc: String) {
        viewModelScope.launch {
            repository.insertFaculty(Faculty(facultyName = name, code = code, deanName = dean, description = desc))
        }
    }

    fun deleteFaculty(faculty: Faculty) {
        viewModelScope.launch {
            repository.deleteFaculty(faculty)
        }
    }

    fun addClass(name: String, progId: Long, progCode: String, year: String, sem: String, level: String) {
        viewModelScope.launch {
            repository.insertClass(
                AcademicClass(
                    className = name,
                    programmeId = progId,
                    programmeCode = progCode,
                    academicYear = year,
                    semester = sem,
                    level = level
                )
            )
        }
    }

    fun deleteClass(academicClass: AcademicClass) {
        viewModelScope.launch {
            repository.deleteClass(academicClass)
        }
    }
}
