package com.hamv15.modulo6practica1.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hamv15.modulo6practica1.data.db.model.CarEntity
import com.hamv15.modulo6practica1.databinding.CarElementBinding

class CarAdapter(private val onCarClicked: (CarEntity) -> Unit): RecyclerView.Adapter<CarViewHolder>() {

    private var cars: List<CarEntity> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = CarElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cars.size
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = cars[position]
        holder.bind(car)

        holder.itemView.setOnClickListener {
            //Aqui va el click de cada elemento
            onCarClicked(car)
        }
    }


    fun updateList(list: List<CarEntity>){
        cars = list
        notifyDataSetChanged()
    }
}