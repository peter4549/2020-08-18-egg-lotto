package com.duke.xial.elliot.kim.kotlin.egglotto.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LottoNumbersModel(@PrimaryKey(autoGenerate = true) val id: Int = 0,
                             val creationTime: String,
                             val lottoNumbers: Array<Int>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LottoNumbersModel

        if (id != other.id) return false
        if (creationTime != other.creationTime) return false
        if (!lottoNumbers.contentEquals(other.lottoNumbers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + creationTime.hashCode()
        result = 31 * result + lottoNumbers.contentHashCode()
        return result
    }

    fun deepCopy(id: Int = this.id, creationTime: String = this.creationTime,
             lottoNumbers: Array<Int> = this.lottoNumbers) =
        LottoNumbersModel(id, creationTime, lottoNumbers)
}