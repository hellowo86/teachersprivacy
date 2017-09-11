package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hellowo.teachersprivacy.model.History
import com.hellowo.teachersprivacy.model.Student
import io.realm.Realm
import io.realm.RealmResults


class StudentViewModel : ViewModel() {
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val studentList: RealmResults<Student> = Realm.getDefaultInstance().where(Student::class.java).findAllSorted("name")

}
