package com.hamv15.modulo6practica1.application

import android.app.Application
import com.hamv15.modulo6practica1.data.CarRepository
import com.hamv15.modulo6practica1.data.db.CarDatabase

class Modulo6Practica1App: Application() {
    private val database by lazy {
        CarDatabase.getDatabase(this@Modulo6Practica1App)
    }

    val repository by lazy {
        CarRepository(database.carDao())
    }

}