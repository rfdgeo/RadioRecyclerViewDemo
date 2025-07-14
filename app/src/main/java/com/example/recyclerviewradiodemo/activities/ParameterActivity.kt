package com.example.recyclerviewradiodemo.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewradiodemo.MainActivity
import com.example.recyclerviewradiodemo.R
import com.example.recyclerviewradiodemo.SampleData
import com.example.recyclerviewradiodemo.adapters.ParentParameterAdapter
import com.example.recyclerviewradiodemo.databinding.ActivityParameterBinding
import com.example.recyclerviewradiodemo.models.ParentParameterModel
import com.example.recyclerviewradiodemo.models.SharedViewModel
import com.google.android.material.snackbar.Snackbar

class ParameterActivity : AppCompatActivity(), ParentParameterAdapter.UpdateListener {

    private lateinit var binding: ActivityParameterBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var parentParameterAdapter: ParentParameterAdapter

    private lateinit var btnRefresh: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView

    private lateinit var originalRvLayoutParams: ViewGroup.LayoutParams
    private lateinit var originalCollections: List<ParentParameterModel>

    private var originalRvConstraintTopToBottom: Int = -1
    private var fullScreenParentPosition: Int = -1
    private var isRecyclerViewExpanded = false
    private var parameterIndex: Int = 0
    private var selectionCount = 0

    private val aggregatedClickData = mutableMapOf<String, MutableSet<String>>()
    private val tabResultStrings = mutableMapOf<Int, String>()
    private val totalCategories = SampleData.parametercollections.size

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParameterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        parameterIndex = intent.getIntExtra("PARAMETERS", 4)
        originalCollections = SampleData.parametercollections

