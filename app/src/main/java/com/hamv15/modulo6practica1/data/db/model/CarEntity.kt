package com.hamv15.modulo6practica1.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hamv15.modulo6practica1.util.Constants

@Entity(tableName = Constants.DATABASE_GAME_TABLE)
data class CarEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "car_id")
    val idCar: Long = 0,

    @ColumnInfo(name = "car_maker")
    var carMaker: String,

    @ColumnInfo(name = "model")
    var model:String,

    @ColumnInfo(name = "car_year", defaultValue = "Desconocido")
    var carYear: Int
)
