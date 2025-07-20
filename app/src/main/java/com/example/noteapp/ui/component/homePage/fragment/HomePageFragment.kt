package com.example.noteapp.ui.component.homePage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentHomePageBinding
import com.example.noteapp.ui.base.BaseFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomePageFragment : BaseFragmentBinding<FragmentHomePageBinding>() {

    override fun getContentViewId(): Int = R.layout.fragment_home_page

    override fun initializeViews() {

    }

    override fun registerListeners() {

    }

    override fun initializeData() {

    }

}