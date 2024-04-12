package com.hamv15.modulo6practica1.ui

import androidx.recyclerview.widget.RecyclerView
import com.hamv15.modulo6practica1.data.db.model.CarEntity
import com.hamv15.modulo6practica1.databinding.CarElementBinding

class CarViewHolder(private val binding: CarElementBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(car: CarEntity){
        //Asignar los valores del carro al elemento de la lista
        binding.apply {
            tvTitle.text = car.model
            tvGenre.text = car.carYear.toString()
        }
    }
}