package com.hamv15.modulo6practica1.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.hamv15.modulo6practica1.R
import com.hamv15.modulo6practica1.application.Modulo6Practica1App
import com.hamv15.modulo6practica1.data.CarRepository
import com.hamv15.modulo6practica1.data.db.model.CarEntity
import com.hamv15.modulo6practica1.databinding.CarDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class CarDialog(
    private val newCar: Boolean = true,
    private var car: CarEntity =  CarEntity(
        carMaker="",
        model= "",
        carYear=0),
    private val updateUI: () -> Unit,
    private val message: (String) -> Unit
): DialogFragment() {
    /*Los gragments siempre tienen que ir ligados a un activity
    * no pueden ir solitos. Se pueden ligar fragments ligados a otros
    * fragments pero el principal siempre debe estar ligado a un
    * activity*/

    //Por si aun no se termina de inflar, nos preparamos para el null
    private var _binding: CarDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: Dialog

    private var saveButton: Button? = null

    private lateinit var repository: CarRepository





    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dropdownOptions = arrayOf("Opción 1", "Opción 2", "Opción 3", "Opción 4")

        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, dropdownOptions)
        val autoCompleteTextView = view?.findViewById<AutoCompleteTextView>(R.id.actvDropdown)

        _binding = CarDialogBinding.inflate(requireActivity().layoutInflater)

        builder=AlertDialog.Builder(requireContext())

        repository = (requireContext().applicationContext as Modulo6Practica1App).repository

        autoCompleteTextView?.setAdapter(adapter)

        binding.apply {
            //Se fijan los datos del elemento
        }

        //Funcionamiento del dialog para insercion, actualizacion y borrado
        dialog = if(newCar)
            buildDialog(
                "Guardar",
                "Cancelar",
                {
                    //Accion de guardar
                    car.apply {
                        carMaker=binding.tietTitle.text.toString().trim()
                        model=binding.tietGenre.text.toString().trim()
                        carYear=binding.tietDeveloper.text.toString().trim().toInt()
                    }
                    try {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val result = async {
                                repository.insertcar(car)
                            }
                            result.await()
                            withContext(Dispatchers.Main){
                                message("Carro guardado exitosamente")
                                updateUI()
                            }
                        }
                    }catch (e: IOException){
                        e.printStackTrace()
                        message("Error al guardar el carro")
                    }
                },
                {
                    //Acccion de cancelar

                })
        else
            buildDialog(
                "Actualizar",
                "Borrar",
                {
                    //Accion de actualizar
                    car.apply {
                        carMaker=binding.tietTitle.text.toString().trim()
                        model=binding.tietGenre.text.toString().trim()
                        carYear=binding.tietDeveloper.text.toString().trim().toInt()
                    }
                    try {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val result = async {
                                repository.updatecar(car)
                            }
                            result.await()
                            withContext(Dispatchers.Main){
                                message("Juego actualizado exitosamente")
                                updateUI()
                            }
                        }
                    }catch (e: IOException){
                        e.printStackTrace()
                        message("Error al guardar el juego")
                    }
                },
                {
                    //Alert de confirmación
                    val context = requireContext()

                    AlertDialog.Builder(context)
                        .setTitle("Confirmación")
                        .setMessage("¿Realmente deseas eliminar el carro ${car.model}?")
                        .setPositiveButton("Aceptar"){_, _ ->
                            //Acción de borrar Juego
                            try {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val result = async {
                                        repository.deletecar(car)
                                    }
                                    result.await()
                                    withContext(Dispatchers.Main){
                                        message(getString(R.string.eliminado_exitoso))
                                        updateUI()
                                    }
                                }


                            }catch (e: IOException){
                                e.printStackTrace()
                                message("Error al borrar el juego")
                            }
                        }
                        .setNegativeButton("Cancelar"){_, _ ->
                        }
                        .create()
                        .show()
                }
            )
        return dialog
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        //Castear para obtener propiedades del dialog
        val alertDialog = dialog as AlertDialog
        saveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        saveButton?.isEnabled = false

        binding.tietTitle.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }

        })
        binding.tietGenre.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }

        })
        binding.tietDeveloper.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }

        })

    }

    private fun validateFields(): Boolean =
        (binding.tietTitle.text.toString().isNotEmpty() &&
                binding.tietGenre.text.toString().isNotEmpty() &&
                binding.tietDeveloper.text.toString().isNotEmpty())

    private fun buildDialog(
        btn1Text: String,
        btn2Text: String,
        positiveButton: () -> Unit,
        negativeButton: () -> Unit
    ): Dialog =
        builder.setView(binding.root)
            .setTitle("Carro")
            .setPositiveButton(btn1Text){_,_ ->
                //Acción para el boton positivo
                positiveButton()
            }
            .setNegativeButton(btn2Text){_,_ ->
                //Acción para el boton negativo
                negativeButton()
            }
            .create()

}