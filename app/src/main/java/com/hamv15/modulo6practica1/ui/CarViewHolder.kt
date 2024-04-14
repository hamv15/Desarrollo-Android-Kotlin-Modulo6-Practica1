package com.hamv15.modulo6practica1.ui

import androidx.recyclerview.widget.RecyclerView
import com.hamv15.modulo6practica1.R
import com.hamv15.modulo6practica1.data.db.model.CarEntity
import com.hamv15.modulo6practica1.databinding.CarElementBinding

class CarViewHolder(private val binding: CarElementBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(car: CarEntity){
        //Asignar los valores del carro al elemento de la lista
        binding.apply {
            tvFabricante.text = car.carMaker
            tvModelo.text = car.model
            tvYear.text = car.carYear.toString()
            ivIcon.setImageResource(getCarImageResouce(car.carMaker))
        }
    }

    private fun getCarImageResouce(makerName: String): Int{
        return when (makerName){
            "Cupra" -> R.drawable.cupra
            "Ford" -> R.drawable.ford
            "Toyota" -> R.drawable.toyota
            else -> R.drawable.cupra
        }
    }
}