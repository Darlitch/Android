package com.example.lab2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lab2.data.local.entity.CurrencyEntity
import com.example.lab2.data.local.entity.MetaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currencies ORDER BY code")
    fun observeCurrencies(): Flow<List<CurrencyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CurrencyEntity>)

    @Query("DELETE FROM currencies")
    suspend fun clear()

    @Query("SELECT * FROM meta WHERE metaKey = :key LIMIT 1")
    suspend fun getMeta(key: String): MetaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMeta(entity: MetaEntity)
}