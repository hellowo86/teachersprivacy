package com.hellowo.teachersprivacy.ui.activity

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.hellowo.teachersprivacy.R
import com.hellowo.teamfinder.viewmodel.MainViewModel

class StudentActivity : LifecycleActivity() {
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        setLayout()
    }

    private fun setLayout() {
    }

}
