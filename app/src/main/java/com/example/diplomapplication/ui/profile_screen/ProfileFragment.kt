package com.example.diplomapplication.ui.profile_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.diplomapplication.ui.theme.DiplomApplicationTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class ProfileFragment : Fragment()  {

    private val viewModel: ProfileScreenViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val navController = findNavController()
        viewModel.navController = navController

        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    ProfileScreen(
                        uiState = viewModel.uiState.collectAsState().value,
                        openMainScreen = { viewModel.navigateToMainScreen() },
                        onDayClick = {
                            val dialog = DayEstimateDialogFragment.newInstance(viewModel.uiState.value.dayEstimates[it])
                            dialog.show(parentFragmentManager, "DayEstimateDialog")
                        },
                        refresh = { viewModel.refresh() }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init()
    }
}
