package com.example.recyclerviewradiodemo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewradiodemo.R
import com.example.recyclerviewradiodemo.activities.ParameterActivity
import com.example.recyclerviewradiodemo.databinding.ParentParameterBinding
import com.example.recyclerviewradiodemo.models.ParentParameterModel
import com.example.recyclerviewradiodemo.models.SharedViewModel

class ParentParameterAdapter(
    private var parameterCollections: List<ParentParameterModel>,
    private val listener: UpdateListener,
    private val sharedViewModel: SharedViewModel,
    private val context: Context
) : RecyclerView.Adapter<ParentParameterAdapter.CollectionsViewHolder>() {

    interface UpdateListener {
        fun onSubItemUpdated(collectionTitle: String, subItemSelections: Map<String, List<Int>>)
        fun onParentItemClickedForFullScreenToggle(position: Int)
    }

    private val subAdaptersMap = mutableMapOf<Int, SubParameterAdapter>()
    private val parentSelectionState = mutableMapOf<String, Boolean>()

    init {
        parameterCollections.forEach { collection ->
            val parentTitle = collection.parentParameterTitle
            val savedColorString = sharedViewModel.getParentTextColorData("textData_$parentTitle")
            parentSelectionState[parentTitle] = savedColorString == "orange"
        }
    }

    class CollectionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ParentParameterBinding = ParentParameterBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.parent_parameter, parent, false)
        return CollectionsViewHolder(view)
    }

    override fun getItemCount(): Int = parameterCollections.size

    override fun onBindViewHolder(holder: CollectionsViewHolder, position: Int) {
        val collection = parameterCollections[position]
        val parentTitle = collection.parentParameterTitle

        holder.binding.apply {
            imgList.setImageResource(collection.iconResource)
            tvParentTitle.text = parentTitle

            loadParentTextColor(tvParentTitle, parentTitle)
            displaySelectedSubItems(tvSelectedSubItems, parentTitle)

            val subItemAdapter = subAdaptersMap.getOrPut(position) {
                SubParameterAdapter(
                    collection.subParameterModel,
                    object : SubParameterAdapter.UpdateListener {
                        override fun onSubItemUpdated(
                            parentCollectionTitle: String,
                            currentSelections: Map<String, List<Int>>
                        ) {
                            listener.onSubItemUpdated(parentCollectionTitle, currentSelections)

                            val hasSelections = currentSelections.any { it.value.isNotEmpty() }
                            parentSelectionState[parentCollectionTitle] = hasSelections

                            updateParentTextColorOnSelection(
                                tvParentTitle,
                                parentCollectionTitle,
                                hasSelections
                            )
                            displaySelectedSubItems(tvSelectedSubItems, parentCollectionTitle)
                        }
                    },
                    collection.parentParameterTitle,
                    sharedViewModel
                )
            }

            rvSubItem.adapter = subItemAdapter
            rvSubItem.layoutManager = LinearLayoutManager(context)
            rvSubItem.visibility = if (collection.isExpanded) View.VISIBLE else View.GONE

            tvParentTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (collection.isExpanded) R.color.red else R.color.black
                )
            )

            root.setOnClickListener {
                collection.isExpanded = !collection.isExpanded
                notifyItemChanged(position)
                listener.onParentItemClickedForFullScreenToggle(holder.adapterPosition)
            }
        }
    }

    private fun loadParentTextColor(textView: TextView, parentTitle: String) {
        val savedColor = sharedViewModel.getParentTextColorData(parentTitle)
        val color = if (savedColor == "orange")
            ContextCompat.getColor(context, R.color.orange)
        else
            ContextCompat.getColor(context, R.color.black)

        textView.setTextColor(color)
    }

    private fun updateParentTextColorOnSelection(
        textView: TextView,
        parentTitle: String,
        hasSelections: Boolean
    ) {
        val colorRes = if (hasSelections) R.color.orange else R.color.black
        textView.setTextColor(ContextCompat.getColor(context, colorRes))

        val tabId = (context as? ParameterActivity)?.getCurrentTabId() ?: "unknown_tab"
        val colorString = if (hasSelections) "orange" else "black"
        sharedViewModel.setParentTextColorData("textData_${tabId}_$parentTitle", colorString)

        Log.d("ParentParameterAdapter", "Saved color for $parentTitle on tab $tabId: $colorString")
    }

    private fun displaySelectedSubItems(textView: TextView, parentTitle: String) {
        val sharedPreferences = context.getSharedPreferences("MenuChoice", Context.MODE_PRIVATE)
        val key = "textData_4"
        val textData = sharedPreferences.getString(key, "") ?: ""

        val selectedItems = mutableListOf<String>()
        if (textData.isNotEmpty()) {
            val parts = textData.split("\n")
            var currentSection = ""

            for (part in parts) {
                when {
                    part.contains("Muscle Group") -> currentSection = "Muscle Group"
                    part.contains("Workout Durations") -> currentSection = "Workout Durations"
                    part.contains("Fitness Levels") -> currentSection = "Fitness Levels"
                    part.contains("Exercise Type") -> currentSection = "Exercise Type"
                    else -> if (parentTitle == currentSection) selectedItems.add(part.trim())
                }
            }
        }

        val displayText = if (selectedItems.isNotEmpty())
            selectedItems.joinToString(", ")
        else
            "Select:"

        textView.text = displayText
        textView.setTextColor(
            ContextCompat.getColor(
                textView.context,
                if (selectedItems.isNotEmpty()) R.color.orange else R.color.black
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCollections(newCollections: List<ParentParameterModel>) {
        subAdaptersMap.clear()
        parentSelectionState.clear()
        parameterCollections = newCollections

        newCollections.forEach { collection ->
            val parentTitle = collection.parentParameterTitle
            val savedColorString = sharedViewModel.getParentTextColorData("textData_$parentTitle")
            parentSelectionState[parentTitle] = savedColorString == "orange"
        }

        notifyDataSetChanged()
    }
}