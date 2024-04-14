package com.hamv15.modulo6practica1.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.hamv15.modulo6practica1.R
import com.hamv15.modulo6practica1.application.Modulo6Practica1App
import com.hamv15.modulo6practica1.data.CarRepository
import com.hamv15.modulo6practica1.data.db.model.CarEntity
import com.hamv15.modulo6practica1.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var cars: List<CarEntity> = emptyList()
    private lateinit var repository: CarRepository
    private lateinit var carAdapter: CarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = (application as Modulo6Practica1App).repository

        //Iniciar adapter
        carAdapter = CarAdapter() { selectedCar ->
            //Click para actualizar o borrar un elemento
            val dialog = CarDialog(
                newCar = false,
                car = selectedCar,
                updateUI = {
                    updateUI()
                },
                message = {action ->
                    message(action)

                }
            )
            dialog.show(supportFragmentManager, "updatedIALOG")
        }

        binding.rvCars.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = carAdapter
        }
        updateUI()
    }

    private fun updateUI(){
        lifecycleScope.launch() {
            cars = repository.getAllCars()

            binding.tvSinRegistros.visibility=
                if (cars.isEmpty()){
                    View.VISIBLE
                }else{
                    View.INVISIBLE
                }
            carAdapter.updateList(cars)

        }
    }


    private fun message(text: String){
        //Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        Snackbar.make(binding.cl, text, Snackbar.LENGTH_SHORT)
            .setTextColor(getColor(R.color.white))
            .setBackgroundTint(Color.parseColor("#9E1734"))
            .show()
    }

    fun click(view: View) {
        //Manejo el click del FAB
        //Paso de lambda como parametro
        val dialog = CarDialog(
            updateUI = {
                updateUI()
            },
            message = {action ->
                message(action)
            })
        dialog.show(supportFragmentManager, "insertDialog")
    }
}