package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.view_model.SettingViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.card.ProfileCard
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.groups.SettingsGroup
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.row.SettingsActionRow
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.row.SettingsSwitchRow
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Constants
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.bio_metric.BiometricHelper
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.currency.SupportedCurrencies

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier, viewModel: SettingViewModel, navController: NavHostController
) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val isDataLoading by viewModel.isDataLoading.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    var showCurrencySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsStateWithLifecycle()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            // Call the newly updated function!
            viewModel.exportFullDatabase(context, uri)
            scope.launch { snackbarHostState.showSnackbar("Full Database Exported Successfully!") }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }) { internalPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                // 1. ONLY apply the bottom padding here so we don't overlap navigation
                .padding(bottom = internalPadding.calculateBottomPadding())
                .padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(24.dp),
            // 2. Set top to 0.dp so it stays perfectly flush with your Top App Bar
            contentPadding = PaddingValues(top = 0.dp, bottom = 32.dp)
        ) {
            // 1. Profile Header
            item {
                ProfileCard()
            }

            // 2. Preferences Group
            item {
                SettingsGroup(title = "Preferences") {
                    SettingsSwitchRow(
                        title = "Dark Mode",
                        subtitle = "Toggle app theme",
                        icon = Icons.Rounded.DarkMode,
                        iconTint = Color(0xFF5E35B1),
                        isChecked = isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode(it) })
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    SettingsActionRow(
                        title = "Notifications",
                        subtitle = "Manage alerts and reminders",
                        icon = Icons.Rounded.NotificationsActive,
                        iconTint = Color(0xFFE53935),
                        onClick = {
                            navController.navigate(Constants.NOTIFICATION_SCREEN_SETTING)
                        })

                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )

                    SettingsSwitchRow(
                        title = "App Lock",
                        subtitle = "Require Biometrics to open app",
                        icon = Icons.Rounded.Lock,
                        iconTint = Color(0xFFE91E63), // Pink/Red color
                        isChecked = isAppLockEnabled,
                        onCheckedChange = { isChecked ->
                            val activity = context as? FragmentActivity
                            if (activity != null && isChecked) {
                                // If they are turning it ON, verify their fingerprint right now to make sure it works!
                                if (BiometricHelper.canAuthenticate(activity)) {
                                    BiometricHelper.authenticate(
                                        activity = activity,
                                        title = "Enable App Lock",
                                        subtitle = "Verify your identity to enable security",
                                        onSuccess = {
                                            viewModel.toggleAppLock(true)
                                            scope.launch { snackbarHostState.showSnackbar("App Lock Enabled \uD83D\uDD12") }
                                        },
                                        onError = { error ->
                                            scope.launch { snackbarHostState.showSnackbar("Setup failed: $error") }
                                        }
                                    )
                                } else {
                                    scope.launch { snackbarHostState.showSnackbar("Your device doesn't support biometric unlock.") }
                                }
                            } else {
                                // Turning it off requires no check
                                viewModel.toggleAppLock(false)
                            }
                        }
                    )

//
//                    SettingsActionRow(
//                        title = "Currency",
//                        subtitle = "Current: $selectedCurrency",
//                        icon = Icons.Rounded.Payments,
//                        iconTint = Color(0xFF4CAF50),
//                        onClick = { showCurrencySheet = true })

                }
            }

            // 3. Data & Testing Group (For your Bulk Import!)
            item {
                SettingsGroup(title = "Data Management") {
                    SettingsActionRow(
                        title = "Load Dummy Data",
                        subtitle = "Inject 6 months of test data",
                        icon = Icons.Rounded.AutoFixHigh,
                        iconTint = Color(0xFF00897B),
                        isLoading = isDataLoading,
                        onClick = {
                            viewModel.injectDummyData()
                            scope.launch {
                                snackbarHostState.showSnackbar("6 Months of data injected successfully!")
                            }
                        })
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    SettingsActionRow(
                        title = "Export Data",
                        subtitle = "Save a full backup of all data",
                        icon = Icons.Rounded.CloudDownload,
                        iconTint = Color(0xFF1E88E5),
                        onClick = {
                            // Suggest a better filename for the full backup
                            exportLauncher.launch("Finance_Full_Backup.csv")
                        })
                }
            }

            // 4. About Group
            item {
                SettingsGroup(title = "About") {
                    SettingsActionRow(
                        title = "Privacy Policy",
                        icon = Icons.Rounded.Security,
                        iconTint = Color(0xFF757575),
                        onClick = { /* TODO */ })
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    SettingsActionRow(
                        title = "App Version",
                        subtitle = "v1.0.0 (Beta)",
                        icon = Icons.Rounded.Info,
                        iconTint = Color(0xFF757575),
                        showForwardArrow = false,
                        onClick = { })
                }
            }
        }
    }


//    if (showCurrencySheet) {
//        ModalBottomSheet(
//            onDismissRequest = { showCurrencySheet = false },
//            sheetState = sheetState,
//            containerColor = MaterialTheme.colorScheme.surface,
//            dragHandle = { BottomSheetDefaults.DragHandle() }) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
//            ) {
//                Text(
//                    text = "Select Currency",
//                    style = MaterialTheme.typography.titleLarge,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(bottom = 16.dp)
//                )
//
//                SupportedCurrencies.list.forEach { currency ->
//                    val isSelected = currency.symbol == selectedCurrency
//
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clip(RoundedCornerShape(16.dp))
//                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
//                            .clickable {
//                                viewModel.updateCurrency(currency.symbol)
//                                showCurrencySheet = false
//                            }
//                            .padding(16.dp),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.SpaceBetween) {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Box(
//                                modifier = Modifier
//                                    .size(40.dp)
//                                    .clip(CircleShape)
//                                    .background(MaterialTheme.colorScheme.surfaceVariant),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Text(
//                                    currency.symbol,
//                                    style = MaterialTheme.typography.titleMedium,
//                                    fontWeight = FontWeight.Bold
//                                )
//                            }
//                            Spacer(modifier = Modifier.width(16.dp))
//                            Column {
//                                Text(
//                                    currency.code,
//                                    style = MaterialTheme.typography.titleMedium,
//                                    fontWeight = FontWeight.Bold
//                                )
//                                Text(
//                                    currency.name,
//                                    style = MaterialTheme.typography.labelMedium,
//                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                                )
//                            }
//                        }
//
//                        if (isSelected) {
//                            Icon(
//                                Icons.Rounded.CheckCircle,
//                                contentDescription = "Selected",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }

}





