package com.sycodes.cryptotrack.roomdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var coinId : String,
    var savedDate : String
)
