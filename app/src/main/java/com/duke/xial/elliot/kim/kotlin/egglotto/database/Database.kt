package com.duke.xial.elliot.kim.kotlin.egglotto.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [LottoNumbersModel::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun dao(): LottoNumbersDao
}