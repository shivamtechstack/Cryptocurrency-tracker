package com.sycodes.cryptotrack.roomdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDAO {

    @Insert
    suspend fun insertItem(item: ItemEntity)

}