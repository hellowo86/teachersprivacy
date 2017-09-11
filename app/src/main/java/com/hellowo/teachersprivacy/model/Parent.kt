package com.hellowo.teachersprivacy.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Parent(@PrimaryKey var id: String? = null,
                  var name: String? = null,
                  var phoneNumber: String? = null,
                  var profileImageUrl: String? = null): RealmObject()