package com.example.diplomapplication.ui.rufie_screen

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
import com.example.diplomapplication.data.TestType
import com.example.diplomapplication.ui.theme.DiplomApplicationTheme
import com.example.diplomapplication.util.HEART_RATE_BUNDLE
import com.example.diplomapplication.util.PPG_FRAGMENT_REQUEST_KEY
import com.example.diplomapplication.util.TEST_FINISHED
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class RufieFragment : Fragment() {

    private val viewModel: RufieScreenViewModel by viewModel()
    private var tts: TextToSpeech? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel.navController = findNavController()
        viewModel.showSnack = {
            Snackbar
                .make(requireView(), getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG)
                .show()
        }
        viewModel.str = { getText(it).toString() }

        var wasAnnounced = false
        setFragmentResultListener(PPG_FRAGMENT_REQUEST_KEY) { key, bundle ->
            viewModel.updateHeartRateText(bundle.getInt(HEART_RATE_BUNDLE).toString())
            wasAnnounced = true
        }

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("ru", "RU")
                viewModel.tts = tts
                viewModel.str(R.string.rufie_explanation_pre_rest).let {
                    viewModel.say(R.string.rufie_explanation_pre_rest)
                }
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    RufieScreen(
                        uiState = viewModel.uiState.collectAsState().value,
                        closeScreen = viewModel::close,
                        mainButtonClick = viewModel::onMainButtonClicked,
                        onHeartRateChange = viewModel::updateHeartRateText,
                        openPPG = viewModel::openPPG,
                        openECG = viewModel::openECG,
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop(); tts?.shutdown()
        setFragmentResult(TestType.RUFIE.label, bundleOf(TEST_FINISHED to viewModel.isFinished.value))
    }
}

