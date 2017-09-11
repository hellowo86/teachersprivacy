package com.hellowo.teachersprivacy.ui.activity

import android.Manifest
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.ImageButton
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.hellowo.teachersprivacy.R
import com.hellowo.teamfinder.ui.fragment.HistoryFragment
import com.hellowo.teamfinder.ui.fragment.SettingFragment
import com.hellowo.teamfinder.ui.fragment.StudentFragment
import com.hellowo.teamfinder.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : LifecycleActivity() {
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        setLayout()
        checkPermission()
    }

    private fun setLayout() {
        studentTab.setOnClickListener{ clickTab(it as ImageButton?) }
        historyTab.setOnClickListener{ clickTab(it as ImageButton?) }
        settingTab.setOnClickListener{ clickTab(it as ImageButton?) }
        clickTab(studentTab)
    }

    private fun clickTab(item: ImageButton?) {
        studentTab.setColorFilter(resources.getColor(R.color.disableText))
        historyTab.setColorFilter(resources.getColor(R.color.disableText))
        settingTab.setColorFilter(resources.getColor(R.color.disableText))
        item?.setColorFilter(resources.getColor(R.color.colorPrimary))

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container,
                when (item) {
                    studentTab -> StudentFragment()
                    historyTab -> HistoryFragment()
                    settingTab -> SettingFragment()
                    else -> return
                })
        fragmentTransaction.commit()
    }

    internal var permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {}
        override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {}
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED) {
            TedPermission(this)
                    .setPermissionListener(permissionlistener)
                    //.setRationaleMessage(getString(R.string.rational_permission))
                    .setDeniedMessage(getString(R.string.denied_permission))
                    .setPermissions(Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.PROCESS_OUTGOING_CALLS /*,Manifest.permission.SYSTEM_ALERT_WINDOW*/)
                    .check()
        }
    }
}
