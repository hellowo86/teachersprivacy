package com.hellowo.teachersprivacy.ui.activity

import android.Manifest
import android.arch.lifecycle.LifecycleActivity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.bumptech.glide.Glide
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.hellowo.teachersprivacy.FileUtil
import com.hellowo.teachersprivacy.R
import com.hellowo.teachersprivacy.log
import com.hellowo.teachersprivacy.model.Student
import gun0912.tedbottompicker.TedBottomPicker
import io.realm.Realm
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_student.*
import java.io.File
import java.util.*


class StudentActivity : LifecycleActivity() {
    lateinit var student: Student
    var isEditMode: Boolean = false
    var isInsertMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
        if(intent.getStringExtra("id").isNullOrEmpty()) {
            student = Student()
            student.id = UUID.randomUUID().toString()
            isEditMode = true
            isInsertMode = true
            setModeLayout()
            deleteBtn.visibility = View.GONE
        }else {
            student = Realm.getDefaultInstance().where(Student::class.java)
                    .equalTo("id", intent.getStringExtra("id")).findFirst()
        }
        log(student.toString())
        setLayout()
    }

    private fun setLayout() {
        nameText.setText(student.name)
        schoolText.text = student.schoolInfo
        phoneNumberText.setText(student.phoneNumber)
        addressText.setText(student.address)
        memoText.setText(student.memo)

        if(student.parents?.isNotEmpty() as Boolean) {
            phoneNumberText.setText(student.parents!![0].phoneNumber)
        }

        if(student.profileImageUrl.isNullOrEmpty()) {
            profileImg.setImageResource(R.drawable.boy)
        }else {
            Glide.with(this).load(File(student.profileImageUrl)).bitmapTransform(CropCircleTransformation(this)).into(profileImg)
        }

        profileImg.setOnClickListener { checkExternalStoragePermission() }
        deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.delete_student)
            builder.setCancelable(true)
            builder.setMessage(R.string.delete_student_sub)
            builder.setPositiveButton(R.string.ok) { _,_ -> delete() }
            builder.setNegativeButton(R.string.cancel, null)
            builder.show()
        }
        editBtn.setOnClickListener {
            isEditMode = true
            setModeLayout()
        }
        confirmBtn.setOnClickListener {
            save(student.profileImageUrl)
            isEditMode = false
            setModeLayout()
            if(isInsertMode) { finish() }
        }
        setModeLayout()
    }

    private fun setModeLayout() {
        if(isEditMode) {
            nameText.isEnabled = true
            phoneNumberText.isEnabled = true
            pPhoneNumberText.isEnabled = true
            addressText.isEnabled = true
            memoText.isEnabled = true
            editBtn.visibility = View.GONE
            confirmBtn.visibility = View.VISIBLE
        }else {
            nameText.isEnabled = false
            phoneNumberText.isEnabled = false
            pPhoneNumberText.isEnabled = false
            addressText.isEnabled = false
            memoText.isEnabled = false
            editBtn.visibility = View.VISIBLE
            confirmBtn.visibility = View.GONE
        }
    }

    private val permissionlistener = object : PermissionListener {
        override fun onPermissionGranted() { showPhotoPicker() }
        override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {}
    }

    fun checkExternalStoragePermission() {
        TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()
    }

    private fun showPhotoPicker() {
        val bottomSheetDialogFragment = TedBottomPicker.Builder(this)
                .setOnImageSelectedListener {
                    uri -> save(FileUtil.getPath(this, uri))
                }.setMaxCount(100).create()
        bottomSheetDialogFragment.show(supportFragmentManager)
    }

    private fun save(imgPath: String?) {
        if(!nameText.text.isNullOrBlank() && !phoneNumberText.text.isNullOrBlank()) {
            Realm.getDefaultInstance().executeTransaction { realm ->
                student.name = nameText.text.toString()
                student.phoneNumber = phoneNumberText.text.toString()
                student.address = addressText.text.toString()
                student.memo = memoText.text.toString()
                student.profileImageUrl = imgPath
                realm.insertOrUpdate(student)
                setLayout()
            }
        }
    }

    private fun delete() {
        Realm.getDefaultInstance().executeTransaction { _ -> student.deleteFromRealm() }
        finish()
    }

    private fun startMap(address : String) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=" + address)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.`package` = "com.google.android.apps.maps"
        startActivity(mapIntent)
    }
}
