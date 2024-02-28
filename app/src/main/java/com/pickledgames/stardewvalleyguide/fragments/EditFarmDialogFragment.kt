package com.pickledgames.stardewvalleyguide.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.databinding.DialogFragmentEditFarmBinding
import com.pickledgames.stardewvalleyguide.enums.FarmType
import com.pickledgames.stardewvalleyguide.interfaces.OnFarmUpdatedListener
import com.pickledgames.stardewvalleyguide.models.Farm

class EditFarmDialogFragment : DialogFragment() {

    private lateinit var containerView: View
    private lateinit var binding: DialogFragmentEditFarmBinding
    override fun getView(): View = binding.root

    private lateinit var farm: Farm
    private var position: Int = -1
    private lateinit var onFarmUpdatedListener: OnFarmUpdatedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onFarmUpdatedListener = context as OnFarmUpdatedListener
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity as MainActivity)
        binding = DialogFragmentEditFarmBinding.inflate(LayoutInflater.from(context))
        containerView = binding.root
        return builder
                .setView(containerView)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val name = binding.editFarmNameTextInputEditText.text.toString()
                    val farmType = binding.editFarmTypeSpinner.selectedItem as FarmType
                    val newFarm = Farm(name, farmType, farm.communityCenterItems, farm.fishes, farm.museumItems, farm.id)
                    onFarmUpdatedListener.onFarmUpdated(newFarm, position)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments != null) {
            farm = arguments?.getParcelable(FARM) ?: Farm("Unnamed", FarmType.Standard)
            position = arguments?.getInt(POSITION) ?: -1
            setup()
        }
    }

    private fun setup() {
        with (binding) {
            editFarmNameTextInputEditText.setText(farm.name)
            editFarmNameTextInputEditText.setSelection(farm.name.length)

            ArrayAdapter<FarmType>(
                    activity as MainActivity,
                    android.R.layout.simple_spinner_item,
                    FarmType.values()
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                editFarmTypeSpinner.adapter = it
            }

            editFarmDeleteButton.setOnClickListener {
                AlertDialog.Builder(activity as MainActivity)
                        .setTitle(R.string.confirm_delete_title)
                        .setMessage(R.string.confirm_delete_message)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            onFarmUpdatedListener.onFarmUpdated(null, position)
                            dismiss()
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .create()
                        .show()
            }
        }
    }

    companion object {
        const val TAG = "EditFarmDialogFragment"
        private const val FARM = "FARM"
        private const val POSITION = "POSITION"

        fun newInstance(farm: Farm, position: Int): EditFarmDialogFragment {
            val editFarmDialogFragment = EditFarmDialogFragment()
            val arguments = Bundle()
            arguments.putParcelable(FARM, farm)
            arguments.putInt(POSITION, position)
            editFarmDialogFragment.arguments = arguments
            return editFarmDialogFragment
        }
    }
}
