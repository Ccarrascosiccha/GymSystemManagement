package com.example.gymsystemmanagement.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.data.AppDatabaseHelper
import com.example.gymsystemmanagement.data.MembresiaDAO
import com.example.gymsystemmanagement.data.PlanMembresiaDAO
import com.example.gymsystemmanagement.data.TransaccionDAO
import com.example.gymsystemmanagement.entity.Membresia
import com.example.gymsystemmanagement.entity.Transaccion
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class RegistroMembresiaFragment : Fragment(R.layout.fragment_registro_membresia) {

    private lateinit var tilUsuario: TextInputLayout
    private lateinit var etUsuario: TextInputEditText
    private lateinit var tilPlanMembresia: TextInputLayout
    private lateinit var actvPlanMembresia: AutoCompleteTextView
    private lateinit var cardInfoPlan: MaterialCardView
    private lateinit var tvDescripcionPlan: TextView
    private lateinit var tvDuracionPlan: TextView
    private lateinit var tvPrecioPlan: TextView
    private lateinit var tilFechaInicio: TextInputLayout
    private lateinit var etFechaInicio: TextInputEditText
    private lateinit var tilFechaFin: TextInputLayout
    private lateinit var etFechaFin: TextInputEditText
    private lateinit var tilMetodoPago: TextInputLayout
    private lateinit var actvMetodoPago: AutoCompleteTextView
    private lateinit var tilMonto: TextInputLayout
    private lateinit var etMonto: TextInputEditText
    private lateinit var tilObservaciones: TextInputLayout
    private lateinit var etObservaciones: TextInputEditText
    private lateinit var btnRegistrar: MaterialButton
    private lateinit var btnCancelar: MaterialButton

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val dbDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private var planSeleccionadoId = 0
    private var precioOriginal = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inicializarVistas(view)
        cargarPlanes()
        cargarMetodosPago()
        configurarDatePickerFechaInicio()
        configurarListeners()
    }

    private fun inicializarVistas(view: View) {
        tilUsuario = view.findViewById(R.id.tilUsuario)
        etUsuario = view.findViewById(R.id.etUsuario)
        tilPlanMembresia = view.findViewById(R.id.tilPlan)
        actvPlanMembresia = view.findViewById(R.id.actvPlan)
        cardInfoPlan = view.findViewById(R.id.cardInfoPlan)
        tvDescripcionPlan = view.findViewById(R.id.tvDescripcionPlan)
        tvDuracionPlan = view.findViewById(R.id.tvDuracionPlan)
        tvPrecioPlan = view.findViewById(R.id.tvPrecioPlan)
        tilFechaInicio = view.findViewById(R.id.tilFechaInicio)
        etFechaInicio = view.findViewById(R.id.etFechaInicio)
        tilFechaFin = view.findViewById(R.id.tilFechaFin)
        etFechaFin = view.findViewById(R.id.etFechaFin)
        tilMetodoPago = view.findViewById(R.id.tilMetodoPago)
        actvMetodoPago = view.findViewById(R.id.actvMetodoPago)
        tilMonto = view.findViewById(R.id.tilMonto)
        etMonto = view.findViewById(R.id.etMonto)
        tilObservaciones = view.findViewById(R.id.tilObservaciones)
        etObservaciones = view.findViewById(R.id.etObservaciones)
        btnRegistrar = view.findViewById(R.id.btnRegistrar)
        btnCancelar = view.findViewById(R.id.btnCancelar)
    }

    private fun cargarPlanes() {
        try {
            val planDao = PlanMembresiaDAO(requireContext())
            val nombresPlanes = planDao.obtenerNombresPlanes()

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                nombresPlanes
            )
            actvPlanMembresia.setAdapter(adapter)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al cargar planes: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun cargarMetodosPago() {
        val metodosPago = listOf(
            "Efectivo",
            "Tarjeta de Débito",
            "Tarjeta de Crédito",
            "Transferencia Bancaria",
            "Yape",
            "Plin"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            metodosPago
        )
        actvMetodoPago.setAdapter(adapter)
    }

    private fun configurarDatePickerFechaInicio() {
        etFechaInicio.isFocusable = false
        etFechaInicio.isClickable = true
        etFechaInicio.isCursorVisible = false

        etFechaFin.isFocusable = false
        etFechaFin.isClickable = false
        etFechaFin.isCursorVisible = false
        etFechaFin.keyListener = null

        etFechaInicio.setOnClickListener {
            mostrarDatePickerFechaInicio()
        }

        tilFechaInicio.setEndIconOnClickListener {
            mostrarDatePickerFechaInicio()
        }
    }

    private fun configurarListeners() {
        // Listener para mostrar info del plan y calcular fecha fin
        actvPlanMembresia.setOnItemClickListener { _, _, _, _ ->
            mostrarInfoPlan(actvPlanMembresia.text.toString())
            calcularFechaFinAutomatica()
        }

        btnRegistrar.setOnClickListener {
            guardarMembresiaConTransaccion()
        }

        btnCancelar.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragmentContainer, OpcionesFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun mostrarInfoPlan(nombrePlan: String) {
        val dao = PlanMembresiaDAO(requireContext())
        planSeleccionadoId = dao.obtenerIdPorNombre(nombrePlan)
        val plan = dao.obtenerPlanPorId(planSeleccionadoId)

        plan?.let {
            precioOriginal = it.precio
            cardInfoPlan.visibility = View.VISIBLE
            tvDescripcionPlan.text = it.descripcion
            tvDuracionPlan.text = "Duración: ${it.duracionTexto()}"
            tvPrecioPlan.text = "Precio: ${it.precioFormateado()}"
            etMonto.setText(it.precio.toString())
        }
    }

    private fun mostrarDatePickerFechaInicio() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                val fechaSeleccionada = dateFormat.format(calendar.time)
                etFechaInicio.setText(fechaSeleccionada)
                tilFechaInicio.error = null
                calcularFechaFinAutomatica()
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun calcularFechaFinAutomatica() {
        val fechaInicioTexto = etFechaInicio.text?.toString() ?: ""

        if (fechaInicioTexto.isBlank()) {
            return
        }

        val nombrePlan = actvPlanMembresia.text?.toString() ?: ""

        if (nombrePlan.isBlank()) {
            etFechaFin.setText("")
            return
        }

        try {
            val planDao = PlanMembresiaDAO(requireContext())
            val idPlan = planDao.obtenerIdPorNombre(nombrePlan)

            if (idPlan == 0) {
                return
            }

            val plan = planDao.obtenerPlanPorId(idPlan)

            if (plan == null) {
                return
            }

            val duracionMeses = plan.duracionMeses
            val fechaInicio = dateFormat.parse(fechaInicioTexto)

            if (fechaInicio != null) {
                val calendarFin = Calendar.getInstance()
                calendarFin.time = fechaInicio
                calendarFin.add(Calendar.MONTH, duracionMeses)

                val fechaFin = dateFormat.format(calendarFin.time)
                etFechaFin.setText(fechaFin)
                tilFechaFin.error = null
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validarCampos(): Boolean {
        var esValido = true

        // Validar código de usuario
        if (etUsuario.text.isNullOrBlank()) {
            tilUsuario.error = "Ingrese el código de usuario"
            esValido = false
        } else {
            try {
                etUsuario.text.toString().toInt()
                tilUsuario.error = null
            } catch (e: NumberFormatException) {
                tilUsuario.error = "Código inválido"
                esValido = false
            }
        }

        // Validar plan
        if (actvPlanMembresia.text.isNullOrBlank()) {
            tilPlanMembresia.error = "Seleccione un plan"
            esValido = false
        } else {
            tilPlanMembresia.error = null
        }

        // Validar fecha inicio
        if (etFechaInicio.text.isNullOrBlank()) {
            tilFechaInicio.error = "Seleccione fecha de inicio"
            esValido = false
        } else {
            tilFechaInicio.error = null
        }

        // Validar fecha fin
        if (etFechaFin.text.isNullOrBlank()) {
            tilFechaFin.error = "La fecha fin se calcula automáticamente"
            esValido = false
        } else {
            tilFechaFin.error = null
        }

        // Validar método de pago
        if (actvMetodoPago.text.isNullOrBlank()) {
            tilMetodoPago.error = "Seleccione método de pago"
            esValido = false
        } else {
            tilMetodoPago.error = null
        }

        // Validar monto
        if (etMonto.text.isNullOrBlank()) {
            tilMonto.error = "Ingrese el monto"
            esValido = false
        } else {
            try {
                val monto = etMonto.text.toString().toDouble()
                if (monto <= 0) {
                    tilMonto.error = "El monto debe ser mayor a 0"
                    esValido = false
                } else {
                    tilMonto.error = null
                }
            } catch (e: NumberFormatException) {
                tilMonto.error = "Monto inválido"
                esValido = false
            }
        }

        return esValido
    }

    private fun obtenerIdUsuario(): Int {
        return try {
            etUsuario.text.toString().toInt()
        } catch (e: Exception) {
            0
        }
    }

    private fun verificarUsuarioExiste(idUsuario: Int): Boolean {
        val db = AppDatabaseHelper(requireContext()).readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM Usuario WHERE id = ?",
            arrayOf(idUsuario.toString())
        )

        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()

        return count > 0
    }

    private fun guardarMembresiaConTransaccion() {
        if (!validarCampos()) {
            Toast.makeText(requireContext(), "Complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val nombrePlanSeleccionado = actvPlanMembresia.text.toString()
            val planDao = PlanMembresiaDAO(requireContext())
            val idPlan = planDao.obtenerIdPorNombre(nombrePlanSeleccionado)

            if (idPlan == 0) {
                Toast.makeText(requireContext(), "Plan no encontrado", Toast.LENGTH_SHORT).show()
                return
            }

            val idUsuario = obtenerIdUsuario()

            if (idUsuario == 0) {
                Toast.makeText(requireContext(), "Código de usuario no válido", Toast.LENGTH_SHORT).show()
                return
            }

            if (!verificarUsuarioExiste(idUsuario)) {
                Toast.makeText(
                    requireContext(),
                    "El usuario con código $idUsuario no existe",
                    Toast.LENGTH_LONG
                ).show()
                tilUsuario.error = "Usuario no encontrado"
                return
            }

            val fechaInicioDisplay = etFechaInicio.text.toString()
            val fechaFinDisplay = etFechaFin.text.toString()

            val fechaInicioDate = dateFormat.parse(fechaInicioDisplay)
            val fechaFinDate = dateFormat.parse(fechaFinDisplay)

            if (fechaInicioDate == null || fechaFinDate == null) {
                Toast.makeText(requireContext(), "Error en el formato de fechas", Toast.LENGTH_SHORT).show()
                return
            }

            val fechaInicioDB = dbDateFormat.format(fechaInicioDate)
            val fechaFinDB = dbDateFormat.format(fechaFinDate)

            // 1. Crear y guardar la membresía
            val nuevaMembresia = Membresia(
                id = 0,
                idUsuario = idUsuario,
                idPlan = idPlan,
                fechaInicio = fechaInicioDB,
                fechaFin = fechaFinDB,
                estado = "Activa"
            )

            val membresiaDao = MembresiaDAO(requireContext())
            val idMembresia = membresiaDao.insertarMembresia(nuevaMembresia)

            if (idMembresia == -1L) {
                Toast.makeText(requireContext(), "❌ Error al registrar membresía", Toast.LENGTH_SHORT).show()
                return
            }

            // 2. Crear y guardar la transacción (pago)
            val plan = planDao.obtenerPlanPorId(idPlan)
            val monto = etMonto.text.toString().toDouble()
            val metodoPago = actvMetodoPago.text.toString()
            val observaciones = etObservaciones.text.toString()

            val descripcionCompleta = buildString {
                append("Pago de ${plan?.nombre ?: "Plan"}")
                append(" - $metodoPago")
                if (observaciones.isNotBlank()) {
                    append(" - $observaciones")
                }
            }

            val nuevaTransaccion = Transaccion(
                id = 0,
                idUsuario = idUsuario,
                idMembresia = idMembresia.toInt(),
                monto = monto,
                tipo = "Cr", // Crédito = Ingreso
                descripcion = descripcionCompleta,
                fecha = dbDateFormat.format(Date())
            )

            val transaccionDao = TransaccionDAO(requireContext())
            val idTransaccion = transaccionDao.insertarTransaccion(nuevaTransaccion)

            if (idTransaccion != -1L) {
                Toast.makeText(
                    requireContext(),
                    "✅ Membresía y pago registrados exitosamente",
                    Toast.LENGTH_LONG
                ).show()
                limpiarCampos()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Membresía registrada pero error en el pago",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }

    private fun limpiarCampos() {
        etUsuario.setText("")
        actvPlanMembresia.setText("")
        cardInfoPlan.visibility = View.GONE
        etFechaInicio.setText("")
        etFechaFin.setText("")
        actvMetodoPago.setText("")
        etMonto.setText("")
        etObservaciones.setText("")

        tilUsuario.error = null
        tilPlanMembresia.error = null
        tilFechaInicio.error = null
        tilFechaFin.error = null
        tilMetodoPago.error = null
        tilMonto.error = null

        calendar.time = Date()
    }
}