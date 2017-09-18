package com.hellowo.teachersprivacy.receiver


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.telephony.TelephonyManager
import com.hellowo.teachersprivacy.R
import com.hellowo.teachersprivacy.log
import com.hellowo.teachersprivacy.model.History
import com.hellowo.teachersprivacy.model.Student
import com.hellowo.teachersprivacy.ui.activity.CallingPopupActivity
import com.hellowo.teachersprivacy.ui.activity.MainActivity
import com.pixplicity.easyprefs.library.Prefs
import io.realm.Realm
import java.util.*


class CallBroadcastReceiver : BroadcastReceiver() {
    companion object {
        private var isIncoming: Boolean = false
        private var savedNumber: String? = null
        private var callStartTime: Long = 0
        private var lastCallState: Int = TelephonyManager.CALL_STATE_IDLE
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.NEW_OUTGOING_CALL") {
            savedNumber = intent.extras.getString("android.intent.extra.PHONE_NUMBER")
        } else {
            val stateStr = intent.extras.getString(TelephonyManager.EXTRA_STATE)
            val number = intent.extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
            var state = 0
            if (stateStr == TelephonyManager.EXTRA_STATE_IDLE) {
                state = TelephonyManager.CALL_STATE_IDLE
            } else if (stateStr == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                state = TelephonyManager.CALL_STATE_OFFHOOK
            } else if (stateStr == TelephonyManager.EXTRA_STATE_RINGING) {
                state = TelephonyManager.CALL_STATE_RINGING
            }
            onCallStateChanged(context, state, number)
        }
    }

    //Derived classes should override these to respond to specific events of interest
    fun onIncomingCallStarted(ctx: Context, number: String?, start: Long) { startPopupActivity(ctx, number, start) }
    fun onOutgoingCallStarted(ctx: Context, number: String?, start: Long) {}
    fun onIncomingCallEnded(ctx: Context, number: String?, start: Long, end: Long) { saveHistory(ctx, number, start, end, 0) }
    fun onOutgoingCallEnded(ctx: Context, number: String?, start: Long, end: Long) { saveHistory(ctx, number, start, end, 1) }
    fun onMissedCall(ctx: Context, number: String?, start: Long) { saveHistory(ctx, number, start, start, 2) }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    fun onCallStateChanged(context: Context, state: Int, number: String) {
        if (lastCallState == state) {
            //No change, debounce extras
            return
        }
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                callStartTime = System.currentTimeMillis()
                savedNumber = number
                onIncomingCallStarted(context, number, callStartTime)
                Prefs.putLong("lastCallTime", callStartTime)
            }
            TelephonyManager.CALL_STATE_OFFHOOK ->
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastCallState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false
                    callStartTime = System.currentTimeMillis()
                    onOutgoingCallStarted(context, savedNumber, callStartTime)
                }
            TelephonyManager.CALL_STATE_IDLE ->
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastCallState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime)
                } else if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, System.currentTimeMillis())
                } else {
                    onOutgoingCallEnded(context, savedNumber, callStartTime, System.currentTimeMillis())
                }
        }
        lastCallState = state
        Prefs.putInt("lastCallState", state)
    }

    fun showNotification(ctx: Context, number: String?, start: Date?) {
/*
        var resource: Bitmap? = null
        try{
            resource = Glide.with(ctx).load(File())
                    .asBitmap().transform(CropCircleTransformation(ctx)).into(100, 100).get()
        }catch (e: Exception){}
*/
        val intent = Intent(ctx, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(ctx)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setColor(Color.BLACK)
                .setContentTitle(number)
                .setContentText("bb")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)

        val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    private fun startPopupActivity(ctx: Context, number: String?, start: Long) {
        log("startPopupActivity" + number)
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val student = realm.where(Student::class.java).equalTo("phoneNumber", number)
                    .or().equalTo("parents.phoneNumber", number)
                    .findFirst()

            student?.let {
                val intent = Intent(ctx, CallingPopupActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("id", it.id)
                ctx.startActivity(intent)
            }
        }
        realm.close()
    }

    private fun  saveHistory(ctx: Context, number: String?, start: Long, end: Long, type: Int) {
        log("saveHistory" + number)
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val student = realm.where(Student::class.java).equalTo("phoneNumber", number)
                    .or().equalTo("parents.phoneNumber", number)
                    .findFirst()
            student.lastCallTime = start

            student?.let {
                val history = realm.createObject(History::class.java, UUID.randomUUID().toString())
                history.student = it
                history.dtStart = start
                history.dtEnd = end
                history.type = type
            }
        }
        realm.close()
    }
}