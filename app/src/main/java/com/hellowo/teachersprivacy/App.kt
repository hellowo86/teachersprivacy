package com.hellowo.teachersprivacy

import android.app.Application
import android.content.ContextWrapper

import com.pixplicity.easyprefs.library.Prefs

import io.realm.Realm
import io.realm.RealmConfiguration

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initRealm()
        initPrefs()
    }

    /**
     * Realm 데이터베이스 초기화
     * Context.getFilesDir()에 "default.realm"란 이름으로 Realm 파일이 위치한다
     */
    private fun initRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder().build()
        Realm.setDefaultConfiguration(config)
    }

    private fun initPrefs() {
        Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(packageName)
                .setUseDefaultSharedPreference(true)
                .build()
    }
}
