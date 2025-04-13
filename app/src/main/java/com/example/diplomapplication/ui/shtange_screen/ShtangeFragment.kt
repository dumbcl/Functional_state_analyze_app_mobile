package com.example.diplomapplication.ui.shtange_screen

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.diplomapplication.R
import com.example.diplomapplication.ui.theme.DiplomApplicationTheme
import com.example.diplomapplication.util.TEST_FINISHED
import com.example.diplomapplication.data.TestType
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale
import kotlin.getValue

class ShtangeFragment: Fragment() {

    private val viewModel: ShtangeScreenViewModel by viewModel()
    private var tts: TextToSpeech? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val navController = findNavController()
        viewModel.navController = navController

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
                viewModel.tts = tts
            }
        }
        viewModel.updateTextToSpeak(getText(R.string.shtange_explanation_pre_exp).toString())

        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    ShtangeScreen(
                        closeScreen = { viewModel.close() },
                        finishTest = { viewModel.finishTest() },
                        uiState = viewModel.uiState.collectAsState().value,
                        startExperiment = { viewModel.startExperiment() },
                        stopExperiment = { viewModel.finishExperiment() },
                        openPPG = { viewModel.openPPG() },
                        onHeartRateChange = { viewModel.updateHeartRateText(it) },
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setFragmentResult(TestType.SHNTANGE.label, bundleOf(TEST_FINISHED to viewModel.isFinished.value))
    }
}
