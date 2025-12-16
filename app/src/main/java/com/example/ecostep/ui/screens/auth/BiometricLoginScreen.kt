package com.example.ecostep.ui.screens.auth

import android.app.Activity
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.ecostep.LocalFragmentActivity
import com.google.firebase.auth.FirebaseAuth

@Composable
fun BiometricLoginScreen(
    onLoginSuccess: () -> Unit,
    onSwitchToEmail: () -> Unit
) {
    val context = LocalContext.current
    val activity = LocalFragmentActivity.current
    val auth = remember { FirebaseAuth.getInstance() }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var canUseBiometric by remember { mutableStateOf(false) }
    
    // Verificăm dacă dispozitivul suportă biometric
    LaunchedEffect(Unit) {
        val biometricManager = BiometricManager.from(context)
        canUseBiometric = when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or 
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                errorMessage = "Dispozitivul nu are senzor biometric"
                false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                errorMessage = "Senzorul biometric nu este disponibil"
                false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                errorMessage = "Nu ai configurat amprentă/Face ID. Configurează în Setări."
                false
            }
            else -> {
                errorMessage = "Autentificare biometrică indisponibilă"
                false
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo/Icon
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Amprentă",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "EcoStep",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Urmărește-ți impactul ecologic",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        if (canUseBiometric) {
            Button(
                onClick = {
                    if (activity != null) {
                        showBiometricPrompt(
                            activity = activity,
                            onSuccess = {
                                // Login cu email după autentificare biometrică
                                auth.signInWithEmailAndPassword("vladscrisu@yahoo.com", "123456")
                                    .addOnSuccessListener {
                                        onLoginSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        errorMessage = "Eroare Firebase: ${e.localizedMessage}"
                                    }
                            },
                            onError = { error -> errorMessage = error }
                        )
                    } else {
                        errorMessage = "Eroare: Activity nu este disponibil. Folosește 'Login cu Email/Parolă'"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Deblochează cu Amprentă",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onSwitchToEmail,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login cu Email/Parolă")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = {
                    // Login anonim pentru demo
                    auth.signInAnonymously()
                        .addOnSuccessListener {
                            onLoginSuccess()
                        }
                        .addOnFailureListener { e ->
                            errorMessage = "Eroare: ${e.message}"
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Intră fără Amprentă (Demo)")
            }
        } else {
            // Dacă nu e disponibil biometric, oferim opțiuni alternative
            Button(
                onClick = onSwitchToEmail,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Login cu Email/Parolă")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = {
                    // Login anonim pentru demo
                    auth.signInAnonymously()
                        .addOnSuccessListener {
                            onLoginSuccess()
                        }
                        .addOnFailureListener { e ->
                            errorMessage = "Eroare: ${e.message}"
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Intră fără Autentificare (Demo)")
            }
        }
        
        // Mesaj de eroare
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
            text = "🔒 Datele tale sunt protejate și stocate local",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}

private fun showBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    
    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
                Toast.makeText(activity, "✅ Autentificare reușită!", Toast.LENGTH_SHORT).show()
            }
            
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                    BiometricPrompt.ERROR_CANCELED -> {
                        onError("Autentificare anulată")
                    }
                    BiometricPrompt.ERROR_LOCKOUT,
                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                        onError("Prea multe încercări. Încearcă mai târziu.")
                    }
                    else -> {
                        onError("Eroare: $errString")
                    }
                }
            }
            
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Nu afișăm eroare aici, utilizatorul poate încerca din nou
                Toast.makeText(activity, "❌ Amprentă nerecunoscută", Toast.LENGTH_SHORT).show()
            }
        }
    )
    
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Deblochează EcoStep")
        .setSubtitle("Folosește amprenta pentru a te autentifica")
        .setDescription("Plasează degetul pe senzor")
        .setNegativeButtonText("Anulează")
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        .build()
    
    biometricPrompt.authenticate(promptInfo)
}

