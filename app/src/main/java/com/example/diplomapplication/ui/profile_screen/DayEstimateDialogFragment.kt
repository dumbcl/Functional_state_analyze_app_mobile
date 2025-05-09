package com.example.diplomapplication.ui.profile_screen

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.example.diplomapplication.ui.theme.DiplomApplicationTheme
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.diplomapplication.R
import com.example.diplomapplication.data.DayEstimate

class DayEstimateDialogFragment: BottomSheetDialogFragment() {

    companion object {
        private const val ITEM_KEY = "item_key"

        fun newInstance(item: DayEstimate): DayEstimateDialogFragment {
            return DayEstimateDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ITEM_KEY, item)
                }
            }
        }
    }

    private var item: DayEstimate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = arguments?.getParcelable(ITEM_KEY)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    DayEstimateDialogScreen(
                        item = item
                    )
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}
