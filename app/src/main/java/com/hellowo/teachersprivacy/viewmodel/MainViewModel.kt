package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel


class MainViewModel : ViewModel() {
    val loading: MutableLiveData<Boolean> = MutableLiveData()

    init {
    }
}
