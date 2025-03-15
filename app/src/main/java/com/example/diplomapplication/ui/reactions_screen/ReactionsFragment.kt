package com.example.diplomapplication.ui.reactions_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.diplomapplication.ui.theme.DiplomApplicationTheme
import com.example.diplomapplication.util.TEST_FINISHED
import com.example.diplomapplication.data.TestType
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class ReactionsFragment: Fragment() {

    private val viewModel: ReactionsScreenViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val navController = findNavController()
        viewModel.navController = navController

        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    ReactionsScreen(
                        closeScreen = { viewModel.close() },
                        finishTest = { viewModel.finishTest() },
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setFragmentResult(TestType.REACTIONS.label, bundleOf(TEST_FINISHED to viewModel.isFinished.value))
    }
}
