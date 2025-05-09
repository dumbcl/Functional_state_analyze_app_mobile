package com.example.diplomapplication.ui.escal_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.diplomapplication.R
import com.example.diplomapplication.ui.theme.DiplomApplicationTheme
import com.example.diplomapplication.util.TEST_FINISHED
import com.example.diplomapplication.data.TestType
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class EscalFragment : Fragment()  {

    private val viewModel: EscalScreenViewModel by viewModel()

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

        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    EscalScreen(
                        uiState = viewModel.uiState.collectAsState().value,
                        closeStartAlert = { viewModel.closeStartAlert() },
                        onBackClicked = { viewModel.setForFinish(it) },
                        closeFinishAlert = { viewModel.closeFinishAlert() },
                        finishTesting = { viewModel.finishTesting() },
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setFragmentResult(TestType.ESCAL.label, bundleOf(TEST_FINISHED to viewModel.isFinished.value))
    }
}
