package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EcuGold
import com.example.ui.theme.EcuGoldContainer
import com.example.ui.theme.EcuGoldLight
import com.example.ui.theme.EcuNavy
import com.example.ui.theme.EcuNavyDark
import com.example.ui.theme.EcuNavySurface
import com.example.ui.theme.EcuRed
import com.example.ui.theme.White

@Composable
fun AdminLoginScreen(
    onLoginSuccess: (String, String) -> Unit,
    onRecoverPassword: (username: String, answer: String, newPass: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit,
    errorMessage: String? = null
) {
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showRecoveryDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EcuNavyDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // University Crest Logo
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .border(2.5.dp, EcuGold, CircleShape)
                    .background(EcuNavy),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ecu_logo),
                    contentDescription = "ECU Crest",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(92.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "EMMANUEL CHRISTIAN UNIVERSITY",
                color = EcuGoldLight,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "ECU DATA ATTENDANCE",
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Smart Biometric Attendance Management System",
                color = White.copy(alpha = 0.75f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Card
            Card(
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EcuNavy),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcuGold.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = EcuGoldLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ADMINISTRATOR ACCESS ONLY",
                            color = EcuGoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Sign in to manage student attendance & biometric registers",
                        color = White.copy(alpha = 0.65f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                    )

                    if (errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(EcuRed.copy(alpha = 0.15f))
                                .border(1.dp, EcuRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = errorMessage,
                                color = EcuRed,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Username input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Administrator Username") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = EcuGold)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EcuGold,
                            unfocusedBorderColor = EcuGold.copy(alpha = 0.4f),
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedLabelColor = EcuGoldLight,
                            unfocusedLabelColor = White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = EcuGold)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = White.copy(alpha = 0.7f)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onLoginSuccess(username, password) }
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EcuGold,
                            unfocusedBorderColor = EcuGold.copy(alpha = 0.4f),
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedLabelColor = EcuGoldLight,
                            unfocusedLabelColor = White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign In Button
                    Button(
                        onClick = { onLoginSuccess(username, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EcuGold,
                            contentColor = EcuNavyDark
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "LOGIN TO SYSTEM",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Forgot Password Link
                    TextButton(
                        onClick = { showRecoveryDialog = true },
                        modifier = Modifier.testTag("forgot_password_button")
                    ) {
                        Text(
                            text = "Forgot password? Authorized Recovery",
                            color = EcuGoldLight,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer University Notice
            Text(
                text = "Emmanuel Christian University • Offline Encrypted System • v2.4",
                color = White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    // Password Recovery Dialog
    if (showRecoveryDialog) {
        var recUsername by remember { mutableStateOf("admin") }
        var recAnswer by remember { mutableStateOf("1990") }
        var newPass by remember { mutableStateOf("") }
        var recError by remember { mutableStateOf<String?>(null) }
        var recSuccess by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showRecoveryDialog = false },
            title = {
                Text(
                    text = "Administrator Password Recovery",
                    color = EcuGoldLight,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Authorized security recovery question: What is the ECU University Founding Year?",
                        color = White,
                        fontSize = 12.sp
                    )

                    if (recError != null) {
                        Text(text = recError!!, color = EcuRed, fontSize = 11.sp)
                    }
                    if (recSuccess) {
                        Text(text = "Password reset successfully! Please sign in.", color = EcuGold, fontSize = 12.sp)
                    }

                    OutlinedTextField(
                        value = recUsername,
                        onValueChange = { recUsername = it },
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = recAnswer,
                        onValueChange = { recAnswer = it },
                        label = { Text("Recovery Answer (Year)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("New Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPass.isBlank()) {
                            recError = "Please enter a new password."
                            return@Button
                        }
                        onRecoverPassword(recUsername, recAnswer, newPass, {
                            recSuccess = true
                            recError = null
                        }, { err ->
                            recError = err
                        })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EcuGold, contentColor = EcuNavyDark)
                ) {
                    Text("Reset Password")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRecoveryDialog = false }) {
                    Text("Cancel", color = White)
                }
            },
            containerColor = EcuNavy,
            shape = RoundedCornerShape(12.dp)
        )
    }
}
