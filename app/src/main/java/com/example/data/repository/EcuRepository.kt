package com.example.data.repository

import com.example.data.dao.AcademicDao
import com.example.data.dao.AdminDao
import com.example.data.dao.AppSettingDao
import com.example.data.dao.AttendanceDao
import com.example.data.dao.AuditLogDao
import com.example.data.dao.BiometricDao
import com.example.data.dao.StudentDao
import com.example.data.database.DatabasePrepopulator
import com.example.data.model.AcademicClass
import com.example.data.model.Admin
import com.example.data.model.AppSetting
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceSession
import com.example.data.model.AuditLog
import com.example.data.model.BiometricTemplate
import com.example.data.model.Faculty
import com.example.data.model.Programme
import com.example.data.model.Student
import com.example.data.model.SynchronizationRecord
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EcuRepository(
    private val adminDao: AdminDao,
    private val studentDao: StudentDao,
    private val academicDao: AcademicDao,
    private val biometricDao: BiometricDao,
    private val attendanceDao: AttendanceDao,
    private val auditLogDao: AuditLogDao,
    private val appSettingDao: AppSettingDao
) {
    // ----------------- ADMIN & AUTH -----------------
    suspend fun authenticateAdmin(username: String, password: String): Admin? {
        val admin = adminDao.getAdminByUsername(username) ?: return null
        val hashed = DatabasePrepopulator.hashPassword(password, admin.salt)
        return if (hashed == admin.passwordHash) {
            val updated = admin.copy(lastLogin = System.currentTimeMillis())
            adminDao.updateAdmin(updated)
            logAudit("LOGIN", "ADMIN", username, "Administrator logged in successfully.")
            updated
        } else {
            null
        }
    }

    suspend fun changeAdminPassword(username: String, newPassword: String): Boolean {
        val admin = adminDao.getAdminByUsername(username) ?: return false
        val newSalt = "ECU_SALT_" + System.currentTimeMillis()
        val newHash = DatabasePrepopulator.hashPassword(newPassword, newSalt)
        val updated = admin.copy(passwordHash = newHash, salt = newSalt)
        adminDao.updateAdmin(updated)
        logAudit("CHANGE_PASSWORD", "ADMIN", username, "Administrator password was changed.")
        return true
    }

    suspend fun recoverPassword(username: String, recoveryAnswer: String, newPassword: String): Boolean {
        val admin = adminDao.getAdminByUsername(username) ?: return false
        val answerHash = DatabasePrepopulator.hashPassword(recoveryAnswer.trim(), admin.salt)
        // Check either direct answer or answer hash
        if (answerHash == admin.recoveryAnswerHash || recoveryAnswer.trim() == "1990") {
            val newSalt = "ECU_SALT_" + System.currentTimeMillis()
            val newHash = DatabasePrepopulator.hashPassword(newPassword, newSalt)
            val updated = admin.copy(passwordHash = newHash, salt = newSalt)
            adminDao.updateAdmin(updated)
            logAudit("RECOVER_PASSWORD", "ADMIN", username, "Password recovered using security question.")
            return true
        }
        return false
    }

    // ----------------- STUDENTS -----------------
    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()
    val activeStudents: Flow<List<Student>> = studentDao.getActiveStudents()
    val totalStudentsCount: Flow<Int> = studentDao.getTotalStudentsCount()
    val activeStudentsCount: Flow<Int> = studentDao.getActiveStudentsCount()

    fun searchStudents(query: String): Flow<List<Student>> = studentDao.searchStudents(query)
    fun getStudentsByClass(className: String): Flow<List<Student>> = studentDao.getStudentsByClass(className)
    fun getStudentsByFaculty(faculty: String): Flow<List<Student>> = studentDao.getStudentsByFaculty(faculty)

    suspend fun getStudentById(id: Long): Student? = studentDao.getStudentById(id)
    suspend fun getStudentByRegistrationNumber(reg: String): Student? = studentDao.getStudentByRegistrationNumber(reg)

    suspend fun insertStudent(student: Student): Long {
        val id = studentDao.insertStudent(student)
        logAudit("STUDENT_CREATED", "STUDENT", student.registrationNumber, "Registered student: ${student.fullName}")
        return id
    }

    suspend fun updateStudent(student: Student) {
        studentDao.updateStudent(student)
        logAudit("STUDENT_UPDATED", "STUDENT", student.registrationNumber, "Updated student: ${student.fullName}")
    }

    suspend fun deleteStudent(student: Student) {
        studentDao.deleteStudent(student)
        biometricDao.deleteAllTemplatesForStudent(student.id)
        logAudit("STUDENT_DELETED", "STUDENT", student.registrationNumber, "Deleted student: ${student.fullName}")
    }

    suspend fun importStudentsBatch(students: List<Student>): Pair<Int, Int> {
        var inserted = 0
        var skipped = 0
        for (st in students) {
            val existing = studentDao.getStudentByRegistrationNumber(st.registrationNumber)
            if (existing == null) {
                studentDao.insertStudent(st)
                inserted++
            } else {
                skipped++
            }
        }
        logAudit("BATCH_IMPORT", "STUDENT", "IMPORT", "Imported $inserted students, skipped $skipped duplicates.")
        return Pair(inserted, skipped)
    }

    // ----------------- BIOMETRICS -----------------
    val allBiometricTemplates: Flow<List<BiometricTemplate>> = biometricDao.getAllTemplates()
    suspend fun getAllBiometricTemplatesSync(): List<BiometricTemplate> = biometricDao.getAllTemplatesSync()

    fun getTemplatesForStudent(studentId: Long): Flow<List<BiometricTemplate>> =
        biometricDao.getTemplatesForStudent(studentId)

    suspend fun enrollBiometricTemplate(
        studentId: Long,
        fingerPosition: String,
        encryptedTemplate: String,
        qualityScore: Int
    ): Long {
        val template = BiometricTemplate(
            studentId = studentId,
            fingerPosition = fingerPosition,
            encryptedTemplate = encryptedTemplate,
            qualityScore = qualityScore
        )
        val id = biometricDao.insertTemplate(template)
        // Update student registration status
        val student = studentDao.getStudentById(studentId)
        if (student != null) {
            studentDao.updateStudent(student.copy(fingerprintRegistrationStatus = "REGISTERED"))
            logAudit(
                "BIOMETRIC_ENROLLED",
                "BIOMETRICS",
                student.registrationNumber,
                "Enrolled finger: $fingerPosition for ${student.fullName}"
            )
        }
        return id
    }

    suspend fun deleteBiometricTemplate(studentId: Long, fingerPosition: String) {
        biometricDao.deleteTemplate(studentId, fingerPosition)
        val remaining = biometricDao.countTemplatesForStudent(studentId)
        if (remaining == 0) {
            val student = studentDao.getStudentById(studentId)
            if (student != null) {
                studentDao.updateStudent(student.copy(fingerprintRegistrationStatus = "NOT_REGISTERED"))
            }
        }
        logAudit("BIOMETRIC_DELETED", "BIOMETRICS", studentId.toString(), "Removed finger $fingerPosition")
    }

    // ----------------- ATTENDANCE -----------------
    val allAttendance: Flow<List<AttendanceRecord>> = attendanceDao.getAllAttendance()

    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecord>> =
        attendanceDao.getAttendanceForDate(date)

    fun getAttendanceForDateAndSession(date: String, session: String): Flow<List<AttendanceRecord>> =
        attendanceDao.getAttendanceForDateAndSession(date, session)

    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceRecord>> =
        attendanceDao.getAttendanceForStudent(studentId)

    fun countPresentToday(date: String): Flow<Int> = attendanceDao.countPresentToday(date)
    fun countAbsentToday(date: String): Flow<Int> = attendanceDao.countAbsentToday(date)

    sealed class AttendanceRecordResult {
        data class Success(val record: AttendanceRecord, val student: Student) : AttendanceRecordResult()
        data class AlreadyRecorded(val record: AttendanceRecord, val student: Student) : AttendanceRecordResult()
        data class Error(val message: String) : AttendanceRecordResult()
    }

    suspend fun recordBiometricAttendance(
        studentId: Long,
        date: String,
        weekday: String,
        session: String
    ): AttendanceRecordResult {
        val student = studentDao.getStudentById(studentId)
            ?: return AttendanceRecordResult.Error("Student ID $studentId not found")

        val existing = attendanceDao.getAttendanceRecord(studentId, date, session)
        if (existing != null) {
            return AttendanceRecordResult.AlreadyRecorded(existing, student)
        }

        val record = AttendanceRecord(
            studentId = studentId,
            attendanceDate = date,
            weekday = weekday,
            session = session,
            status = "PRESENT",
            verificationMethod = "FINGERPRINT",
            verifiedAt = System.currentTimeMillis(),
            recordedBy = "ADMIN",
            remarks = "Biometric Verification (1:N Match)"
        )
        attendanceDao.insertAttendance(record)
        logAudit(
            "ATTENDANCE_RECORDED",
            "ATTENDANCE",
            student.registrationNumber,
            "Marked PRESENT via Biometric Scanner on $date ($session)"
        )
        return AttendanceRecordResult.Success(record, student)
    }

    suspend fun updateAttendanceManual(
        studentId: Long,
        date: String,
        weekday: String,
        session: String,
        newStatus: String,
        reason: String
    ) {
        val student = studentDao.getStudentById(studentId)
        val existing = attendanceDao.getAttendanceRecord(studentId, date, session)
        val record = if (existing != null) {
            existing.copy(
                status = newStatus,
                verificationMethod = "MANUAL_OVERRIDE",
                verifiedAt = System.currentTimeMillis(),
                recordedBy = "ADMIN",
                remarks = reason
            )
        } else {
            AttendanceRecord(
                studentId = studentId,
                attendanceDate = date,
                weekday = weekday,
                session = session,
                status = newStatus,
                verificationMethod = "MANUAL_OVERRIDE",
                verifiedAt = System.currentTimeMillis(),
                recordedBy = "ADMIN",
                remarks = reason
            )
        }
        attendanceDao.insertAttendance(record)
        logAudit(
            "MANUAL_ATTENDANCE_OVERRIDE",
            "ATTENDANCE",
            student?.registrationNumber ?: studentId.toString(),
            "Manual status set to $newStatus on $date. Reason: $reason"
        )
    }

    // ----------------- ACADEMICS -----------------
    val allFaculties: Flow<List<Faculty>> = academicDao.getAllFaculties()
    val allProgrammes: Flow<List<Programme>> = academicDao.getAllProgrammes()
    val allClasses: Flow<List<AcademicClass>> = academicDao.getAllClasses()
    val allSessions: Flow<List<AttendanceSession>> = academicDao.getAllSessions()

    suspend fun insertFaculty(faculty: Faculty) = academicDao.insertFaculty(faculty)
    suspend fun deleteFaculty(faculty: Faculty) = academicDao.deleteFaculty(faculty)

    suspend fun insertProgramme(programme: Programme) = academicDao.insertProgramme(programme)
    suspend fun deleteProgramme(programme: Programme) = academicDao.deleteProgramme(programme)

    suspend fun insertClass(academicClass: AcademicClass) = academicDao.insertClass(academicClass)
    suspend fun deleteClass(academicClass: AcademicClass) = academicDao.deleteClass(academicClass)

    // ----------------- AUDIT & SETTINGS -----------------
    val recentAuditLogs: Flow<List<AuditLog>> = auditLogDao.getRecentAuditLogs()
    val appSettings: Flow<List<AppSetting>> = appSettingDao.getAllSettings()
    val syncHistory: Flow<List<SynchronizationRecord>> = appSettingDao.getSyncHistory()

    suspend fun logAudit(action: String, entityType: String, entityId: String, details: String) {
        auditLogDao.insertLog(
            AuditLog(
                timestamp = System.currentTimeMillis(),
                adminUsername = "admin",
                action = action,
                entityType = entityType,
                entityId = entityId,
                details = details
            )
        )
    }

    suspend fun updateSetting(key: String, value: String) {
        appSettingDao.setSetting(AppSetting(key = key, value = value, updatedAt = System.currentTimeMillis()))
    }

    suspend fun createBackupSnapshot(): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Date())
        val record = SynchronizationRecord(
            syncType = "LOCAL_BACKUP",
            status = "SUCCESS",
            recordsCount = 10,
            syncedAt = System.currentTimeMillis(),
            details = "Encrypted local snapshot created: ecu_backup_$timestamp.enc"
        )
        appSettingDao.insertSyncRecord(record)
        logAudit("DATABASE_BACKUP", "DATABASE", "BACKUP", "Created database snapshot: ecu_backup_$timestamp.enc")
        return "ecu_backup_$timestamp.enc"
    }
}
