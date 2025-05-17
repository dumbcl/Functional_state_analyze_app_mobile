package com.example.diplomapplication.ui.ppg_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import com.example.diplomapplication.ui.theme.DiplomApplicationTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import java.util.Locale
import com.example.diplomapplication.R
import com.example.diplomapplication.util.HEART_RATE_BUNDLE
import com.example.diplomapplication.util.PPG_FRAGMENT_REQUEST_KEY

class PPGFragment : Fragment()  {

    private val viewModel: PPGScreenViewModel by viewModel()
    private var tts: TextToSpeech? = null

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                viewModel.updateCameraPermission(true)
            } else {
                viewModel.updateCameraPermission(false)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val navController = findNavController()
        viewModel.navController = navController

        checkCameraPermission()
        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("ru", "RU")
                viewModel.updateTextToSpeak(getText(R.string.ppg_first_step).toString())
                tts?.speak(getText(R.string.ppg_first_step).toString(), TextToSpeech.QUEUE_FLUSH, null, null)
                observeTextToSpeak()
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    PPGScreen(
                        uiState = viewModel.uiState.collectAsState().value,
                        startRecord = { viewModel.startRecord() },
                        close = { viewModel.close() },
                        closeAlert = { viewModel.closeFinishAlert() },
                        updateHeartRate = { viewModel.updateHeartRate(it) },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkCameraPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop()
        tts?.shutdown()
        setFragmentResult(PPG_FRAGMENT_REQUEST_KEY, bundleOf(HEART_RATE_BUNDLE to viewModel.heartRate))
    }

    private fun observeTextToSpeak() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                val text = when {
                    state.timerText == "0" -> {
                        viewModel.updateTextToSpeak(getText(R.string.ppg_start_anonce).toString())
                        getText(R.string.ppg_start_anonce).toString()
                    }
                    state.timerText == "8" -> {
                        viewModel.updateTextToSpeak(getString(R.string.ppg_continue))
                        getString(R.string.ppg_continue)
                    }
                    state.timerText == "30" -> {
                        viewModel.updateTextToSpeak(getString(R.string.ppg_finish_anonce))
                        getString(R.string.ppg_finish_anonce)
                    }
                    state.isFinishAlertVisible -> {
                        viewModel.updateTextToSpeak(getString(R.string.ppg_first_step))
                        getString(R.string.ppg_alert_explanation, (viewModel.heartRate ?: 0).toString())
                    }
                    else -> null
                }
                if (!text.isNullOrBlank()) {
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    }

    private fun checkCameraPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.updateCameraPermission(true)
        } else {
            viewModel.updateCameraPermission(false)
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

}
