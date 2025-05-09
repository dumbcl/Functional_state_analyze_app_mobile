package com.example.diplomapplication.ui.strup_screen

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.diplomapplication.R
import com.example.diplomapplication.data.TestType
import com.example.diplomapplication.ui.theme.DiplomApplicationTheme
import com.example.diplomapplication.util.TEST_FINISHED
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale
import kotlin.coroutines.resume

class StrupFragment : Fragment() {

    private val vm: StrupScreenViewModel by viewModel()
    private var tts: TextToSpeech? = null

    /* один launcher на всё время жизни view */
    private lateinit var speechLauncher: ActivityResultLauncher<Intent>
    private var speechCallback: ((String?) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        speechLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { res ->
            val txt = res.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            speechCallback?.invoke(txt)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        vm.navController = findNavController()
        vm.showSnack = {
            Snackbar
                .make(requireView(), getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG)
                .show()
        }
        vm.str = { getText(it).toString() }
        vm.requestSpeech = { awaitSpeechText() }

        tts = TextToSpeech(requireContext()) {
            if (it == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
                vm.tts = tts
                vm.uiState.value.description ?: vm.speakAndSet(R.string.strup_description) { it }
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    StrupScreen(
                        uiState = vm.uiState.collectAsState().value,
                        start = vm::onStartClicked,
                        finish = vm::onFinishClicked,
                        back = vm::close
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop(); tts?.shutdown()
        vm.requestSpeech = null
        setFragmentResult(TestType.STRUP.label, bundleOf(TEST_FINISHED to vm.isFinished.value))
    }

    /* ----------------  suspending speech --------------- */

    private suspend fun awaitSpeechText(): String? = suspendCancellableCoroutine { cont ->
        speechCallback = { txt ->
            cont.resume(txt)
            speechCallback = null
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.strup_say_color_prompt))
        }
        speechLauncher.launch(intent)
        cont.invokeOnCancellation { speechCallback = null }
    }
}
