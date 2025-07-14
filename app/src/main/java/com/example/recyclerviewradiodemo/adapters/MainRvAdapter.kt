package com.example.recyclerviewradiodemo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewradiodemo.R
import com.example.recyclerviewradiodemo.activities.ParameterActivity
import com.example.recyclerviewradiodemo.databinding.ParentMainBinding
import com.example.recyclerviewradiodemo.models.MainMenuModel

class MainRvAdapter(private var mainmenucollections: List<MainMenuModel>) :
    RecyclerView.Adapter<MainRvAdapter.CollectionsViewHolder>() {
    private lateinit var context: Context

    class CollectionsViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

        val binding = ParentMainBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.parent_main,parent,false)
        context = parent.context

        return CollectionsViewHolder(view)
    }

    override fun getItemCount()= mainmenucollections.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CollectionsViewHolder, position: Int) {
        holder.binding.apply {
            val mainmenucollection = mainmenucollections[position]


            imageView.setImageResource(mainmenucollection.imageResId)


            mainMenuClick.setOnClickListener {

                when (mainmenucollection.imageResId) {

                    R.drawable.main -> {
                        val intent = Intent(context, ParameterActivity::class.java)
                        context.startActivity(intent)
                    }

                }
            }

        }
    }

}