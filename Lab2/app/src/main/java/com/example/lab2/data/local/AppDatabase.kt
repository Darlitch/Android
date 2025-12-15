package com.example.lab2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.lab2.data.local.dao.CurrencyDao
import com.example.lab2.data.local.entity.CurrencyEntity
import com.example.lab2.data.local.entity.MetaEntity

@Database(entities = [CurrencyEntity::class, MetaEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}