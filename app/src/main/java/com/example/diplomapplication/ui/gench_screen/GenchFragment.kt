package com.example.diplomapplication.ui.gench_screen

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
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.diplomapplication.R
import com.example.diplomapplication.ui.theme.DiplomApplicationTheme
import com.example.diplomapplication.util.TEST_FINISHED
import com.example.diplomapplication.data.TestType
import com.example.diplomapplication.util.HEART_RATE_BUNDLE
import com.example.diplomapplication.util.PPG_FRAGMENT_REQUEST_KEY
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale
import kotlin.getValue

class GenchFragment: Fragment() {

    private val viewModel: GenchScreenViewModel by viewModel()
    private var tts: TextToSpeech? = null

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

        var wasAnnounced = false
        setFragmentResultListener(PPG_FRAGMENT_REQUEST_KEY) { key, bundle ->
            viewModel.updateHeartRateText(bundle.getInt(HEART_RATE_BUNDLE).toString())
            wasAnnounced = true
        }

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("ru", "RU")
                viewModel.tts = tts
                if (wasAnnounced.not()) viewModel.updateTextToSpeak(getText(R.string.shtange_explanation_pre_exp).toString())
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    GenchScreen(
                        closeScreen = { viewModel.close() },
                        finishTest = { viewModel.finishTest() },
                        uiState = viewModel.uiState.collectAsState().value,
                        startExperiment = { viewModel.startExperiment(it) },
                        stopExperiment = { viewModel.finishExperiment(it) },
                        openPPG = { viewModel.openPPG() },
                        openECG = { viewModel.openECG() },
                        onHeartRateChange = { viewModel.updateHeartRateText(it) },
                        changeToPreExperiment = { viewModel.changeToPreExpState(it) }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
        setFragmentResult(TestType.GENCH.label, bundleOf(TEST_FINISHED to viewModel.isFinished.value))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop()
        tts?.shutdown()
    }
}
