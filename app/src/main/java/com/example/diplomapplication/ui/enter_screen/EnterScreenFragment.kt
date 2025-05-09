package com.example.diplomapplication.ui.enter_screen

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.diplomapplication.ui.enter_screen.elements.EnterScreen
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
import com.example.diplomapplication.R

class EnterScreenFragment : Fragment() {

    private val viewModel: EnterViewModel by viewModel()

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
        //val authToken = requireContext().getSharedPreferences("APP_SHARED_PREFERENCES", Context.MODE_PRIVATE).getString("AUTH_TOKEN", "")
        //if (authToken.isNullOrEmpty().not()) navController.navigate(EnterScreenFragmentDirections.actionEnterFragmentToMainFragment())

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        EnterScreen(
                            uiState = viewModel.uiState.collectAsState().value,
                            toStartForm = viewModel::toStartForm,
                            toLoginForm = viewModel::toLoginForm,
                            toRegistrationForm = viewModel::toRegisterForm,
                            login = viewModel::login,
                            register = viewModel::register,
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}
