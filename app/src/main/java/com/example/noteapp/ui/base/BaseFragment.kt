package com.example.noteapp.ui.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : BaseView, Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(getContentViewId(), container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        initializeData()
        registerListeners()
    }

    fun isActive() = isAdded && activity != null

    fun getActivitySafety(action: (Activity) -> Unit) {
        if (isActive()) action(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}