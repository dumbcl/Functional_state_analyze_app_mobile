package com.example.diplomapplication.ui.reactions_screen

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.diplomapplication.R
import com.example.diplomapplication.data.ReactionsTestResults
import com.example.diplomapplication.data.TestsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class ReactionsScreenViewModel(
    private val repo: TestsRepository,
) : ViewModel() {

    lateinit var navController: NavController
    lateinit var str: (Int) -> String
    var tts: TextToSpeech? = null

    private val _uiState = MutableStateFlow(ReactionsScreenState())
    val uiState = _uiState.asStateFlow()

    private val visualPairs = mutableListOf<Pair<Long, Long>>()
    private val audioPairs  = mutableListOf<Pair<Long, Long>>()

    private val handler = Handler(Looper.getMainLooper())
    private var testStartWall = 0L
    private var stimulusSchedule = emptyList<Long>()
    private var nextStimulusIdx = 0
    private var currentStimulusWall: Long? = null

    private enum class Mode { VISUAL, AUDIO }
    private var runningMode = Mode.VISUAL

    private var mediaPlayer: MediaPlayer? = null
    private fun playBeep() {
        val ctx = navController.context.applicationContext
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(ctx, R.raw.beep_short)?.apply {
                setOnCompletionListener { /* держим плеер созданным — только перематываем */ }
            }
        }
        mediaPlayer?.let {
            if (it.isPlaying) it.pause()
            it.seekTo(0)
            it.start()
        }
    }

    fun onStartClicked() = when (_uiState.value.screenState) {
        ReactionsScreenState.ScreenState.VISUAL_READY -> startTest(Mode.VISUAL)
        ReactionsScreenState.ScreenState.AUDIO_READY  -> startTest(Mode.AUDIO)
        else -> Unit
    }

    fun onScreenTap() {
        val stimWall = currentStimulusWall ?: return
        val list = if (runningMode == Mode.VISUAL) visualPairs else audioPairs
        list.add(stimWall to System.currentTimeMillis())
        currentStimulusWall = null
        if (runningMode == Mode.VISUAL) _uiState.update { it.copy(flashColor = null) }
    }

    fun onFinishClicked() {
        viewModelScope.launch {
            repo.sendReactionsTestResults(
                ReactionsTestResults(
                    visual = visualPairs,
                    audio = audioPairs
                )
            )
        }
        close()
    }

    fun close() {
        handler.removeCallbacksAndMessages(null)
        mediaPlayer?.release()
        tts?.stop()
        navController.popBackStack()
    }

    private fun startTest(mode: Mode) {
        runningMode = mode
        testStartWall = System.currentTimeMillis()
        stimulusSchedule = generateSchedule()
        nextStimulusIdx = 0
        currentStimulusWall = null

        _uiState.update {
            it.copy(
                screenState = if (mode == Mode.VISUAL)
                    ReactionsScreenState.ScreenState.VISUAL_RUNNING
                else
                    ReactionsScreenState.ScreenState.AUDIO_RUNNING,
                secondsLeft = null,
                flashColor = null,
                text = null
            )
        }
        handler.post(timerRunnable)
    }

    private fun generateSchedule(): List<Long> =
        List(30) { Random.nextLong(0, 120_000) }
            .distinct()
            .sorted()

    private val timerRunnable = object : Runnable {
        override fun run() {
            val elapsed = System.currentTimeMillis() - testStartWall
            val left = (120_000L - elapsed).coerceAtLeast(0)
            _uiState.update {
                it.copy(
                    secondsLeft = "%02d:%02d".format(left / 1000 / 60, left / 1000 % 60)
                )
            }

            while (nextStimulusIdx < stimulusSchedule.size &&
                elapsed >= stimulusSchedule[nextStimulusIdx]
            ) {
                fireStimulus()
                nextStimulusIdx++
            }

            if (left > 0) handler.postDelayed(this, 100) else finishPhase()
        }
    }

    private fun fireStimulus() {
        currentStimulusWall = System.currentTimeMillis()
        when (runningMode) {
            Mode.VISUAL -> _uiState.update { it.copy(flashColor = Color.Red) }
            Mode.AUDIO  -> playBeep()
        }
    }

    private fun finishPhase() {
        _uiState.update { it.copy(flashColor = null) }
        handler.removeCallbacks(timerRunnable)

        val nextState: ReactionsScreenState.ScreenState
        val textRes: Int
        if (runningMode == Mode.VISUAL) {
            nextState = ReactionsScreenState.ScreenState.AUDIO_READY
            textRes = R.string.reactions_audio_ready
        } else {
            nextState = ReactionsScreenState.ScreenState.FINISH_WAIT
            textRes = R.string.reactions_finish_prompt
        }
        speakAndSet(textRes) { it.copy(screenState = nextState) }
    }

    fun speakAndSet(res: Int, updater: (ReactionsScreenState) -> ReactionsScreenState) {
        val s = str(res)
        _uiState.update { updater(it).copy(text = s) }
        tts?.speak(s, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}
