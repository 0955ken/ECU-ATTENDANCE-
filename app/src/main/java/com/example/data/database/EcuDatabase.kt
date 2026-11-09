package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AcademicDao
import com.example.data.dao.AdminDao
import com.example.data.dao.AppSettingDao
import com.example.data.dao.AttendanceDao
import com.example.data.dao.AuditLogDao
import com.example.data.dao.BiometricDao
import com.example.data.dao.StudentDao
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Admin::class,
        Student::class,
        Faculty::class,
        Programme::class,
        AcademicClass::class,
        BiometricTemplate::class,
        AttendanceRecord::class,
        AttendanceSession::class,
        AuditLog::class,
        AppSetting::class,
        SynchronizationRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EcuDatabase : RoomDatabase() {

    abstract fun adminDao(): AdminDao
    abstract fun studentDao(): StudentDao
    abstract fun academicDao(): AcademicDao
    abstract fun biometricDao(): BiometricDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun appSettingDao(): AppSettingDao

    companion object {
        @Volatile
        private var INSTANCE: EcuDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): EcuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EcuDatabase::class.java,
                    "ecu_attendance.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        DatabasePrepopulator.prepopulate(database)
                    }
                }
            }
        }
    }
}
