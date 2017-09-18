package com.hellowo.teachersprivacy.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Student(@PrimaryKey var id: String? = null,
                   var schoolInfo: String? = null,
                   var name: String? = null,
                   var number: Int? = 0,
                   var phoneNumber: String? = null,
                   var address: String? = null,
                   var memo: String? = null,
                   var profileImageUrl: String? = null,
                   var lastCallTime: Long = 0,
                   var parents: RealmList<Parent> = RealmList()) : RealmObject()