package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.bio_metric.BiometricHelper


@Composable
fun LockScreen(
    onUnlocked: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Trigger the prompt automatically when the screen opens
    LaunchedEffect(Unit) {
        if (activity != null && BiometricHelper.canAuthenticate(activity)) {
            BiometricHelper.authenticate(
                activity = activity,
                onSuccess = { onUnlocked() },
                onError = { errorMessage = it })
        } else {
            errorMessage = "Biometric authentication unavailable."
        }
    }

    // The UI shown behind the system prompt, or if the user cancels it and needs a button to try again
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = "Locked",
                modifier = Modifier.size(50.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "App Locked",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorMessage ?: "Use your fingerprint or face to unlock.",
            style = MaterialTheme.typography.bodyMedium,
            color = if (errorMessage != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.6f
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (activity != null) {
                    errorMessage = null
                    BiometricHelper.authenticate(
                        activity = activity,
                        onSuccess = { onUnlocked() },
                        onError = { errorMessage = it })
                }
            }, modifier = Modifier.height(56.dp)
        ) {
            Icon(Icons.Rounded.Fingerprint, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Unlock")
        }
    }
}