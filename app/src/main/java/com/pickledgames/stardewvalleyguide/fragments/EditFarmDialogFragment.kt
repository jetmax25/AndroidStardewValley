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
import com.pickledgames.stardewvalleyguide.enums.FarmType
import com.pickledgames.stardewvalleyguide.interfaces.OnFarmUpdatedListener
import com.pickledgames.stardewvalleyguide.models.Farm
import kotlinx.android.synthetic.main.dialog_fragment_edit_farm.*

class EditFarmDialogFragment : DialogFragment() {

    private lateinit var containerView: View
    override fun getView(): View? {
        return containerView
    }

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
        containerView = LayoutInflater.from(activity).inflate(R.layout.dialog_fragment_edit_farm, null)
        return builder
                .setView(containerView)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val name = edit_farm_name_text_input_edit_text?.text.toString()
                    val farmType = edit_farm_type_spinner?.selectedItem as FarmType
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
        edit_farm_name_text_input_edit_text?.setText(farm.name)
        edit_farm_name_text_input_edit_text?.setSelection(farm.name.length)

        ArrayAdapter<FarmType>(
                activity as MainActivity,
                android.R.layout.simple_spinner_item,
                FarmType.values()
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            edit_farm_type_spinner.adapter = it
        }

        edit_farm_delete_button.setOnClickListener {
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
