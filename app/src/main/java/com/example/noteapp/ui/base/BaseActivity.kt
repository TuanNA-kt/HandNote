package com.example.noteapp.ui.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity: BaseView, AppCompatActivity() {

    protected lateinit var view: View

    //abstract override fun getContentViewId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = layoutInflater.inflate(getContentViewId(), null)
        setContentView(view)
    }
}