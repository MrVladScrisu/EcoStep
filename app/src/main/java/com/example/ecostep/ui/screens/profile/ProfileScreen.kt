package com.example.ecostep.ui.screens.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.ecostep.util.PreferencesManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var isVerifying by remember { mutableStateOf(false) }
    var emailVerified by remember { mutableStateOf(currentUser?.isEmailVerified ?: false) }
    
    // Profile picture state
    val savedPhotoUri = PreferencesManager.getProfilePhotoUri(context)
    var profilePhotoUri by remember { mutableStateOf<Uri?>(savedPhotoUri?.let { Uri.parse(it) }) }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profilePhotoUri = it
            PreferencesManager.saveProfilePhotoUri(context, it.toString())
            Toast.makeText(context, "Poză de profil actualizată!", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Check verification status periodically when screen is visible
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(3000) // Check every 3 seconds
            currentUser?.reload()?.addOnSuccessListener {
                emailVerified = currentUser.isEmailVerified
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Înapoi",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Profilul meu",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Profile Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profilePhotoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(profilePhotoUri),
                        contentDescription = "Poză de profil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Camera icon overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Schimbă poza",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // User Name
        Text(
            text = currentUser?.displayName ?: "Utilizator EcoStep",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // User Email
        Text(
            text = currentUser?.email ?: "user@ecostep.ro",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Profile Info Cards
        ProfileInfoCard(
            icon = Icons.Default.Email,
            title = "Email",
            value = currentUser?.email ?: "user@ecostep.ro"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        ProfileInfoCard(
            icon = Icons.Default.DateRange,
            title = "Membru din",
            value = formatDate(currentUser?.metadata?.creationTimestamp ?: System.currentTimeMillis())
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        ProfileInfoCard(
            icon = Icons.Default.CheckCircle,
            title = "Status",
            value = if (emailVerified) "Verificat" else "Neverificat",
            actionButton = if (!emailVerified && currentUser != null) {
                {
                    TextButton(
                        onClick = {
                            isVerifying = true
                            currentUser.sendEmailVerification()
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Email de verificare trimis!", Toast.LENGTH_LONG).show()
                                    isVerifying = false
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Eroare: ${e.message}", Toast.LENGTH_SHORT).show()
                                    isVerifying = false
                                }
                        },
                        enabled = !isVerifying
                    ) {
                        Text(if (isVerifying) "Se trimite..." else "Trimite email verificare")
                    }
                }
            } else null
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Logout Button
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Deconectare",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ProfileInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    actionButton: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            actionButton?.invoke()
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.forLanguageTag("ro-RO"))
    return format.format(date)
}

