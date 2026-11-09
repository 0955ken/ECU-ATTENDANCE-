package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "admins")
data class Admin(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password_hash") val passwordHash: String,
    @ColumnInfo(name = "salt") val salt: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "recovery_question") val recoveryQuestion: String = "University Founding Year",
    @ColumnInfo(name = "recovery_answer_hash") val recoveryAnswerHash: String = "1990",
    @ColumnInfo(name = "last_login") val lastLogin: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "students",
    indices = [Index(value = ["registration_number"], unique = true)]
)
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "registration_number") val registrationNumber: String,
    @ColumnInfo(name = "faculty") val faculty: String,
    @ColumnInfo(name = "department") val department: String,
    @ColumnInfo(name = "programme") val programme: String,
    @ColumnInfo(name = "academic_year") val academicYear: String,
    @ColumnInfo(name = "class_level") val classLevel: String,
    @ColumnInfo(name = "student_status") val studentStatus: String = "ACTIVE", // ACTIVE, SUSPENDED, GRADUATED
    @ColumnInfo(name = "fingerprint_registration_status") val fingerprintRegistrationStatus: String = "NOT_REGISTERED", // REGISTERED, NOT_REGISTERED
    @ColumnInfo(name = "student_identification_number") val studentIdentificationNumber: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "faculties")
data class Faculty(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "faculty_name") val facultyName: String,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "dean_name") val deanName: String = "",
    @ColumnInfo(name = "description") val description: String = ""
)

@Entity(tableName = "programmes")
data class Programme(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "programme_name") val programmeName: String,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "faculty_id") val facultyId: Long,
    @ColumnInfo(name = "faculty_name") val facultyName: String,
    @ColumnInfo(name = "duration_years") val durationYears: Int = 4
)

@Entity(tableName = "academic_classes")
data class AcademicClass(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "class_name") val className: String, // e.g. "BBA Year 2"
    @ColumnInfo(name = "programme_id") val programmeId: Long,
    @ColumnInfo(name = "programme_code") val programmeCode: String,
    @ColumnInfo(name = "academic_year") val academicYear: String, // e.g. "2026"
    @ColumnInfo(name = "semester") val semester: String = "Semester 1",
    @ColumnInfo(name = "level") val level: String = "Year 2"
)

@Entity(
    tableName = "biometric_templates",
    indices = [Index(value = ["student_id", "finger_position"], unique = true)]
)
data class BiometricTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "student_id") val studentId: Long,
    @ColumnInfo(name = "finger_position") val fingerPosition: String, // "Right Thumb", "Right Index", "Left Thumb", "Left Index"
    @ColumnInfo(name = "encrypted_template") val encryptedTemplate: String, // Encrypted minutiae / feature vector
    @ColumnInfo(name = "template_version") val templateVersion: String = "ISO-19794-2-v2",
    @ColumnInfo(name = "quality_score") val qualityScore: Int = 90, // 0-100
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "attendance",
    indices = [Index(value = ["student_id", "attendance_date", "session"], unique = true)]
)
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "student_id") val studentId: Long,
    @ColumnInfo(name = "attendance_date") val attendanceDate: String, // YYYY-MM-DD
    @ColumnInfo(name = "weekday") val weekday: String, // Monday, Tuesday, Wednesday, Thursday, Friday
    @ColumnInfo(name = "session") val session: String, // Morning, Afternoon, Evening
    @ColumnInfo(name = "status") val status: String, // PRESENT, ABSENT
    @ColumnInfo(name = "verification_method") val verificationMethod: String = "FINGERPRINT", // FINGERPRINT, MANUAL_OVERRIDE
    @ColumnInfo(name = "verified_at") val verifiedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "recorded_by") val recordedBy: String = "ADMIN",
    @ColumnInfo(name = "remarks") val remarks: String = ""
)

@Entity(tableName = "attendance_sessions")
data class AttendanceSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "session_name") val sessionName: String, // Morning Session, Afternoon Session
    @ColumnInfo(name = "start_time") val startTime: String = "08:00",
    @ColumnInfo(name = "end_time") val endTime: String = "12:00",
    @ColumnInfo(name = "academic_year") val academicYear: String = "2026",
    @ColumnInfo(name = "semester") val semester: String = "Semester 1",
    @ColumnInfo(name = "is_active") val isActive: Boolean = true
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "admin_username") val adminUsername: String = "admin",
    @ColumnInfo(name = "action") val action: String, // e.g. "MANUAL_ATTENDANCE_OVERRIDE", "STUDENT_ENROLLED"
    @ColumnInfo(name = "entity_type") val entityType: String, // "ATTENDANCE", "STUDENT", "FINGERPRINT"
    @ColumnInfo(name = "entity_id") val entityId: String,
    @ColumnInfo(name = "details") val details: String
)

@Entity(tableName = "app_settings")
data class AppSetting(
    @PrimaryKey val key: String,
    @ColumnInfo(name = "value") val value: String,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "synchronization_records")
data class SynchronizationRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "sync_type") val syncType: String = "LOCAL_BACKUP",
    @ColumnInfo(name = "status") val status: String = "SUCCESS",
    @ColumnInfo(name = "records_count") val recordsCount: Int = 0,
    @ColumnInfo(name = "synced_at") val syncedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "details") val details: String = ""
)
