package com.example.recyclerviewradiodemo.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.recyclerviewradiodemo.PreferenceHelper
import kotlin.collections.getOrPut

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val preferenceHelper = PreferenceHelper(application)

    // --- LiveData for Tab-Specific Text Data ---
    private val _textDataMap = mutableMapOf<String, MutableLiveData<String>>()

//    fun getTextDataLiveData(tabId: String): LiveData<String> {
//        val initialData = preferenceHelper.getTextData(tabId) ?: ""
//        return _textDataMap.getOrPut(tabId) { MutableLiveData(initialData) }
//    }

    fun setTextData(tabIndex: String, data: String) {
        preferenceHelper.setTextData(tabIndex, data)
        _textDataMap.getOrPut(tabIndex) { MutableLiveData() }.value = data
    }

    fun getTextData(tabIndex: String): String? {
        return preferenceHelper.getTextData(tabIndex)
    }

    // --- Parent Text Color ---
    fun setParentTextColorData(parentTitleWithTab: String, color: String) {
        preferenceHelper.setParentTextColorData(parentTitleWithTab, color)
    }

    fun getParentTextColorData(parentTitleWithTab: String): String? {
        return preferenceHelper.getParentTextColorData(parentTitleWithTab)
    }

    fun clearParentTextColorData() {
        preferenceHelper.clearParentTextColorData()
    }

     //--- Sub-Item Text Color ---
//    fun setSubItemTextColorData(tabId: String, parentTitle: String, subItemTitle: String, color: String) {
//        preferenceHelper.setSubItemTextColorData(tabId, parentTitle, subItemTitle, color)
//    }
//
//    fun getSubItemTextColorData(tabId: String, parentTitle: String, subItemTitle: String): String? {
//        return preferenceHelper.getSubItemTextColorData(tabId, parentTitle, subItemTitle)
//    }
//
//    fun clearSubItemTextColorsForParent(tabId: String, parentTitle: String) {
//        preferenceHelper.clearSubItemTextColorsForParent(tabId, parentTitle)
//    }

    // --- Radio Button Text Color ---
    fun setRadioButtonColorData(subItemTitle: String, color: String) {
        preferenceHelper.setRadioButtonColorData(subItemTitle, color)
    }

    fun getRadioButtonColorData(subItemTitle: String): String? {
        return preferenceHelper.getRadioButtonColorData(subItemTitle)
    }

    fun clearRadioButtonColorData() {
        preferenceHelper.clearRadioButtonColorData()
    }

    // --- Deprecated Generic Text Color (Legacy Fallback) ---
    fun setTextColorData(subItemTitle: String, color: String) {
        preferenceHelper.setTextColorData(subItemTitle, color)
    }

    fun getTextColorData(subItemTitle: String): String? {
        return preferenceHelper.getTextColorData(subItemTitle)
    }

    fun clearTextColorData() {
        preferenceHelper.clearTextColorData()
    }

    // --- Selected Sub-Items for a Parent ---
    private fun getSelectedSubItemsPreferenceKey(parentTitle: String, tabId: String = "defaultTab"): String {
        return "selected_sub_items_${tabId}_$parentTitle"
    }

    fun setSelectedSubItemsForParent(
        parentTitle: String,
        selectedSubItemTitles: List<String>,
        tabId: String = "defaultTab"
    ) {
        val key = getSelectedSubItemsPreferenceKey(parentTitle, tabId)
        val dataToSave = selectedSubItemTitles.joinToString(",")
        preferenceHelper.setTextData(key, dataToSave)
    }


}




