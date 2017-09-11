package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hellowo.teachersprivacy.model.History
import io.realm.Realm
import io.realm.RealmResults


class HistoryViewModel : ViewModel() {
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val historyList: RealmResults<History> = Realm.getDefaultInstance().where(History::class.java).findAllSorted("dtStart")

}
