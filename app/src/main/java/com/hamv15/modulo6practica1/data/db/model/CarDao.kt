package com.hamv15.modulo6practica1.data.db.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.hamv15.modulo6practica1.util.Constants

@Dao
interface CarDao {
    //Create
    @Insert
    suspend fun insertCar(car: CarEntity) //Suspendida porque se llama desde una corrutina

    //Read
    @Query("SELECT * FROM ${Constants.DATABASE_GAME_TABLE}")
    suspend fun getAllCars(): List<CarEntity>

    //Update
    @Update
    suspend fun updateCar(car: CarEntity)

    //Delete
    @Delete
    suspend fun deleteCar(car: CarEntity)
}