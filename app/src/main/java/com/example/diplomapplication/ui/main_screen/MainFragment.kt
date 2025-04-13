package com.example.diplomapplication.ui.main_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.diplomapplication.ui.theme.DiplomApplicationTheme
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    private val viewModel: MainScreenViewModel by viewModel()
    private var healthConnectClient: HealthConnectClient? = null

    private val PERMISSIONS =
        setOf(
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getWritePermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getWritePermission(StepsRecord::class)
        )
    val requestPermissionActivityContract =
        PermissionController.createRequestPermissionResultContract()
    val requestPermissions =
        registerForActivityResult(requestPermissionActivityContract) { granted ->
            if (granted.containsAll(PERMISSIONS)) {
                viewModel.isPermissionsForHealthGranted.update { true }
            } else {
                viewModel.isPermissionsForHealthGranted.update { false }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val navController = findNavController()
        viewModel.navController = navController
        viewModel.init()

        context?.let { context ->
            val availabilityStatus = HealthConnectClient.getSdkStatus(context)
            when (availabilityStatus) {
                HealthConnectClient.SDK_UNAVAILABLE -> viewModel.showHealthDialog()
                HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> viewModel.showHealthDialog()
                else -> {
                    healthConnectClient = HealthConnectClient.getOrCreate(context)
                    viewModel.healthConnectClient = healthConnectClient
                    val healthClient = healthConnectClient
                    if (healthClient != null) {
                        viewModel.viewModelScope.launch {
                            checkPermissionsAndRun(healthClient)
                        }
                    }
                }
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    MainScreen(
                        uiState = viewModel.uiState.collectAsState().value,
                        openProfile = { viewModel.navigateToProfile() },
                        openEscal = { viewModel.navigateToEscalTesting() },
                        refresh = { viewModel.refresh() },
                        closeHealthAlert = { viewModel.closeHealthDialog() }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        context?.let { context ->
            val availabilityStatus = HealthConnectClient.getSdkStatus(context)
            when (availabilityStatus) {
                HealthConnectClient.SDK_UNAVAILABLE -> {}
                HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {}
                else -> {
                    healthConnectClient = HealthConnectClient.getOrCreate(context)
                    viewModel.healthConnectClient = healthConnectClient
                    val healthClient = healthConnectClient
                    if (healthClient != null) {
                        viewModel.viewModelScope.launch {
                            checkPermissionsAndRun(healthClient)
                        }
                    }
                }
            }
        }
    }

    suspend fun checkPermissionsAndRun(healthConnectClient: HealthConnectClient) {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        if (granted.containsAll(PERMISSIONS)) {
            viewModel.isPermissionsForHealthGranted.update { true }
            viewModel.updateHeartRateOnServer()
        } else {
            requestPermissions.launch(PERMISSIONS)
        }
    }

}
