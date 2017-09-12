package com.hellowo.teachersprivacy.receiver


import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import java.util.*
import android.media.RingtoneManager
import android.app.PendingIntent
import android.graphics.Bitmap
import android.graphics.Color
import android.support.v4.app.NotificationCompat
import com.bumptech.glide.Glide
import com.hellowo.teachersprivacy.R
import com.hellowo.teachersprivacy.log
import com.hellowo.teachersprivacy.model.History
import com.hellowo.teachersprivacy.ui.activity.CallingPopupActivity
import com.hellowo.teachersprivacy.ui.activity.MainActivity
import io.realm.Realm
import jp.wasabeef.glide.transformations.CropCircleTransformation
import java.io.File


class CallBroadcastReceiver : BroadcastReceiver() {
    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var callStartTime: Date? = null
    private var isIncoming: Boolean = false
    private var savedNumber: String? = null  //because the passed incoming is only valid in ringing

    override fun onReceive(context: Context, intent: Intent) {
        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
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
            log(stateStr + "/" + number)
            onCallStateChanged(context, state, number)
        }
    }

    //Derived classes should override these to respond to specific events of interest
    fun onIncomingCallStarted(ctx: Context, number: String?, start: Date?) { startPopupActivity(ctx, number, start) }
    fun onOutgoingCallStarted(ctx: Context, number: String?, start: Date?) {}
    fun onIncomingCallEnded(ctx: Context, number: String?, start: Date?, end: Date?) { saveHistory(ctx, number, start, end, 0) }
    fun onOutgoingCallEnded(ctx: Context, number: String?, start: Date?, end: Date?) { saveHistory(ctx, number, start, end, 1) }
    fun onMissedCall(ctx: Context, number: String?, start: Date?) { saveHistory(ctx, number, start, start, 2) }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    fun onCallStateChanged(context: Context, state: Int, number: String) {
        if (lastState == state) {
            //No change, debounce extras
            return
        }
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                callStartTime = Date()
                savedNumber = number
                onIncomingCallStarted(context, number, callStartTime as Date)
            }
            TelephonyManager.CALL_STATE_OFFHOOK ->
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false
                    callStartTime = Date()
                    onOutgoingCallStarted(context, savedNumber, callStartTime)
                }
            TelephonyManager.CALL_STATE_IDLE ->
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime)
                } else if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, Date())
                } else {
                    onOutgoingCallEnded(context, savedNumber, callStartTime, Date())
                }
        }
        lastState = state
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

    private fun startPopupActivity(ctx: Context, number: String?, start: Date?) {
        log("startPopupActivity" + number)
        val intent = Intent(ctx, CallingPopupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("phoneNumber", number)
        ctx.startActivity(intent)
    }

    private fun  saveHistory(ctx: Context, number: String?, start: Date?, end: Date?, type: Int) {
        log("saveHistory" + number)
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val history = realm.createObject(History::class.java, UUID.randomUUID().toString())
            history.dtStart = start?.time
            history.dtEnd = end?.time
        }
        realm.close()
    }
}