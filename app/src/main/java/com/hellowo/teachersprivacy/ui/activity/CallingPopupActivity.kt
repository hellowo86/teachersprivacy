package com.hellowo.teachersprivacy.ui.activity

import android.arch.lifecycle.LifecycleActivity
import android.os.Bundle
import com.hellowo.teachersprivacy.R
import kotlinx.android.synthetic.main.activity_calling_popup.*

class CallingPopupActivity : LifecycleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling_popup)
        setLayout()
    }

    private fun setLayout() {
        clearBtn.setOnClickListener { finish() }
    }
}
