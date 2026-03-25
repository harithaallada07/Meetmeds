package uk.ac.tees.mad.meetmeds.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.tees.mad.meetmeds.domain.model.AuthResult
import uk.ac.tees.mad.meetmeds.presentation.navigation.Screen
import uk.ac.tees.mad.meetmeds.presentation.theme.MeetMedsTheme

@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val authState = viewModel.authState.value
    val resetPasswordState = viewModel.resetPasswordState.value

    // Handle Login/Register Results
    LaunchedEffect(authState) {
        when (authState) {
            is AuthResult.Success -> {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            }
            is AuthResult.Error -> {
                Toast.makeText(context, authState.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    // Handle Password Reset Results
    LaunchedEffect(resetPasswordState) {
        when (resetPasswordState) {
            is AuthResult.Success -> {
                Toast.makeText(context, "Reset link sent to email", Toast.LENGTH_LONG).show()
                viewModel.clearResetPasswordState()
            }
            is AuthResult.Error -> {
                Toast.makeText(context, resetPasswordState.message, Toast.LENGTH_SHORT).show()
                viewModel.clearResetPasswordState()
            }
            else -> {}
        }
    }

    // Pass events and state to the stateless content
    AuthContent(
        authState = authState,
        onLogin = { email, pass -> viewModel.loginUser(email, pass) },
        onRegister = { email, pass -> viewModel.registerUser(email, pass) },
        onResetPassword = { email -> viewModel.resetPassword(email) }
    )
}

@Composable
fun AuthContent(
    authState: AuthResult?,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String) -> Unit,
    onResetPassword: (String) -> Unit
) {
    val context = LocalContext.current

    // UI State for toggle
    var isLoginMode by remember { mutableStateOf(true) }

    // Input States
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // VISIBILITY STATES
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    // Dialog Visibility State
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        var resetEmail by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text(text = "Reset Password") },
            text = {
                Column {
                    Text("Enter your email address to receive a reset link.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resetEmail.isNotEmpty()) {
                            onResetPassword(resetEmail)
                            showForgotPasswordDialog = false
                        } else {
                            Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Send")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Main UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(MaterialTheme.colorScheme.background), // Uses Theme Background
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = if (isLoginMode) "Welcome Back" else "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
                }
            }
        )

        // Confirm Password Field (Only in Register Mode)
        if (!isLoginMode) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = confirmPassword.isNotEmpty() && confirmPassword != password,
                visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (isConfirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle Confirm Password Visibility")
                    }
                }
            )
            if (confirmPassword.isNotEmpty() && confirmPassword != password) {
                Text(
                    text = "Passwords do not match",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }

        // Forgot Password Link
        if (isLoginMode) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Forgot Password?",
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { showForgotPasswordDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Button
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    if (isLoginMode) {
                        onLogin(email, password)
                    } else {
                        if (password == confirmPassword) {
                            onRegister(email, password)
                        } else {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthResult.Loading
        ) {
            if (authState is AuthResult.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary, // Contrast against button primary color
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = if (isLoginMode) "Log In" else "Sign Up")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle Login/Signup
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isLoginMode) "Don't have an account? " else "Already have an account? ",
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (isLoginMode) "Sign Up" else "Log In",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    isLoginMode = !isLoginMode
                    email = ""
                    password = ""
                    confirmPassword = ""
                    isPasswordVisible = false
                    isConfirmPasswordVisible = false
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    MeetMedsTheme {
        AuthContent(
            authState = null,
            onLogin = { _, _ -> },
            onRegister = { _, _ -> },
            onResetPassword = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenLoadingPreview() {
    MeetMedsTheme {
        AuthContent(
            authState = AuthResult.Loading,
            onLogin = { _, _ -> },
            onRegister = { _, _ -> },
            onResetPassword = {}
        )
    }
}