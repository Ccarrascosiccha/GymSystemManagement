package com.example.gymsystemmanagement.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.adapter.PlanMembresiaAdapter
import com.example.gymsystemmanagement.entity.PlanMembresia
import com.example.gymsystemmanagement.repository.PlanMembresiaRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistorialPlanMembresiaFragment : Fragment(R.layout.fragment_historial_plan_membresia) {

    private lateinit var rvPlanesMemb: RecyclerView
    private lateinit var planMembresiaAdapter: PlanMembresiaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvPlanesMemb = view.findViewById(R.id.rvPlanesMemb)
        rvPlanesMemb.layoutManager = LinearLayoutManager(requireContext())

        cargarPlanesMembresia()
    }

    private fun cargarPlanesMembresia() {
        GlobalScope.launch(Dispatchers.Main) {
            val repo = PlanMembresiaRepository(requireContext())
            val planesMembresia = withContext(Dispatchers.IO) {
                repo.obtenerTodosLosPlanes()
            }
            planMembresiaAdapter = PlanMembresiaAdapter(
                planesMembresia,
                onOpciones = { plan -> mostrarDialogOpciones(plan) }
            )
            rvPlanesMemb.adapter = planMembresiaAdapter
        }
    }

    private fun mostrarDialogOpciones(plan: PlanMembresia) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_opciones, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTitulo)
        val btnEditar = dialogView.findViewById<MaterialButton>(R.id.btnEditar)
        val btnEliminar = dialogView.findViewById<MaterialButton>(R.id.btnEliminar)
        val btnCancelar = dialogView.findViewById<MaterialButton>(R.id.btnCancelar)

        tvTitulo.text = "Plan: ${plan.nombrePlan}"

        btnEditar.setOnClickListener {
            dialog.dismiss()
            mostrarDialogEditar(plan)
        }

        btnEliminar.setOnClickListener {
            dialog.dismiss()
            mostrarDialogEliminar(plan)
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun mostrarDialogEditar(plan: PlanMembresia) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_editar_plan, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val etNombre = dialogView.findViewById<TextInputEditText>(R.id.etNomPlanMembresia)
        val etPrecio = dialogView.findViewById<TextInputEditText>(R.id.etPrecioPlanMembresia)
        val etDuracion = dialogView.findViewById<TextInputEditText>(R.id.etDuracionPlanMembresia)
        val etDescripcion = dialogView.findViewById<TextInputEditText>(R.id.etDescPlanMembresia)
        val btnActualizar = dialogView.findViewById<MaterialButton>(R.id.btnActualizar)
        val btnCancelar = dialogView.findViewById<MaterialButton>(R.id.btnCancelar)

        etNombre.setText(plan.nombrePlan)
        etPrecio.setText(plan.precioPlan.toString())
        etDuracion.setText(plan.duracionMeses.toString())
        etDescripcion.setText(plan.descripcionPlan)

        btnActualizar.setOnClickListener {
            if (validarCampos(etNombre, etPrecio, etDuracion, etDescripcion)) {
                dialog.dismiss()
                // Mostrar confirmación personalizada
                mostrarDialogConfirmacion(
                    titulo = "¿Actualizar Plan?",
                    mensaje = "¿Está seguro de actualizar el plan '${plan.nombrePlan}'?",
                    textoBotonConfirmar = "Actualizar",
                    iconoResId = R.drawable.ic_check
                ) {
                    val planActualizado = PlanMembresia(
                        id = plan.id,
                        nombrePlan = etNombre.text.toString(),
                        precioPlan = etPrecio.text.toString().toDouble(),
                        duracionMeses = etDuracion.text.toString().toInt(),
                        descripcionPlan = etDescripcion.text.toString()
                    )
                    actualizarPlan(planActualizado)
                }
            }
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun mostrarDialogEliminar(plan: PlanMembresia) {
        mostrarDialogConfirmacion(
            titulo = "¿Eliminar Plan?",
            mensaje = "¿Está seguro de eliminar el plan '${plan.nombrePlan}'?\n\nEsta acción no se puede deshacer.",
            textoBotonConfirmar = "Eliminar",
            iconoResId = R.drawable.ic_alert
        ) {
            eliminarPlan(plan.id)
        }
    }
    private fun mostrarDialogConfirmacion(
        titulo: String,
        mensaje: String,
        textoBotonConfirmar: String = "Confirmar",
        iconoResId: Int = R.drawable.ic_alert,
        onConfirmar: () -> Unit
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirmacion, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Hacer el fondo transparente para que se vean las esquinas redondeadas
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Referencias a las vistas
        val ivIcono = dialogView.findViewById<ImageView>(R.id.ivIconoDialog)
        val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTituloDialog)
        val tvMensaje = dialogView.findViewById<TextView>(R.id.tvMensajeDialog)
        val btnConfirmar = dialogView.findViewById<MaterialButton>(R.id.btnConfirmar)
        val btnCancelar = dialogView.findViewById<MaterialButton>(R.id.btnCancelarDialog)

        // Configurar contenido
        ivIcono.setImageResource(iconoResId)
        tvTitulo.text = titulo
        tvMensaje.text = mensaje
        btnConfirmar.text = textoBotonConfirmar

        // Acciones
        btnConfirmar.setOnClickListener {
            onConfirmar()
            dialog.dismiss()
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun validarCampos(
        etNombre: TextInputEditText,
        etPrecio: TextInputEditText,
        etDuracion: TextInputEditText,
        etDescripcion: TextInputEditText
    ): Boolean {
        when {
            etNombre.text.isNullOrEmpty() -> {
                etNombre.error = "Ingrese el nombre"
                etNombre.requestFocus()
                return false
            }
            etPrecio.text.isNullOrEmpty() -> {
                etPrecio.error = "Ingrese el precio"
                etPrecio.requestFocus()
                return false
            }
            etDuracion.text.isNullOrEmpty() -> {
                etDuracion.error = "Ingrese la duración"
                etDuracion.requestFocus()
                return false
            }
            etDescripcion.text.isNullOrEmpty() -> {
                etDescripcion.error = "Ingrese la descripción"
                etDescripcion.requestFocus()
                return false
            }
        }
        return true
    }

    private fun actualizarPlan(plan: PlanMembresia) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val repo = PlanMembresiaRepository(requireContext())
                val resultado = withContext(Dispatchers.IO) {
                    repo.actualizarPlanMembresia(plan)
                }

                if (resultado > 0) {
                    Toast.makeText(
                        requireContext(),
                        "Plan actualizado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    cargarPlanesMembresia() // Recargar lista
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error al actualizar el plan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun eliminarPlan(idPlan: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val repo = PlanMembresiaRepository(requireContext())
                val resultado = withContext(Dispatchers.IO) {
                    repo.eliminarPlanMembresia(idPlan)
                }
                if (resultado > 0) {
                    Toast.makeText(
                        requireContext(),
                        "Plan eliminado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    cargarPlanesMembresia() // Recargar lista
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error al eliminar el plan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}