package com.duke.xial.elliot.kim.kotlin.egglotto.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
@Suppress("SpellCheckingInspection")
interface LottoNumbersDao {
    @Query("SELECT * FROM lottonumbersmodel")
    fun getAll(): LiveData<MutableList<LottoNumbersModel>>
    // ORDER BY creationTime DESC

    @Insert
    fun insert(note: LottoNumbersModel)

    @Delete
    fun delete(note: LottoNumbersModel)

    @Query("DELETE FROM lottonumbersmodel")
    fun nukeTable()
}