        setupViews()
        setupToolbar()
        setupRecyclerView()
        loadSavedData()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToMainActivity()
            }
        })
    }

    private fun setupViews() {
        tvStatus = binding.tvStatus
        btnRefresh = binding.parameterRefresh
        progressBar = binding.progressBar
        progressBar.max = totalCategories
        progressBar.progressDrawable.colorFilter =
            PorterDuffColorFilter(ContextCompat.getColor(this, R.color.orange), PorterDuff.Mode.SRC_IN)
    }

    private fun setupToolbar() {
        val toolbar = binding.toolbar
        val originalDrawable = ContextCompat.getDrawable(this, R.drawable.home)
        originalDrawable?.let {
            val width = (10 * resources.displayMetrics.density).toInt()
            val height = (10 * resources.displayMetrics.density).toInt()
            val resizedDrawable = BitmapDrawable(resources, Bitmap.createScaledBitmap((it as BitmapDrawable).bitmap, width, height, true))
            toolbar.navigationIcon = resizedDrawable
        }
        toolbar.setNavigationOnClickListener { navigateToMainActivity() }
    }

    private fun setupRecyclerView() {
        parentParameterAdapter = ParentParameterAdapter(
            originalCollections,
            this,
            sharedViewModel,
            this
        )

        binding.rvParameterMenu.adapter = parentParameterAdapter

        originalRvLayoutParams = binding.rvParameterMenu.layoutParams
        originalRvConstraintTopToBottom =
            (binding.rvParameterMenu.layoutParams as ConstraintLayout.LayoutParams).topToBottom
    }

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        navigateToMainActivity()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("IS_RV_EXPANDED", isRecyclerViewExpanded)
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onSubItemUpdated(collectionTitle: String, subItemSelections: Map<String, List<Int>>) {
        // Convert List<Int> to Set<String> for internal use
        val selectedTitles = subItemSelections.keys.toMutableSet()
        aggregatedClickData[collectionTitle] = selectedTitles

        Log.d(
            "ParameterActivity",
            "onSubItemUpdated: Data to be saved: $aggregatedClickData for collectionName: $collectionTitle"
        )

        // Update text, progress, and completion status
        updateDataTextView()
        updateProgressBar()
        checkAndUpdateCompletionStatus()

        // ✅ Collapse the parent item and full screen (if any)
//        onParentItemClickedForFullScreenToggle(-1)

        val parentToCollapse = originalCollections.find { it.parentParameterTitle == collectionTitle }
        parentToCollapse?.isExpanded = false

        if (isRecyclerViewExpanded) {
            toggleRecyclerViewExpansion()
        } else {
            // If not in full screen, refresh the adapter to reflect collapsed parent
            parentParameterAdapter.updateCollections(originalCollections)
        }

        // ✅ Show custom snackbar
        val rootView = binding.root // Use ViewBinding safely
        val snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT)

        // Inflate custom layout
        val customView = layoutInflater.inflate(R.layout.snackbar_custom, null)
        val textView = customView.findViewById<TextView>(R.id.snackbar_text)
        textView.text = "Saved: $collectionTitle"

        // Set background color from colors.xml
        customView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_orange))

        // Apply custom layout to the snackbar
        val snackbarLayout = snackbar.view as ViewGroup
        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.setBackgroundColor(Color.TRANSPARENT) // Make native bg transparent
        snackbarLayout.addView(customView, 0)

        snackbar.show()
    }


    override fun onParentItemClickedForFullScreenToggle(position: Int) {
        if (!isRecyclerViewExpanded) {
            fullScreenParentPosition = position
            if (position in originalCollections.indices) {
                originalCollections[position].isExpanded = true
            }
        }
        toggleRecyclerViewExpansion()
    }

    private fun toggleRecyclerViewExpansion() {
        isRecyclerViewExpanded = !isRecyclerViewExpanded
        if (isRecyclerViewExpanded) expandRecyclerView() else collapseRecyclerView()
    }

    private fun expandRecyclerView() {
        binding.toolbar.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.main.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        if (fullScreenParentPosition in originalCollections.indices) {
            val item = originalCollections[fullScreenParentPosition]
            item.isExpanded = true
            parentParameterAdapter.updateCollections(listOf(item))
        } else {
            Log.w("ParameterActivity", "Invalid fullScreenParentPosition: $fullScreenParentPosition")
        }

        val fullParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        ).apply {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }

        binding.rvParameterMenu.layoutParams = fullParams
        binding.rvParameterMenu.bringToFront()
        binding.rvParameterMenu.scrollToPosition(0)
    }

    private fun collapseRecyclerView() {
        // Fade out RecyclerView before collapsing
        val fadeOut = AlphaAnimation(1f, 0f).apply {
            duration = 300
            fillAfter = true
        }

        binding.rvParameterMenu.startAnimation(fadeOut)

        // Use a delayed post to wait until animation ends before actually collapsing
        binding.rvParameterMenu.postDelayed({
            binding.toolbar.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE

            parentParameterAdapter.updateCollections(originalCollections)
            fullScreenParentPosition = RecyclerView.NO_POSITION

            val previousFullScreenPosition = fullScreenParentPosition
            if (previousFullScreenPosition != RecyclerView.NO_POSITION && previousFullScreenPosition < originalCollections.size) {
                binding.rvParameterMenu.scrollToPosition(previousFullScreenPosition)
            }

            // Restore original layout
            binding.rvParameterMenu.layoutParams = originalRvLayoutParams

            val currentParams = binding.rvParameterMenu.layoutParams as ConstraintLayout.LayoutParams
            if (originalRvConstraintTopToBottom != -1) {
                currentParams.topToBottom = originalRvConstraintTopToBottom
            }
            currentParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            currentParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            currentParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            currentParams.topToTop = ConstraintLayout.LayoutParams.UNSET

            binding.rvParameterMenu.layoutParams = currentParams

            // Fade in again after reset
            val fadeIn = AlphaAnimation(0f, 1f).apply {
                duration = 1000
                fillAfter = true
            }
            binding.rvParameterMenu.startAnimation(fadeIn)

        }, 300) // Wait for fadeOut to finish (300ms)
    }

    private fun updateProgressBar() {
        if (aggregatedClickData.isNotEmpty() && selectionCount < aggregatedClickData.size) {
            selectionCount = aggregatedClickData.size
        }
        progressBar.progress = selectionCount
    }

    private fun updateDataTextView() {
        val resultString = buildString {
            for ((collection, titles) in aggregatedClickData) {
                append("$collection:\n")
                titles.forEach { append("  $it\n") }
            }
        }

        tabResultStrings[parameterIndex] = resultString
        sharedViewModel.setTextData(parameterIndex.toString(), resultString)
    }

    private fun checkAndUpdateCompletionStatus() {
        val requiredSections = mapOf(
            "Muscle Group" to 1,
            "Workout Durations" to 1,
            "Fitness Levels" to 1,
            "Exercise Type" to 1,
        )

        val allComplete = requiredSections.all { (section, count) ->
            (aggregatedClickData[section]?.size ?: 0) >= count
        }

        tvStatus.apply {
            text = if (allComplete) "Finished" else "Not Finished"
            setTextColor(
                ContextCompat.getColor(
                    this@ParameterActivity,
                    if (allComplete) R.color.green else R.color.orange
                )
            )
            visibility = View.VISIBLE
        }
    }

    private fun loadSavedData() {
        sharedViewModel.getTextData(parameterIndex.toString())?.let {
            parseSavedData(it)
            updateProgressBar()
            checkAndUpdateCompletionStatus()
        }
    }

    private fun parseSavedData(savedData: String) {
        var currentCollection = ""
        for (line in savedData.split("\n")) {
            when {
                line.endsWith(":") -> {
                    currentCollection = line.removeSuffix(":")
                    aggregatedClickData[currentCollection] = mutableSetOf()
                }

                line.startsWith("  ") -> {
                    val title = line.trim()
                    aggregatedClickData[currentCollection]?.add(title)
                }
            }
        }
    }

    fun getCurrentTabId(): String {
        return parameterIndex.toString()
    }

    fun onRefreshClicked(view: View) {
        aggregatedClickData.clear()
        updateDataTextView()
        selectionCount = 0
        updateProgressBar()

        tvStatus.visibility = View.GONE
        sharedViewModel.clearTextColorData()
        sharedViewModel.clearParentTextColorData()
        sharedViewModel.clearRadioButtonColorData()

        startActivity(Intent(this, ParameterActivity::class.java).apply {
            putExtra("PARAMETERS", parameterIndex)
        })
    }
}