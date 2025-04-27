package com.example.diplomapplication.ui.reactions_screen

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

class ReactionsFragment : Fragment() {

    private val viewModel: ReactionsScreenViewModel by viewModel()
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
                viewModel.speakAndSet(R.string.reactions_visual_ready) { it }
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    ReactionsScreen(
                        uiState = viewModel.uiState.collectAsState().value,
                        onStart = viewModel::onStartClicked,
                        onTap = viewModel::onScreenTap,
                        onFinish = viewModel::onFinishClicked,
                        onBack = viewModel::close
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
