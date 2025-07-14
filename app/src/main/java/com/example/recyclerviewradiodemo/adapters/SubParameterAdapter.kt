package com.example.recyclerviewradiodemo.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewradiodemo.R
import com.example.recyclerviewradiodemo.databinding.SubParameterBinding
import com.example.recyclerviewradiodemo.models.SharedViewModel
import com.example.recyclerviewradiodemo.models.SubParameterModel

class SubParameterAdapter(
    private val subParameterModel: List<SubParameterModel>,
    private val subAdapterListener: Any,
    private val parentCollectionTitle: String,
    private val sharedViewModel: SharedViewModel
) : RecyclerView.Adapter<SubParameterAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var selectedRadioButton: RadioButton? = null
    private val clickData = mutableMapOf<String, MutableList<Int>>()
    private val textColorData = mutableMapOf<String, Int>()
    private var isInitialLoadComplete = false

    interface UpdateListener {
        fun onSubItemUpdated(
            parentCollectionTitle: String,
            currentSelections: Map<String, List<Int>>
        )
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = SubParameterBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.sub_parameter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subItem = subParameterModel[position]
        val subItemTitle = subItem.parameters
        val binding = holder.binding

        binding.apply {
            tvSubItemTitle.text = subItemTitle
            radioButton.tag = subItemTitle

            // Set initial text color
            val savedTextColor = sharedViewModel.getTextColorData(subItemTitle)
            val initialTextColor = when (savedTextColor) {
                "orange" -> ContextCompat.getColor(context, R.color.orange)
                "teal" -> ContextCompat.getColor(context, R.color.teal)
                else -> ContextCompat.getColor(context, R.color.teal)
            }
            tvSubItemTitle.setTextColor(initialTextColor)
            textColorData[subItemTitle] = initialTextColor

            // Set initial radio button color and checked state
            val savedRadioColor = sharedViewModel.getRadioButtonColorData(subItemTitle)
            val isInitiallyChecked = savedRadioColor == "orange"
            val initialRadioColor = when (savedRadioColor) {
                "orange" -> ContextCompat.getColor(context, R.color.orange)
                "teal" -> ContextCompat.getColor(context, R.color.teal)
                else -> ContextCompat.getColor(context, R.color.teal)
            }
            radioButton.isChecked = isInitiallyChecked
            radioButton.buttonTintList = ColorStateList.valueOf(initialRadioColor)
            setRadioButtonColor(radioButton, isInitiallyChecked, subItemTitle)

            if (isInitiallyChecked) {
                clickData[subItemTitle] = mutableListOf()
                selectedRadioButton = radioButton
            }

            radioButton.setOnClickListener {
                handleRadioButtonClick(holder, subItemTitle, radioButton, tvSubItemTitle)
            }

            if (!isInitialLoadComplete) isInitialLoadComplete = true
        }
    }

    override fun getItemCount(): Int = subParameterModel.size

    private fun handleRadioButtonClick(
        holder: ViewHolder,
        subItemTitle: String,
        radioButton: RadioButton,
        textView: TextView
    ) {
        // Deselect previous selection
        if (selectedRadioButton != null && selectedRadioButton != radioButton) {
            val prevTag = selectedRadioButton?.tag as? String ?: return
            selectedRadioButton?.isChecked = false
            setRadioButtonColor(selectedRadioButton!!, false, prevTag)
            clickData.remove(prevTag)

            val prevPosition = subParameterModel.indexOfFirst { it.parameters == prevTag }
            if (prevPosition != RecyclerView.NO_POSITION && prevPosition >= 0) {
                val parent = holder.itemView.parent as? RecyclerView
                val prevHolder = parent?.findViewHolderForAdapterPosition(prevPosition) as? ViewHolder
                prevHolder?.binding?.tvSubItemTitle?.let {
                    updateTextColor(it, prevTag)
                }
            }
        }

        // Toggle current selection
        if (!clickData.containsKey(subItemTitle)) {
            clickData[subItemTitle] = mutableListOf()
            radioButton.isChecked = true
            updateTextColor(textView, subItemTitle)
            setRadioButtonColor(radioButton, true, subItemTitle)
        } else {
            clickData.remove(subItemTitle)
            radioButton.isChecked = false
            updateTextColor(textView, subItemTitle)
            setRadioButtonColor(radioButton, false, subItemTitle)
        }

        selectedRadioButton = radioButton
        selectedRadioButton?.tag = subItemTitle

        val selectedTitles = clickData.keys.toList()
        sharedViewModel.setSelectedSubItemsForParent(parentCollectionTitle, selectedTitles)
        (subAdapterListener as? UpdateListener)?.onSubItemUpdated(parentCollectionTitle, clickData)
    }

//    fun resetSelections() {
//        val hadSelections = clickData.isNotEmpty()
//        clickData.clear()
//
//        subParameterModel.forEach { subItem ->
//            // Clear SharedPrefs if needed
//            // sharedViewModel.clearSubItemTextColorsForParent(...)
//        }
//
//        if (hadSelections) {
//            notifyDataSetChanged()
//        }
//    }

    private fun setRadioButtonColor(
        radioButton: RadioButton,
        isSelected: Boolean,
        subItemTitle: String
    ) {
        val colorRes = if (isSelected) R.color.orange else R.color.teal
        val color = ContextCompat.getColor(context, colorRes)
        radioButton.buttonTintList = ColorStateList.valueOf(color)
        saveRadioButtonColorToPreferences(subItemTitle, color)
    }

    private fun updateTextColor(textView: TextView, subItemTitle: String) {
        val color = if (clickData.containsKey(subItemTitle)) {
            ContextCompat.getColor(textView.context, R.color.orange)
        } else {
            ContextCompat.getColor(textView.context, R.color.black)
        }
        textView.setTextColor(color)
        textColorData[subItemTitle] = color
        saveTextColorToPreferences(subItemTitle, color)
    }

    private fun saveRadioButtonColorToPreferences(subItemTitle: String, color: Int) {
        val colorString = when (color) {
            ContextCompat.getColor(context, R.color.orange) -> "orange"
            ContextCompat.getColor(context, R.color.teal) -> "teal"
            else -> "unknown"
        }
        sharedViewModel.setRadioButtonColorData(subItemTitle, colorString)
    }

    private fun saveTextColorToPreferences(subItemTitle: String, color: Int) {
        val colorString = when (color) {
            ContextCompat.getColor(context, R.color.orange) -> "orange"
            ContextCompat.getColor(context, R.color.teal) -> "teal"
            else -> "unknown"
        }
        sharedViewModel.setTextColorData(subItemTitle, colorString)
    }
}