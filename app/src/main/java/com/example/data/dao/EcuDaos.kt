package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface AdminDao {
    @Query("SELECT * FROM admins WHERE username = :username LIMIT 1")
    suspend fun getAdminByUsername(username: String): Admin?

    @Query("SELECT COUNT(*) FROM admins")
    suspend fun getAdminCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(admin: Admin): Long

    @Update
    suspend fun updateAdmin(admin: Admin)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY full_name ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: Long): Student?

    @Query("SELECT * FROM students WHERE registration_number = :regNumber LIMIT 1")
    suspend fun getStudentByRegistrationNumber(regNumber: String): Student?

    @Query("SELECT * FROM students WHERE is_active = 1")
    fun getActiveStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE class_level = :className AND is_active = 1 ORDER BY full_name ASC")
    fun getStudentsByClass(className: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE faculty = :faculty AND is_active = 1 ORDER BY full_name ASC")
    fun getStudentsByFaculty(faculty: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE full_name LIKE '%' || :query || '%' OR registration_number LIKE '%' || :query || '%'")
    fun searchStudents(query: String): Flow<List<Student>>

    @Query("SELECT COUNT(*) FROM students")
    fun getTotalStudentsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM students WHERE is_active = 1")
    fun getActiveStudentsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>): List<Long>

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudentById(id: Long)
}

@Dao
interface AcademicDao {
    // Faculties
    @Query("SELECT * FROM faculties ORDER BY faculty_name ASC")
    fun getAllFaculties(): Flow<List<Faculty>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaculty(faculty: Faculty): Long

    @Delete
    suspend fun deleteFaculty(faculty: Faculty)

    // Programmes
    @Query("SELECT * FROM programmes ORDER BY programme_name ASC")
    fun getAllProgrammes(): Flow<List<Programme>>

    @Query("SELECT * FROM programmes WHERE faculty_id = :facultyId")
    fun getProgrammesByFaculty(facultyId: Long): Flow<List<Programme>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgramme(programme: Programme): Long

    @Delete
    suspend fun deleteProgramme(programme: Programme)

    // Academic Classes
    @Query("SELECT * FROM academic_classes ORDER BY class_name ASC")
    fun getAllClasses(): Flow<List<AcademicClass>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(academicClass: AcademicClass): Long

    @Delete
    suspend fun deleteClass(academicClass: AcademicClass)

    // Attendance Sessions
    @Query("SELECT * FROM attendance_sessions WHERE is_active = 1")
    fun getActiveSessions(): Flow<List<AttendanceSession>>

    @Query("SELECT * FROM attendance_sessions")
    fun getAllSessions(): Flow<List<AttendanceSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: AttendanceSession): Long
}

@Dao
interface BiometricDao {
    @Query("SELECT * FROM biometric_templates")
    fun getAllTemplates(): Flow<List<BiometricTemplate>>

    @Query("SELECT * FROM biometric_templates")
    suspend fun getAllTemplatesSync(): List<BiometricTemplate>

    @Query("SELECT * FROM biometric_templates WHERE student_id = :studentId")
    fun getTemplatesForStudent(studentId: Long): Flow<List<BiometricTemplate>>

    @Query("SELECT * FROM biometric_templates WHERE student_id = :studentId")
    suspend fun getTemplatesForStudentSync(studentId: Long): List<BiometricTemplate>

    @Query("SELECT COUNT(*) FROM biometric_templates WHERE student_id = :studentId")
    suspend fun countTemplatesForStudent(studentId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: BiometricTemplate): Long

    @Query("DELETE FROM biometric_templates WHERE student_id = :studentId AND finger_position = :fingerPosition")
    suspend fun deleteTemplate(studentId: Long, fingerPosition: String)

    @Query("DELETE FROM biometric_templates WHERE student_id = :studentId")
    suspend fun deleteAllTemplatesForStudent(studentId: Long)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY verified_at DESC")
    fun getAllAttendance(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance WHERE attendance_date = :date")
    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance WHERE attendance_date = :date AND session = :session")
    fun getAttendanceForDateAndSession(date: String, session: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance WHERE student_id = :studentId AND attendance_date = :date AND session = :session LIMIT 1")
    suspend fun getAttendanceRecord(studentId: Long, date: String, session: String): AttendanceRecord?

    @Query("SELECT * FROM attendance WHERE student_id = :studentId ORDER BY attendance_date DESC")
    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance WHERE attendance_date BETWEEN :startDate AND :endDate")
    fun getAttendanceBetweenDates(startDate: String, endDate: String): Flow<List<AttendanceRecord>>

    @Query("SELECT COUNT(*) FROM attendance WHERE attendance_date = :date AND status = 'PRESENT'")
    fun countPresentToday(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance WHERE attendance_date = :date AND status = 'ABSENT'")
    fun countAbsentToday(date: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(records: List<AttendanceRecord>)

    @Update
    suspend fun updateAttendance(record: AttendanceRecord)

    @Query("DELETE FROM attendance WHERE student_id = :studentId AND attendance_date = :date AND session = :session")
    suspend fun deleteAttendance(studentId: Long, date: String, session: String)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 200")
    fun getRecentAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLog): Long
}

@Dao
interface AppSettingDao {
    @Query("SELECT * FROM app_settings WHERE `key` = :key LIMIT 1")
    suspend fun getSetting(key: String): AppSetting?

    @Query("SELECT * FROM app_settings")
    fun getAllSettings(): Flow<List<AppSetting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: AppSetting)

    // Sync records
    @Query("SELECT * FROM synchronization_records ORDER BY synced_at DESC LIMIT 50")
    fun getSyncHistory(): Flow<List<SynchronizationRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncRecord(record: SynchronizationRecord): Long
}
