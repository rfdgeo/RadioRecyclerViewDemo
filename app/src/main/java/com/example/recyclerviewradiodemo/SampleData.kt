package com.example.recyclerviewradiodemo


import com.example.recyclerviewradiodemo.models.MainMenuModel
import com.example.recyclerviewradiodemo.models.ParentParameterModel
import com.example.recyclerviewradiodemo.models.SubParameterModel

object SampleData {

    // collections

    // parameter collections
    private var musclegroup = listOf(
        SubParameterModel("Chest", 1.0, "Chest"),
        SubParameterModel("Back", 2.0, "Back"),
        SubParameterModel("Legs", 3.0, "Legs"),
        SubParameterModel("Shoulders", 4.0, "Shoulders"),
        SubParameterModel("Arms", 5.0, "Arms"),

    )

    // parameter collections
    private var workoutdurations = listOf(
        SubParameterModel("Quick (5–15 mins)", 1.0, "Quick (5–15 mins)"),
        SubParameterModel("Moderate (20–30 mins)", 2.0, "Moderate (20–30 mins)"),
        SubParameterModel("Intense (45+ mins)", 3.0, "Intense (45+ mins)"),

    )
    // parameter collections
    private var fitnesslevels = listOf(
        SubParameterModel("Beginner", 1.0, "Beginner"),
        SubParameterModel("Intermediate", 2.0, "Intermediate"),
        SubParameterModel("Advanced", 3.0, "Advanced"),

            )

    private var exercise = listOf(
        SubParameterModel("None", 0.0, "None"),
        SubParameterModel("Mild", 1.0, "Mild"),
        SubParameterModel("Medium", 2.0, "Medium"),
        SubParameterModel("Hard", 3.0, "Hard"),

    )



    val parametercollections = listOf(
        ParentParameterModel("Muscle Group", R.drawable.armmuscle, subParameterModel = musclegroup),
        ParentParameterModel("Workout Durations", R.drawable.duration, subParameterModel = workoutdurations),
        ParentParameterModel("Fitness Levels", R.drawable.dumbbell, subParameterModel = fitnesslevels),
        ParentParameterModel("Exercise Type", R.drawable.exercisetype, subParameterModel = exercise),


        )
    // Main Menu
    val mainmenucollections = listOf(
        MainMenuModel(R.drawable.main, "Parameters"),


    )


}