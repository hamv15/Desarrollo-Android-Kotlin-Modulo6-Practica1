package com.hamv15.modulo6practica1.data

import com.hamv15.modulo6practica1.data.db.model.CarDao
import com.hamv15.modulo6practica1.data.db.model.CarEntity

class CarRepository(private val carDao: CarDao) {

    suspend fun insertcar(car: CarEntity){
        carDao.insertCar(car)
    }

    suspend fun getAllcars(): List<CarEntity>{
        return carDao.getAllCars()
    }

    suspend fun updatecar(car: CarEntity){
        carDao.updateCar(car)
    }

    suspend fun deletecar(car: CarEntity){
        carDao.deleteCar(car)
    }
}