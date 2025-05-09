package com.example.diplomapplication.ui.text_audition_screen

import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.Locale
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import android.Manifest

class TextAuditionFragment : Fragment() {

    private val vm: TextAuditionScreenViewModel by viewModel()
    private var tts: TextToSpeech? = null

    /* ---------- permission launcher ---------- */
    private lateinit var micPermLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        micPermLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            micPermContinuation?.resume(granted)    // продолжим suspend-функцию
            micPermContinuation = null
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
        vm.requestRecord = { startVoiceRecording() }

        tts = TextToSpeech(requireContext()) {
            if (it == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
                vm.tts = tts
                vm.initSpeak()
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    TextAuditionScreen(
                        uiState = vm.uiState.collectAsState().value,
                        actions = object : TextAuditionActions {
                            override fun start() = vm.onStartClicked()
                            override fun startReading() = vm.onStartRecordReading()
                            override fun continueAfterReading() = vm.onContinueClicked()
                            override fun listeningDone() = vm.onListeningFinished()
                            override fun startRepeating() = vm.onStartRepeatRecording()
                            override fun finish() = vm.onFinishClicked()
                            override fun back() = vm.close()
                            override fun finishRecord() = vm.finishRecord
                            override fun startListening() = vm.onStartListening()
                        }
                    )
                }
            }
        }
    }

    /* ---------- onCreateView как у вас (сократил) ---------- */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.loadTexts()
    }

    /* ---------------- Voice-record «правильный» ---------------- */

    /** suspend-функция, которую ViewModel вызывает через vm.requestRecord */
    private suspend fun startVoiceRecording(): String? = withContext(Dispatchers.IO) {
        // 1. Проверяем / запрашиваем разрешение
        if (!ensureMicPermission()) return@withContext null   // мой Snack уже покажет

        suspendCancellableCoroutine { cont ->
            val file = File(
                requireContext().cacheDir,
                "audition_${System.currentTimeMillis()}.m4a"
            )

            val recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }

            vm.finishRecord = {
                try { recorder.stop() } catch (_: Exception) {}
                recorder.release()
                cont.resume(file.absolutePath)
                vm.onRecordFinished()
            }

            cont.invokeOnCancellation {
                try { recorder.stop() } catch (_: Exception) {}
                recorder.release()
                cont.resume(file.absolutePath)
            }
        }
    }

    /* ---------- permission helper ---------- */

    private var micPermContinuation: Continuation<Boolean>? = null

    private suspend fun ensureMicPermission(): Boolean {
        // уже есть?
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) return true

        // спросим
        return suspendCancellableCoroutine { cont ->
            micPermContinuation = cont
            micPermLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }.also { granted ->
            if (!granted) {
                Snackbar.make(
                    requireView(),
                    R.string.micro_permission_denied,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop(); tts?.shutdown()
        vm.requestRecord = null
        setFragmentResult(TestType.TEXT_AUDITION.label, bundleOf(TEST_FINISHED to vm.isFinished.value))
    }
}

