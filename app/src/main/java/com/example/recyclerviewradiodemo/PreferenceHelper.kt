package com.example.recyclerviewradiodemo

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class PreferenceHelper(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences("MenuChoice", Context.MODE_PRIVATE)

    // --- Text Data per Tab ---
    fun setTextData(tabIndex: String, data: String) {
        Log.d("PreferenceHelper", "Saving data: $data for tabIndex: $tabIndex")
        preferences.edit().putString("textData_$tabIndex", data).apply()
    }

    fun getTextData(tabIndex: String): String? {
        return preferences.getString("textData_$tabIndex", null)
    }

    // --- Parent Text Color ---
    fun setParentTextColorData(parentTitleWithTab: String, color: String) {
        preferences.edit().putString("parentTextColor_$parentTitleWithTab", color).apply()
    }

    fun getParentTextColorData(parentTitleWithTab: String): String? {
        return preferences.getString("parentTextColor_$parentTitleWithTab", null)
    }

    fun clearParentTextColorData() {
        val editor = preferences.edit()
        val keysToRemove = preferences.all.keys.filter { it.startsWith("parentTextColor_") }
        keysToRemove.forEach { editor.remove(it) }
        editor.apply()
    }


    fun setRadioButtonColorData(subItemTitle: String, color: String) {
        preferences.edit().putString("radioButtonColor_$subItemTitle", color).apply()
    }

    fun getRadioButtonColorData(subItemTitle: String): String? {
        return preferences.getString("radioButtonColor_$subItemTitle", null)
    }

    fun clearRadioButtonColorData() {
        val editor = preferences.edit()
        val keysToRemove = preferences.all.keys.filter { it.startsWith("radioButtonColor_") }
        keysToRemove.forEach { editor.remove(it) }
        editor.apply()
    }

    // --- Deprecated: Generic Text Color (Consider Removing) ---
    @Deprecated("Use tab-specific or parent-specific methods")
    fun setTextColorData(subItemTitle: String, color: String) {
        preferences.edit().putString("textColor_$subItemTitle", color).apply()
    }

    @Deprecated("Use tab-specific or parent-specific methods")
    fun getTextColorData(subItemTitle: String): String? {
        return preferences.getString("textColor_$subItemTitle", null)
    }

    @Deprecated("Use tab-specific or parent-specific methods")
    fun clearTextColorData() {
        val editor = preferences.edit()
        val keysToRemove = preferences.all.keys.filter { it.startsWith("textColor_") }
        keysToRemove.forEach { editor.remove(it) }
        editor.apply()
    }
}


