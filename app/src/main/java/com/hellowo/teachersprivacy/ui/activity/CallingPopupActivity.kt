package com.hellowo.teachersprivacy.ui.activity

import android.arch.lifecycle.LifecycleActivity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import com.hellowo.teachersprivacy.R
import com.hellowo.teachersprivacy.log
import com.hellowo.teachersprivacy.model.Student
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_calling_popup.*


class CallingPopupActivity : LifecycleActivity() {
    lateinit var student: Student

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("CallingPopupActivity onCreate")
        setContentView(R.layout.activity_calling_popup)
        if(!intent.getStringExtra("id").isNullOrEmpty()) {
            student = Realm.getDefaultInstance().where(Student::class.java)
                    .equalTo("id", intent.getStringExtra("id")).findFirst()
            setLayout()
        }else {
            finish()
        }
    }

    private fun setLayout() {
        clearBtn.setOnClickListener { finish() }
    }

    private fun acceptCall() {
        Thread(Runnable {
            try {
                Runtime.getRuntime().exec("input keyevent " + KeyEvent.KEYCODE_HEADSETHOOK)
            } catch (t: Throwable) {
                val enforcedPerm = "android.permission.CALL_PRIVILEGED"
                val btnDown = Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT,
                        KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK))
                val btnUp = Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT,
                        KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK))

                sendOrderedBroadcast(btnDown, enforcedPerm)
                sendOrderedBroadcast(btnUp, enforcedPerm)
            }
        }).start()
    }
}
