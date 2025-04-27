package com.example.diplomapplication.ui.strup_screen

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.diplomapplication.R
import com.example.diplomapplication.ui.theme.DiplomApplicationTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class StrupFragment : Fragment() {

    private val viewModel: StrupScreenViewModel by viewModel()
    private var tts: TextToSpeech? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel.navController = findNavController()
        viewModel.str = { getText(it).toString() }

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
                viewModel.tts = tts
                val intro = getText(R.string.strup_description).toString()
                viewModel.uiState.value.text ?: viewModel.updateText(intro)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    StrupScreen(
                        uiState = viewModel.uiState.collectAsState().value,
                        start = viewModel::onStartClicked,
                        finish = viewModel::onFinishClicked,
                        back = viewModel::close
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop(); tts?.shutdown()
    }
}
