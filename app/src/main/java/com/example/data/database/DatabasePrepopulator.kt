package com.example.data.database

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
import java.security.MessageDigest

object DatabasePrepopulator {

    fun hashPassword(password: String, salt: String = "ECU_SALT_2026"): String {
        val input = "$salt:$password"
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun prepopulate(db: EcuDatabase) {
        // 1. Insert Administrator
        val adminSalt = "ECU_SALT_2026"
        val defaultAdmin = Admin(
            id = 1,
            username = "admin",
            passwordHash = hashPassword("admin123", adminSalt),
            salt = adminSalt,
            fullName = "System Administrator (ECU)",
            recoveryQuestion = "University Founding Year",
            recoveryAnswerHash = hashPassword("1990", adminSalt)
        )
        db.adminDao().insertAdmin(defaultAdmin)

        // 2. Insert Faculties
        val faculties = listOf(
            Faculty(id = 1, facultyName = "Faculty of Business Administration", code = "FBA", deanName = "Prof. Samuel Deng", description = "Undergraduate & Graduate Business Studies"),
            Faculty(id = 2, facultyName = "Faculty of Science & Information Technology", code = "FSIT", deanName = "Eng. Mary Kiden", description = "Computer Science, Software & Telecom"),
            Faculty(id = 3, facultyName = "Faculty of Theology & Biblical Studies", code = "FTBS", deanName = "Rev. Dr. Joseph Lado", description = "Theology, Pastoral Ministry & Ethics"),
            Faculty(id = 4, facultyName = "Faculty of Education & Humanities", code = "FEDH", deanName = "Dr. Paul Wani", description = "Teacher Education, Languages & Social Sciences")
        )
        faculties.forEach { db.academicDao().insertFaculty(it) }

        // 3. Insert Programmes
        val programmes = listOf(
            Programme(id = 1, programmeName = "Bachelor of Business Administration", code = "BBA", facultyId = 1, facultyName = "Faculty of Business Administration", durationYears = 4),
            Programme(id = 2, programmeName = "Bachelor of Science in Information Technology", code = "BSc IT", facultyId = 2, facultyName = "Faculty of Science & Information Technology", durationYears = 4),
            Programme(id = 3, programmeName = "Bachelor of Theology", code = "BTh", facultyId = 3, facultyName = "Faculty of Theology & Biblical Studies", durationYears = 3),
            Programme(id = 4, programmeName = "Bachelor of Arts in Education", code = "BA Ed", facultyId = 4, facultyName = "Faculty of Education & Humanities", durationYears = 4)
        )
        programmes.forEach { db.academicDao().insertProgramme(it) }

        // 4. Insert Classes
        val classes = listOf(
            AcademicClass(id = 1, className = "BBA Year 1", programmeId = 1, programmeCode = "BBA", academicYear = "2026", semester = "Semester 1", level = "Year 1"),
            AcademicClass(id = 2, className = "BBA Year 2", programmeId = 1, programmeCode = "BBA", academicYear = "2026", semester = "Semester 1", level = "Year 2"),
            AcademicClass(id = 3, className = "BBA Year 3", programmeId = 1, programmeCode = "BBA", academicYear = "2026", semester = "Semester 1", level = "Year 3"),
            AcademicClass(id = 4, className = "BBA Year 4", programmeId = 1, programmeCode = "BBA", academicYear = "2026", semester = "Semester 1", level = "Year 4"),
            AcademicClass(id = 5, className = "BSc IT Year 2", programmeId = 2, programmeCode = "BSc IT", academicYear = "2026", semester = "Semester 1", level = "Year 2"),
            AcademicClass(id = 6, className = "BTh Year 1", programmeId = 3, programmeCode = "BTh", academicYear = "2026", semester = "Semester 1", level = "Year 1"),
            AcademicClass(id = 7, className = "BA Ed Year 3", programmeId = 4, programmeCode = "BA Ed", academicYear = "2026", semester = "Semester 1", level = "Year 3")
        )
        classes.forEach { db.academicDao().insertClass(it) }

        // 5. Insert Sessions
        val sessions = listOf(
            AttendanceSession(id = 1, sessionName = "Morning Session", startTime = "08:00", endTime = "12:00", academicYear = "2026", semester = "Semester 1", isActive = true),
            AttendanceSession(id = 2, sessionName = "Afternoon Session", startTime = "13:00", endTime = "17:00", academicYear = "2026", semester = "Semester 1", isActive = false),
            AttendanceSession(id = 3, sessionName = "Evening Session", startTime = "17:30", endTime = "20:30", academicYear = "2026", semester = "Semester 1", isActive = false)
        )
        sessions.forEach { db.academicDao().insertSession(it) }

        // 6. Insert Students (Matching Master Prompt Format)
        val students = listOf(
            Student(
                id = 1,
                fullName = "Deng Garang Deng",
                registrationNumber = "24/BBA/006",
                faculty = "Faculty of Business Administration",
                department = "Accounting & Finance",
                programme = "Bachelor of Business Administration",
                academicYear = "2026",
                classLevel = "BBA Year 2",
                studentStatus = "ACTIVE",
                fingerprintRegistrationStatus = "REGISTERED",
                studentIdentificationNumber = "ECU-2024-006"
            ),
            Student(
                id = 2,
                fullName = "Rebecca Tabitha Taban",
                registrationNumber = "24/BBA/007",
                faculty = "Faculty of Business Administration",
                department = "Business Management",
                programme = "Bachelor of Business Administration",
                academicYear = "2026",
                classLevel = "BBA Year 2",
                studentStatus = "ACTIVE",
                fingerprintRegistrationStatus = "REGISTERED",
                studentIdentificationNumber = "ECU-2024-007"
            ),
            Student(
                id = 3,
                fullName = "Emmanuel Lado Wani",
                registrationNumber = "24/BBA/008",
                faculty = "Faculty of Business Administration",
                department = "Marketing",
                programme = "Bachelor of Business Administration",
                academicYear = "2026",
                classLevel = "BBA Year 2",
                studentStatus = "ACTIVE",
                fingerprintRegistrationStatus = "REGISTERED",
                studentIdentificationNumber = "ECU-2024-008"
            ),
            Student(
                id = 4,
                fullName = "Sarah Nyidier Gatluak",
                registrationNumber = "24/BBA/009",
                faculty = "Faculty of Business Administration",
                department = "Procurement & Logistics",
                programme = "Bachelor of Business Administration",
                academicYear = "2026",
                classLevel = "BBA Year 2",
                studentStatus = "ACTIVE",
                fingerprintRegistrationStatus = "REGISTERED",
                studentIdentificationNumber = "ECU-2024-009"
            ),
            Student(
                id = 5,
                fullName = "Peter Bol Majok",
                registrationNumber = "24/IT/012",
                faculty = "Faculty of Science & Information Technology",
                department = "Computer Science",
                programme = "Bachelor of Science in Information Technology",
                academicYear = "2026",
                classLevel = "BSc IT Year 2",
                studentStatus = "ACTIVE",
                fingerprintRegistrationStatus = "REGISTERED",
                studentIdentificationNumber = "ECU-2024-012"
            ),
            Student(
                id = 6,
                fullName = "Grace Aluel Kuol",
                registrationNumber = "24/IT/013",
                faculty = "Faculty of Science & Information Technology",
                department = "Software Engineering",
                programme = "Bachelor of Science in Information Technology",
                academicYear = "2026",
                classLevel = "BSc IT Year 2",
                studentStatus = "ACTIVE",
                fingerprintRegistrationStatus = "REGISTERED",
                studentIdentificationNumber = "ECU-2024-013"
            ),
            Student(
                id = 7,
                fullName = "John Kenyi Daniel",
                registrationNumber = "24/THEO/004",
                faculty = "Faculty of Theology & Biblical Studies",
                department = "Systematic Theology",
                programme = "Bachelor of Theology",
                academicYear = "2026",
                classLevel = "BTh Year 1",
                studentStatus = "ACTIVE",
                fingerprintRegistrationStatus = "NOT_REGISTERED",
                studentIdentificationNumber = "ECU-2024-004"
            ),
            Student(
                id = 8,
                fullName = "Mary Achol Akech",
                registrationNumber = "24/ED/021",
                faculty = "Faculty of Education & Humanities",
                department = "Educational Foundations",
                programme = "Bachelor of Arts in Education",
                academicYear = "2026",
                classLevel = "BA Ed Year 3",
                studentStatus = "ACTIVE",
                fingerprintRegistrationStatus = "NOT_REGISTERED",
                studentIdentificationNumber = "ECU-2024-021"
            )
        )
        students.forEach { db.studentDao().insertStudent(it) }

        // 7. Insert Biometric Templates for Registered Students (Encrypted ISO format templates)
        val templates = listOf(
            BiometricTemplate(id = 1, studentId = 1, fingerPosition = "Right Thumb", encryptedTemplate = "ENC-TMP-ECU-24BBA006-RTHUMB-9A8B7C", qualityScore = 95),
            BiometricTemplate(id = 2, studentId = 1, fingerPosition = "Right Index", encryptedTemplate = "ENC-TMP-ECU-24BBA006-RINDEX-1F2E3D", qualityScore = 92),
            BiometricTemplate(id = 3, studentId = 2, fingerPosition = "Right Thumb", encryptedTemplate = "ENC-TMP-ECU-24BBA007-RTHUMB-4B5C6D", qualityScore = 94),
            BiometricTemplate(id = 4, studentId = 2, fingerPosition = "Right Index", encryptedTemplate = "ENC-TMP-ECU-24BBA007-RINDEX-7E8F9A", qualityScore = 91),
            BiometricTemplate(id = 5, studentId = 3, fingerPosition = "Right Thumb", encryptedTemplate = "ENC-TMP-ECU-24BBA008-RTHUMB-2A3B4C", qualityScore = 89),
            BiometricTemplate(id = 6, studentId = 4, fingerPosition = "Right Thumb", encryptedTemplate = "ENC-TMP-ECU-24BBA009-RTHUMB-6D7E8F", qualityScore = 93),
            BiometricTemplate(id = 7, studentId = 5, fingerPosition = "Right Thumb", encryptedTemplate = "ENC-TMP-ECU-24IT012-RTHUMB-5F6A7B", qualityScore = 96),
            BiometricTemplate(id = 8, studentId = 6, fingerPosition = "Right Thumb", encryptedTemplate = "ENC-TMP-ECU-24IT013-RTHUMB-3C4D5E", qualityScore = 90)
        )
        templates.forEach { db.biometricDao().insertTemplate(it) }

        // 8. Sample Attendance Records for Current Week (Monday to Friday)
        val sampleAttendance = listOf(
            // Monday
            AttendanceRecord(id = 1, studentId = 1, attendanceDate = "2026-11-02", weekday = "Monday", session = "Morning Session", status = "PRESENT", verificationMethod = "FINGERPRINT"),
            AttendanceRecord(id = 2, studentId = 2, attendanceDate = "2026-11-02", weekday = "Monday", session = "Morning Session", status = "PRESENT", verificationMethod = "FINGERPRINT"),
            AttendanceRecord(id = 3, studentId = 3, attendanceDate = "2026-11-02", weekday = "Monday", session = "Morning Session", status = "PRESENT", verificationMethod = "FINGERPRINT"),
            AttendanceRecord(id = 4, studentId = 4, attendanceDate = "2026-11-02", weekday = "Monday", session = "Morning Session", status = "ABSENT", verificationMethod = "MANUAL_OVERRIDE", remarks = "Excused medical"),
            // Tuesday
            AttendanceRecord(id = 5, studentId = 1, attendanceDate = "2026-11-03", weekday = "Tuesday", session = "Morning Session", status = "PRESENT", verificationMethod = "FINGERPRINT"),
            AttendanceRecord(id = 6, studentId = 2, attendanceDate = "2026-11-03", weekday = "Tuesday", session = "Morning Session", status = "ABSENT", verificationMethod = "MANUAL_OVERRIDE", remarks = "Official university sports"),
            AttendanceRecord(id = 7, studentId = 3, attendanceDate = "2026-11-03", weekday = "Tuesday", session = "Morning Session", status = "PRESENT", verificationMethod = "FINGERPRINT"),
            AttendanceRecord(id = 8, studentId = 4, attendanceDate = "2026-11-03", weekday = "Tuesday", session = "Morning Session", status = "PRESENT", verificationMethod = "FINGERPRINT"),
            // Wednesday
            AttendanceRecord(id = 9, studentId = 1, attendanceDate = "2026-11-04", weekday = "Wednesday", session = "Morning Session", status = "ABSENT", verificationMethod = "MANUAL_OVERRIDE", remarks = "Unverified"),
            AttendanceRecord(id = 10, studentId = 2, attendanceDate = "2026-11-04", weekday = "Wednesday", session = "Morning Session", status = "PRESENT", verificationMethod = "FINGERPRINT"),
            AttendanceRecord(id = 11, studentId = 3, attendanceDate = "2026-11-04", weekday = "Wednesday", session = "Morning Session", status = "PRESENT", verificationMethod = "FINGERPRINT"),
            // Thursday
            AttendanceRecord(id = 12, studentId = 1, attendanceDate = "2026-11-05", weekday = "Thursday", session = "Morning Session", status = "PRESENT", verificationMethod = "FINGERPRINT"),
            AttendanceRecord(id = 13, studentId = 2, attendanceDate = "2026-11-05", weekday = "Thursday", session = "Morning Session", status = "PRESENT", verificationMethod = "FINGERPRINT"),
            AttendanceRecord(id = 14, studentId = 3, attendanceDate = "2026-11-05", weekday = "Thursday", session = "Morning Session", status = "PRESENT", verificationMethod = "FINGERPRINT"),
            // Friday
            AttendanceRecord(id = 15, studentId = 1, attendanceDate = "2026-11-06", weekday = "Friday", session = "Morning Session", status = "PRESENT", verificationMethod = "FINGERPRINT"),
            AttendanceRecord(id = 16, studentId = 2, attendanceDate = "2026-11-06", weekday = "Friday", session = "Morning Session", status = "PRESENT", verificationMethod = "FINGERPRINT"),
            AttendanceRecord(id = 17, studentId = 3, attendanceDate = "2026-11-06", weekday = "Friday", session = "Morning Session", status = "PRESENT", verificationMethod = "FINGERPRINT")
        )
        sampleAttendance.forEach { db.attendanceDao().insertAttendance(it) }

        // 9. Initial Settings
        val settings = listOf(
            AppSetting("university_name", "EMMANUEL CHRISTIAN UNIVERSITY"),
            AppSetting("system_title", "ECU DATA ATTENDANCE"),
            AppSetting("tagline", "Smart Biometric Attendance Management System"),
            AppSetting("academic_year", "2026"),
            AppSetting("current_semester", "Semester 1"),
            AppSetting("scanner_model", "SecuGen Hamster Pro 20 / USB Biometric"),
            AppSetting("auto_lock_minutes", "15")
        )
        settings.forEach { db.appSettingDao().setSetting(it) }

        // 10. Audit Log
        db.auditLogDao().insertLog(
            AuditLog(
                id = 1,
                timestamp = System.currentTimeMillis(),
                adminUsername = "admin",
                action = "SYSTEM_INITIALIZATION",
                entityType = "SYSTEM",
                entityId = "SETUP",
                details = "ECU DATA ATTENDANCE initialized with official university database and default admin account."
            )
        )
    }
}
