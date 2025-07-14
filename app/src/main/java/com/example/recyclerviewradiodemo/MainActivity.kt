package com.example.recyclerviewradiodemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.recyclerviewradiodemo.adapters.MainRvAdapter
import com.example.recyclerviewradiodemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Optional: use WindowCompat for modern edge-to-edge layout
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set up RecyclerView with data
        binding.rvMain.adapter = MainRvAdapter(SampleData.mainmenucollections)
    }
}