package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.databinding.FragmentDialogPurchaseBinding

class PurchaseDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDialogPurchaseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogPurchaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int = R.style.TransparentBottomSheetStyle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragment = PurchasesFragment()
        childFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)

            val screenHeight = resources.displayMetrics.heightPixels
            val desiredHeight = (screenHeight * 0.9).toInt() // 90% of screen height

            it.layoutParams.height = desiredHeight
            it.requestLayout()

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
