package com.example.data.firebase

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.data.model.AttendanceRecord
import com.example.data.model.Student
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CloudBackupMeta(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val studentCount: Long = 0L,
    val attendanceCount: Long = 0L,
    val academicYear: String = "2026",
    val backupPayload: String = "",
    val createdAtStr: String = ""
)

class FirebaseCloudSyncManager(private val context: Context) {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore by lazy {
        val dbId = context.getString(R.string.firestore_database_id)
        FirebaseFirestore.getInstance(dbId)
    }

    private val credentialManager = CredentialManager.create(context)

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _syncStatus = MutableStateFlow<String?>(null)
    val syncStatus: StateFlow<String?> = _syncStatus.asStateFlow()

    private val _cloudBackups = MutableStateFlow<List<CloudBackupMeta>>(emptyList())
    val cloudBackups: StateFlow<List<CloudBackupMeta>> = _cloudBackups.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            if (firebaseAuth.currentUser != null) {
                loadCloudBackups()
            } else {
                _cloudBackups.value = emptyList()
            }
        }
    }

    fun signInWithGoogle(
        activity: Activity,
        scope: CoroutineScope,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        val clientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            onError("Google Client ID configuration not found.")
            return
        }

        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    val authResult = auth.signInWithCredential(authCredential).await()
                    val user = authResult.user
                    if (user != null) {
                        _currentUser.value = user
                        // Sync user record to Firestore
                        saveUserProfile(user)
                        loadCloudBackups()
                        onSuccess(user)
                    } else {
                        onError("User was null after Google authentication.")
                    }
                } else {
                    onError("Unexpected credential type returned.")
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w("FirebaseSync", "Google Sign-In dismissed: ${e.message}")
                _syncStatus.value = "Google Sign-In was cancelled."
            } catch (e: Exception) {
                Log.e("FirebaseSync", "Sign in failed", e)
                onError(e.localizedMessage ?: "Google Sign-In failed.")
            }
        }
    }

    fun signOut(scope: CoroutineScope, onDone: () -> Unit) {
        scope.launch {
            withContext(NonCancellable) {
                try {
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                } catch (e: Exception) {
                    Log.w("FirebaseSync", "Clear credential failed: ${e.message}")
                } finally {
                    auth.signOut()
                    _currentUser.value = null
                    _cloudBackups.value = emptyList()
                    onDone()
                }
            }
        }
    }

    private fun saveUserProfile(user: FirebaseUser) {
        val uid = user.uid
        val docRef = firestore.collection("users").document(uid)
        val payload = mapOf(
            "userId" to uid,
            "email" to (user.email ?: ""),
            "displayName" to (user.displayName ?: "ECU Administrator"),
            "role" to "ADMINISTRATOR",
            "university" to "Emmanuel Christian University",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload)
    }

    suspend fun syncAttendanceToCloud(
        students: List<Student>,
        attendance: List<AttendanceRecord>
    ): Boolean {
        val user = auth.currentUser
        if (user == null) {
            _syncStatus.value = "Please sign in with Google to sync with Firebase Cloud."
            return false
        }

        return withContext(Dispatchers.IO) {
            try {
                val uid = user.uid
                val timestampStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
                val backupId = "ecu_cloud_${System.currentTimeMillis()}"

                val payloadSummary = "Students: ${students.size}, Attendance Records: ${attendance.size}"

                val backupData = mapOf(
                    "id" to backupId,
                    "userId" to uid,
                    "title" to "ECU Attendance Cloud Snapshot ($timestampStr)",
                    "studentCount" to students.size.toLong(),
                    "attendanceCount" to attendance.size.toLong(),
                    "academicYear" to "2026",
                    "backupPayload" to payloadSummary,
                    "createdAt" to FieldValue.serverTimestamp()
                )

                firestore.collection("users")
                    .document(uid)
                    .collection("cloud_backups")
                    .document(backupId)
                    .set(backupData)
                    .await()

                _syncStatus.value = "Cloud Sync successful! Synced to Firebase Firestore."
                loadCloudBackups()
                true
            } catch (e: Exception) {
                Log.e("FirebaseSync", "Cloud sync failed", e)
                _syncStatus.value = "Cloud Sync failed: ${e.message}"
                false
            }
        }
    }

    fun loadCloudBackups() {
        val user = auth.currentUser ?: return
        firestore.collection("users")
            .document(user.uid)
            .collection("cloud_backups")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getString("id") ?: doc.id
                    val title = doc.getString("title") ?: "Cloud Backup"
                    val stCount = doc.getLong("studentCount") ?: 0L
                    val attCount = doc.getLong("attendanceCount") ?: 0L
                    val yr = doc.getString("academicYear") ?: "2026"
                    val payload = doc.getString("backupPayload") ?: ""
                    CloudBackupMeta(
                        id = id,
                        userId = user.uid,
                        title = title,
                        studentCount = stCount,
                        attendanceCount = attCount,
                        academicYear = yr,
                        backupPayload = payload,
                        createdAtStr = title
                    )
                }
                _cloudBackups.value = list
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseSync", "Error loading cloud backups", e)
            }
    }
}
