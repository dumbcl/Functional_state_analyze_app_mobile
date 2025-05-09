package com.example.diplomapplication.ui.main_screen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.diplomapplication.R
import com.example.diplomapplication.data.TestType
import com.example.diplomapplication.ui.theme.DiplomApplicationTheme
import com.example.diplomapplication.util.HEART_RATE_BUNDLE
import com.example.diplomapplication.util.PPG_FRAGMENT_REQUEST_KEY
import com.example.diplomapplication.util.TEST_FINISHED
import com.google.android.material.snackbar.Snackbar
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
        viewModel.showSnack = {
            Snackbar
                .make(requireView(), getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG)
                .show()
        }
        val login = requireContext().getSharedPreferences("APP_SHARED_PREFERENCES", Context.MODE_PRIVATE).getString("USERNAME", "")
        if (login.isNullOrEmpty().not()) viewModel.setUserName(login)
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
                        openTest = { viewModel.openTest(it) },
                        openForTest = {viewModel.navigateToTextAudition()},
                        refresh = { viewModel.refresh() },
                        closeHealthAlert = { viewModel.closeHealthDialog() },
                        onPersonalReportSaveClick = { performanceMeasure, daysComparisonEnumIndex -> viewModel.savePersonalReport(performanceMeasure, daysComparisonEnumIndex) }
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

        TestType.entries.forEach {
            setFragmentResultListener(it.label) { key, bundle ->
                val needRefresh = bundle.getBoolean(TEST_FINISHED)
                if (needRefresh) viewModel.refresh()
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
