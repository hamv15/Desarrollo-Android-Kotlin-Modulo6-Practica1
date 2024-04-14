package com.hamv15.modulo6practica1.data

import com.hamv15.modulo6practica1.data.db.model.CarDao
import com.hamv15.modulo6practica1.data.db.model.CarEntity

class CarRepository(private val carDao: CarDao) {

    suspend fun insertCar(car: CarEntity){
        carDao.insertCar(car)
    }

    suspend fun getAllCars(): List<CarEntity>{
        return carDao.getAllCars()
    }

    suspend fun updateCar(car: CarEntity){
        carDao.updateCar(car)
    }

    suspend fun deleteCar(car: CarEntity){
        carDao.deleteCar(car)
    }
}