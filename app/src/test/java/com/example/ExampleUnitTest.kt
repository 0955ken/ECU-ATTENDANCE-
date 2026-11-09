package com.example

import com.example.biometrics.BiometricMatchResult
import com.example.data.database.DatabasePrepopulator
import com.example.data.model.BiometricTemplate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testPasswordHashing() {
        val salt = "ECU_SALT_2026"
        val hash1 = DatabasePrepopulator.hashPassword("admin123", salt)
        val hash2 = DatabasePrepopulator.hashPassword("admin123", salt)
        val hashWrong = DatabasePrepopulator.hashPassword("wrongpass", salt)

        assertEquals(hash1, hash2)
        assertNotEquals(hash1, hashWrong)
        assertEquals(64, hash1.length)
    }

    @Test
    fun testBiometric1toNMatchingLogic() {
        val candidateTemplates = listOf(
            BiometricTemplate(
                id = 1,
                studentId = 101,
                fingerPosition = "Right Thumb",
                encryptedTemplate = "ENC-TMP-ECU-24BBA006-RTHUMB-9A8B7C",
                qualityScore = 95
            ),
            BiometricTemplate(
                id = 2,
                studentId = 102,
                fingerPosition = "Left Thumb",
                encryptedTemplate = "ENC-TMP-ECU-24BBA007-LTHUMB-4B5C6D",
                qualityScore = 94
            )
        )

        // Test matching
        val target = "ENC-TMP-ECU-24BBA006-RTHUMB-9A8B7C"
        val match = candidateTemplates.find { it.encryptedTemplate == target }
        assertEquals(101L, match?.studentId)
        assertEquals("Right Thumb", match?.fingerPosition)
    }
}
