package com.example.ecostep.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun EmailLoginScreen(
    onLoginSuccess: () -> Unit,
    onBackToBiometric: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isRegisterMode by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val auth = remember { FirebaseAuth.getInstance() }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Text(
            text = if (isRegisterMode) "Înregistrare" else "Login",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (isRegisterMode) 
                "Creează un cont nou" 
            else 
                "Conectează-te la contul tău",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                errorMessage = null
            },
            label = { Text("Email") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                errorMessage = null
            },
            label = { Text("Parolă") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) 
                            Icons.Default.Check 
                        else 
                            Icons.Default.Close,
                        contentDescription = if (passwordVisible) "Ascunde parola" else "Arată parola"
                    )
                }
            },
            visualTransformation = if (passwordVisible) 
                VisualTransformation.None 
            else 
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Login/Register Button
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Completează toate câmpurile"
                    return@Button
                }
                
                isLoading = true
                errorMessage = null
                
                scope.launch {
                    try {
                        if (isRegisterMode) {
                            // Înregistrare
                            auth.createUserWithEmailAndPassword(email, password).await()
                        } else {
                            // Login
                            auth.signInWithEmailAndPassword(email, password).await()
                        }
                        onLoginSuccess()
                    } catch (e: Exception) {
                        errorMessage = when {
                            e.message?.contains("email") == true -> 
                                "Email invalid"
                            e.message?.contains("password") == true -> 
                                "Parolă prea scurtă (min 6 caractere)"
                            e.message?.contains("user-not-found") == true -> 
                                "Contul nu există"
                            e.message?.contains("wrong-password") == true -> 
                                "Parolă greșită"
                            e.message?.contains("email-already-in-use") == true -> 
                                "Email-ul este deja folosit"
                            else -> 
                                "Eroare: ${e.message}"
                        }
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = if (isRegisterMode) "Înregistrează-te" else "Login",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Switch Login/Register
        TextButton(
            onClick = { 
                isRegisterMode = !isRegisterMode
                errorMessage = null
            },
            enabled = !isLoading
        ) {
            Text(
                text = if (isRegisterMode) 
                    "Am deja cont. Login" 
                else 
                    "Nu am cont. Înregistrare",
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Back to Biometric
        OutlinedButton(
            onClick = onBackToBiometric,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Înapoi la Login cu Amprentă")
        }
        
        // Error Message
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "💡 Tip: Folosește același email pe toate dispozitivele",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}

