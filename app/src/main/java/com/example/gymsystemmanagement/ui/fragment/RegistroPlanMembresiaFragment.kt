package com.example.gymsystemmanagement.ui.fragment

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.entity.PlanMembresia
import com.example.gymsystemmanagement.repository.PlanMembresiaRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineName

class RegistroPlanMembresiaFragment: Fragment(R.layout.fragment_registro_plan_membresia) {

    private lateinit var tilNomPlanMembresia: TextInputLayout
    private lateinit var tilDescPlanMembresia: TextInputLayout
    private lateinit var tilPrecioPlanMembresia: TextInputLayout
    private lateinit var etNomPlanMembresia: TextInputEditText
    private lateinit var etDescPlanMembresia: TextInputEditText
    private lateinit var etPrecioPlanMembresia: TextInputEditText
    private lateinit var tilDuracionPlanMembresia: TextInputLayout
    private lateinit var etDuracionPlanMembresia: TextInputEditText
    private lateinit var btnRegistrarMembresia: MaterialButton
    private lateinit var btnCancelar: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tilNomPlanMembresia = view.findViewById(R.id.tilNomPlanMembresia)
        tilDescPlanMembresia = view.findViewById(R.id.tilDescPlanMembresia)
        tilPrecioPlanMembresia = view.findViewById(R.id.tilPrecioPlanMembresia)
        etNomPlanMembresia = view.findViewById(R.id.etNomPlanMembresia)
        etDescPlanMembresia = view.findViewById(R.id.etDescPlanMembresia)
        etPrecioPlanMembresia = view.findViewById(R.id.etPrecioPlanMembresia)
        btnRegistrarMembresia = view.findViewById(R.id.btnRegistrarMembresia)
        btnCancelar = view.findViewById(R.id.btnCancelar)
        tilDuracionPlanMembresia = view.findViewById(R.id.tilDuracionPlanMembresia)
        etDuracionPlanMembresia = view.findViewById(R.id.etDuracionPlanMembresia)

        btnRegistrarMembresia.setOnClickListener {
            guardarPlanMembresia()
        }
        btnCancelar.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, OpcionesFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    private fun guardarPlanMembresia() {
        if(!validarCampos()) return
        try {
            val planMembresia = PlanMembresia(
                nombrePlan = etNomPlanMembresia.text.toString(),
                descripcionPlan = etDescPlanMembresia.text.toString(),
                precioPlan = etPrecioPlanMembresia.text.toString().toDouble(),
                duracionMeses = etDuracionPlanMembresia.text.toString().toInt()
            )
            val repo = PlanMembresiaRepository(requireContext())
            val id = repo.insertarPlanMembresia(planMembresia)
            if (id == -1L) {
                mostrarSnackbar("No se pudo registrar el plan de membresía", R.color.red)
                return
            }
            mostrarSnackbar("Plan de membresía registrado con ID: $id", R.color.green,"Ver"){
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, InicioFragment())
                    .addToBackStack(null)
                    .commit()
            }
            LimpiarFormulario()
        }catch (e: SQLiteConstraintException){
            mostrarSnackbar("Error de restricción en la base de datos:", R.color.red)
        }catch (e: Exception){
            mostrarSnackbar("Error al registrar el plan de membresía:", R.color.red)
        }
    }
    private fun validarCampos(): Boolean{
        var error = false
        val nombre = etNomPlanMembresia.text.toString().trim()
        val descripcion = etDescPlanMembresia.text.toString().trim()
        val precioText = etPrecioPlanMembresia.text.toString().trim()
        val duracionText = etDuracionPlanMembresia.text.toString().trim()
        listOf(
            tilDuracionPlanMembresia,tilNomPlanMembresia, tilDescPlanMembresia,tilPrecioPlanMembresia
        ).forEach { it.error = null }
        if (nombre.isEmpty())  tilNomPlanMembresia.error = "El nombre del plan es obligatorio".also { error = true }
        if (descripcion.isEmpty())  tilDescPlanMembresia.error = "La descripción del plan es obligatoria".also { error = true }
        if (precioText.isEmpty())  tilPrecioPlanMembresia.error = "El precio del plan es obligatorio".also { error = true}
        if (duracionText.isEmpty()) tilDuracionPlanMembresia.error =  "La duración del plan es obligatoria".also { error = true }
        return !error
    }
    private fun LimpiarFormulario() {
        etNomPlanMembresia.text?.clear()
        etDescPlanMembresia.text?.clear()
        etPrecioPlanMembresia.text?.clear()
        etDuracionPlanMembresia.text?.clear()
    }
    private fun mostrarSnackbar(mensaje: String, color: Int, accion: String? = null, onClick: (() -> Unit)? = null) {
        val snackbar = Snackbar.make(requireView(), mensaje, Snackbar.LENGTH_LONG)
            .setBackgroundTint(resources.getColor(color, null))
            .setTextColor(resources.getColor(R.color.white, null))

        if (accion != null && onClick != null) {
            snackbar.setAction(accion) { onClick() }
        }
        snackbar.show()
    }

}