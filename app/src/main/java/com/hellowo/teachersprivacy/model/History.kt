package com.hellowo.teachersprivacy.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class History(@PrimaryKey var id: String? = null,
                   var dtStart: Long? = null,
                   var dtEnd: Long? = null,
                   var type: Int? = 0,
                   var student: Student? = null): RealmObject()