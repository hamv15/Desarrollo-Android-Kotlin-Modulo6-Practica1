package com.hamv15.modulo6practica1.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hamv15.modulo6practica1.data.db.model.CarDao
import com.hamv15.modulo6practica1.data.db.model.CarEntity
import com.hamv15.modulo6practica1.util.Constants

@Database(
    entities = [CarEntity::class],
    version = 1,
    exportSchema = true //Por si se quiere el esquema para una migracion
)
abstract class CarDatabase: RoomDatabase() {
    //Aqui va el DAO
    abstract fun carDao(): CarDao

    //Esto para manejar Singleton

    companion object{

        @Volatile
        private var INSTANCE: CarDatabase? = null

        fun getDatabase(context: Context): CarDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CarDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                instance
            }
        }


    }
}