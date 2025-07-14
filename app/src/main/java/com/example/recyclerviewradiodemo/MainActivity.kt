package com.example.recyclerviewradiodemo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.recyclerviewradiodemo.adapters.MainRvAdapter
import com.example.recyclerviewradiodemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.rvMain.adapter = MainRvAdapter(SampleData.mainmenucollections)

    }
}