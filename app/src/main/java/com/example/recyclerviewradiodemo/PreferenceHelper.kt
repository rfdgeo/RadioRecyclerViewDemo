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

    // --- Sub-Item Text Color ---
    fun setSubItemTextColorData(tabId: String, parentTitle: String, subItemTitle: String, color: String) {
        val key = "subItemTextColor_${tabId}_${parentTitle}_$subItemTitle"
        preferences.edit().putString(key, color).apply()
        Log.d("PreferenceHelper", "Saving sub-item color for key $key: $color")
    }

    fun getSubItemTextColorData(tabId: String, parentTitle: String, subItemTitle: String): String? {
        val key = "subItemTextColor_${tabId}_${parentTitle}_$subItemTitle"
        return preferences.getString(key, null)
    }

    fun clearSubItemTextColorsForParent(tabId: String, parentTitle: String) {
        val editor = preferences.edit()
        val prefix = "subItemTextColor_${tabId}_${parentTitle}_"
        val keysToRemove = preferences.all.keys.filter { it.startsWith(prefix) }
        keysToRemove.forEach {
            editor.remove(it)
            Log.d("PreferenceHelper", "Removing sub-item text color key: $it")
        }
        editor.apply()
        Log.d("PreferenceHelper", "Cleared sub-item text colors for parent $parentTitle on tab $tabId.")
    }

    // --- Radio Button Color ---
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


