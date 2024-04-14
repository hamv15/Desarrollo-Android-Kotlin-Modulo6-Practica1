package com.hamv15.modulo6practica1.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
    private val car: CarEntity =  CarEntity(
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

    //Variable para almacenar el fabricante guardado
    private lateinit var fabricanteGuardado: String
    private lateinit var modeloGuardado: String
    private lateinit var yearGuardado: String



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //Obtener datos guardados
        fabricanteGuardado=car.carMaker
        modeloGuardado=car.model
        yearGuardado=car.carYear.toString()

        //Variable para almacenar el fabricante seleccionado
        var fabricanteSeleccionado: String= ""

        _binding = CarDialogBinding.inflate(requireActivity().layoutInflater)

        builder=AlertDialog.Builder(requireContext())

        repository = (requireContext().applicationContext as Modulo6Practica1App).repository

        // Configuración del adapter para el spinner
        val fabricantesCarros = arrayOf(
            requireContext().getString(R.string.lsCarMakerOp0),
            requireContext().getString(R.string.lsCarMakerOp1),
            requireContext().getString(R.string.lsCarMakerOp2),
            requireContext().getString(R.string.lsCarMakerOp3)) // Aquí puedes establecer tus opciones para el droplist
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, fabricantesCarros)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spMaker.adapter = adapter // Aquí asigna el adaptador al Spinner

        // Detección de cambios en el spinner
        binding.spMaker.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fabricanteSeleccionado = fabricantesCarros[position]
                //Se valida que el seleccionado sea diferente del guardado para habilidar validacion del formulario
                if (fabricanteGuardado != fabricanteSeleccionado){
                    // Si la selección es diferente al guardado, se disparan validaciones del formulario
                    saveButton?.isEnabled = validateFields()
                }else{
                    saveButton?.isEnabled = false
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No es necesario hacer nada aquí si no se selecciona nada

            }
        }

        binding.apply {
            //Se fijan los datos del elemento
            when (car.carMaker) {
                requireContext().getString(R.string.lsCarMakerOp1) -> binding.spMaker.setSelection(1)
                requireContext().getString(R.string.lsCarMakerOp2) -> binding.spMaker.setSelection(2)
                requireContext().getString(R.string.lsCarMakerOp3) -> binding.spMaker.setSelection(3)
                else -> binding.spMaker.setSelection(0)
            }
            binding.tietModel.setText(car.model)
            binding.tietYear.setText(car.carYear.toString())
        }

        //Funcionamiento del dialog para insercion, actualizacion y borrado
        dialog = if(newCar)
            buildDialog(
                requireContext().getString(R.string.dialogBtnSave),
                getString(R.string.dialogBtnCancel),
                {
                    //Accion de guardar
                    car.apply {
                        carMaker=fabricanteSeleccionado
                        model=binding.tietModel.text.toString().trim()
                        carYear=binding.tietYear.text.toString().trim().toInt()
                    }
                    try {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val result = async {
                                repository.insertCar(car)
                            }
                            result.await()
                            withContext(Dispatchers.Main){
                                message(requireContext().getString(R.string.msjSaveSucc))
                                updateUI()
                            }
                        }
                    }catch (e: IOException){
                        e.printStackTrace()
                        message(requireContext().getString(R.string.msjErrorSave))
                    }
                },
                {
                    //Acccion de cancelar

                })
        else
            buildDialog(
                requireContext().getString(R.string.dialogBtnUpdate),
                requireContext().getString(R.string.dialogBtnDelete),
                {
                    //Accion de actualizar
                    car.apply {
                        carMaker=fabricanteSeleccionado
                        model=binding.tietModel.text.toString().trim()
                        carYear=binding.tietYear.text.toString().trim().toInt()
                    }
                    try {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val result = async {
                                repository.updateCar(car)
                            }
                            result.await()
                            withContext(Dispatchers.Main){
                                message(requireContext().getString(R.string.msjUpdateSucc))
                                updateUI()
                            }
                        }
                    }catch (e: IOException){
                        e.printStackTrace()
                        message(getString(R.string.msjSaveError))
                    }
                },
                {
                    //Alert de confirmación
                    val context = requireContext()

                    AlertDialog.Builder(context)
                        .setTitle(requireContext().getString(R.string.titleDialog))
                        .setMessage(requireContext().getString(R.string.msjDialogDelete, car.model))
                        .setPositiveButton(requireContext().getString(R.string.dialogBtnConfirm)){ _, _ ->
                            //Acción de borrar Juego
                            try {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val result = async {
                                        repository.deleteCar(car)
                                    }
                                    result.await()
                                    withContext(Dispatchers.Main){
                                        message(context.getString(R.string.eliminado_exitoso))
                                        updateUI()
                                    }
                                }


                            }catch (e: IOException){
                                e.printStackTrace()
                                message(requireContext().getString(R.string.msjDialogDeleteError))
                            }
                        }
                        .setNegativeButton(requireContext().getText(R.string.dialogBtnCancel)){_, _ ->

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


        binding.tietModel.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (modeloGuardado != binding.tietModel.text.toString().trim()){
                    saveButton?.isEnabled = validateFields()
                }else{
                    saveButton?.isEnabled = false
                }

            }

        })
        binding.tietYear.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (yearGuardado != binding.tietYear.text.toString().trim()){
                    saveButton?.isEnabled = validateFields()
                }else{
                    saveButton?.isEnabled = false
                }
            }

        })

    }


    private fun validateFields(): Boolean{
        val fabricanteSeleccionado = binding.spMaker.selectedItem?.toString() ?: ""
        val modelo = binding.tietModel.text.toString()
        val year = binding.tietYear.text.toString()
        return fabricanteSeleccionado != requireContext().getString(R.string.lsCarMakerOp0)  && modelo.isNotEmpty() && year.isNotEmpty()
    }


    private fun buildDialog(
        btn1Text: String,
        btn2Text: String,
        positiveButton: () -> Unit,
        negativeButton: () -> Unit
    ): Dialog =
        builder.setView(binding.root)
            .setTitle(requireContext().getString(R.string.modalTitleCarro))
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