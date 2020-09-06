package com.duke.xial.elliot.kim.kotlin.egglotto.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.duke.xial.elliot.kim.kotlin.egglotto.database.Database
import com.duke.xial.elliot.kim.kotlin.egglotto.database.LottoNumbersModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ViewModel (application: Application): AndroidViewModel(application) {
    private val database = Room.databaseBuilder(
        application,
        Database::class.java,
        DATABASE_NAME
    ).build()

    lateinit var changedLottoNumbers: LottoNumbersModel
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    var state = INITIALIZE

    fun getAll(): LiveData<MutableList<LottoNumbersModel>> = database.dao().getAll()

    fun insert(lottoNumbers: LottoNumbersModel) {
        state = INSERT
        scope.launch {
            changedLottoNumbers = lottoNumbers
            database.dao().insert(lottoNumbers)
        }
    }

    fun delete(lottoNumbers: LottoNumbersModel) {
        state = DELETE
        scope.launch {
            changedLottoNumbers = lottoNumbers
            database.dao().delete(lottoNumbers)
        }
    }

    fun nukeTable() {
        scope.launch {
            database.dao().nukeTable()
        }
    }

    companion object {
        const val INITIALIZE = 0
        const val INITIALIZED = 1
        const val INSERT = 2
        const val DELETE = 3
        private const val DATABASE_NAME = "zion_egg_lotto_database"
    }
}