package com.example.gymsystemmanagement.ui.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.data.AppDatabaseHelper
import com.example.gymsystemmanagement.data.MembresiaDAO
import com.example.gymsystemmanagement.data.PlanMembresiaDAO
import com.example.gymsystemmanagement.entity.Membresia
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class RegistroMembresiaFragment : Fragment(R.layout.fragment_registro_membresia) {

    private lateinit var tilUsuario: TextInputLayout
    private lateinit var etUsuario: TextInputEditText
    private lateinit var tilPlanMembresia: TextInputLayout
    private lateinit var actvPlanMembresia: AutoCompleteTextView
    private lateinit var tilFechaInicio: TextInputLayout
    private lateinit var etFechaInicio: TextInputEditText
    private lateinit var tilFechaFin: TextInputLayout
    private lateinit var etFechaFin: TextInputEditText
    private lateinit var btnRegistrar: MaterialButton
    private lateinit var btnCancelar: MaterialButton

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val dbDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        tilUsuario = view.findViewById(R.id.tilUsuario)
        etUsuario = view.findViewById(R.id.etUsuario)
        tilPlanMembresia = view.findViewById(R.id.tilPlan)
        actvPlanMembresia = view.findViewById(R.id.actvPlan)
        tilFechaInicio = view.findViewById(R.id.tilFechaInicio)
        etFechaInicio = view.findViewById(R.id.etFechaInicio)
        tilFechaFin = view.findViewById(R.id.tilFechaFin)
        etFechaFin = view.findViewById(R.id.etFechaFin)
        btnRegistrar = view.findViewById(R.id.btnRegistrar)
        btnCancelar = view.findViewById(R.id.btnCancelar)

        // Cargar planes en el AutoCompleteTextView
        cargarPlanes()

        // Configurar DatePicker para Fecha Inicio
        configurarDatePickerFechaInicio()

        // Configurar listener para calcular fecha fin automáticamente cuando se selecciona un plan
        actvPlanMembresia.setOnItemClickListener { _, _, _, _ ->
            calcularFechaFinAutomatica()
        }

        // Configurar listeners de botones
        btnRegistrar.setOnClickListener {
            guardarMembresia()
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

    /**
     * Configurar DatePicker para el campo Fecha Inicio
     */
    private fun configurarDatePickerFechaInicio() {
        // Hacer el campo de fecha inicio clickeable pero no editable manualmente
        etFechaInicio.isFocusable = false
        etFechaInicio.isClickable = true
        etFechaInicio.isCursorVisible = false

        // Hacer el campo de fecha fin completamente no editable
        etFechaFin.isFocusable = false
        etFechaFin.isClickable = false
        etFechaFin.isCursorVisible = false
        etFechaFin.keyListener = null  // Deshabilitar completamente el teclado

        // Activar DatePicker al hacer clic en el campo
        etFechaInicio.setOnClickListener {
            mostrarDatePickerFechaInicio()
        }

        // Activar DatePicker al hacer clic en el ícono del calendario
        tilFechaInicio.setEndIconOnClickListener {
            mostrarDatePickerFechaInicio()
        }
    }

    /**
     * Mostrar el DatePickerDialog para Fecha Inicio
     */
    private fun mostrarDatePickerFechaInicio() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Actualizar el calendario con la fecha seleccionada
                calendar.set(selectedYear, selectedMonth, selectedDay)

                // Mostrar la fecha en el campo
                val fechaSeleccionada = dateFormat.format(calendar.time)
                etFechaInicio.setText(fechaSeleccionada)

                android.util.Log.d("RegistroMembresia", "Fecha inicio seleccionada: $fechaSeleccionada")

                // Limpiar error si existía
                tilFechaInicio.error = null

                // Calcular automáticamente la fecha fin
                android.util.Log.d("RegistroMembresia", "Llamando a calcularFechaFinAutomatica()")
                calcularFechaFinAutomatica()
            },
            year,
            month,
            day
        )

        // Establecer fecha mínima (hoy) para evitar fechas pasadas
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    /**
     * Calcular automáticamente la fecha fin basándose en el plan seleccionado y la fecha inicio
     */
    private fun calcularFechaFinAutomatica() {
        android.util.Log.d("RegistroMembresia", "=== INICIO calcularFechaFinAutomatica ===")

        // Verificar que haya una fecha de inicio
        val fechaInicioTexto = etFechaInicio.text?.toString() ?: ""
        android.util.Log.d("RegistroMembresia", "Fecha inicio texto: '$fechaInicioTexto'")

        if (fechaInicioTexto.isBlank()) {
            android.util.Log.d("RegistroMembresia", "Fecha inicio vacía - mostrando toast")
            Toast.makeText(
                requireContext(),
                "Primero seleccione la fecha de inicio",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Verificar que haya un plan seleccionado
        val nombrePlan = actvPlanMembresia.text?.toString() ?: ""
        android.util.Log.d("RegistroMembresia", "Plan seleccionado: '$nombrePlan'")

        if (nombrePlan.isBlank()) {
            android.util.Log.d("RegistroMembresia", "Plan no seleccionado - limpiando fecha fin")
            etFechaFin.setText("")
            return
        }

        try {
            android.util.Log.d("RegistroMembresia", "Intentando obtener plan de BD...")

            // Obtener el plan seleccionado usando el DAO
            val planDao = PlanMembresiaDAO(requireContext())
            val idPlan = planDao.obtenerIdPorNombre(nombrePlan)

            android.util.Log.d("RegistroMembresia", "ID Plan obtenido: $idPlan")

            if (idPlan == 0) {
                android.util.Log.e("RegistroMembresia", "Plan no encontrado en BD")
                Toast.makeText(requireContext(), "Plan no encontrado", Toast.LENGTH_SHORT).show()
                return
            }

            val plan = planDao.obtenerPlanPorId(idPlan)

            if (plan == null) {
                android.util.Log.e("RegistroMembresia", "Error al obtener plan por ID")
                Toast.makeText(requireContext(), "Error al obtener el plan", Toast.LENGTH_SHORT).show()
                return
            }

            // Obtener la duración en meses del plan
            val duracionMeses = plan.duracionMeses
            android.util.Log.d("RegistroMembresia", "Duración del plan: $duracionMeses meses")

            // Parsear la fecha de inicio seleccionada
            val fechaInicio = dateFormat.parse(fechaInicioTexto)
            android.util.Log.d("RegistroMembresia", "Fecha inicio parseada: $fechaInicio")

            if (fechaInicio != null) {
                // Crear un nuevo calendario con la fecha de inicio
                val calendarFin = Calendar.getInstance()
                calendarFin.time = fechaInicio

                android.util.Log.d("RegistroMembresia", "Fecha antes de agregar meses: ${calendarFin.time}")

                // Agregar los meses de duración del plan
                calendarFin.add(Calendar.MONTH, duracionMeses)

                android.util.Log.d("RegistroMembresia", "Fecha después de agregar $duracionMeses meses: ${calendarFin.time}")

                // Mostrar la fecha fin calculada
                val fechaFin = dateFormat.format(calendarFin.time)
                android.util.Log.d("RegistroMembresia", "Fecha fin formateada: $fechaFin")

                etFechaFin.setText(fechaFin)

                android.util.Log.d("RegistroMembresia", "✅ Fecha fin establecida correctamente")

                // Limpiar error si existía
                tilFechaFin.error = null
            } else {
                android.util.Log.e("RegistroMembresia", "No se pudo parsear la fecha inicio")
            }

        } catch (e: Exception) {
            android.util.Log.e("RegistroMembresia", "ERROR: ${e.message}", e)
            Toast.makeText(
                requireContext(),
                "Error al calcular fecha fin: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }

        android.util.Log.d("RegistroMembresia", "=== FIN calcularFechaFinAutomatica ===")
    }

    /**
     * Cargar los planes disponibles en el AutoCompleteTextView
     */
    private fun cargarPlanes() {
        try {
            val planDao = PlanMembresiaDAO(requireContext())
            val nombresPlanes = planDao.obtenerNombresPlanes()

            // Configurar adapter
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                nombresPlanes
            )
            actvPlanMembresia.setAdapter(adapter)

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error al cargar planes: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }

    /**
     * Validar que todos los campos estén llenos
     */
    private fun validarCampos(): Boolean {
        var esValido = true

        // Validar código de usuario
        if (etUsuario.text.isNullOrBlank()) {
            tilUsuario.error = "Ingrese el código de usuario"
            esValido = false
        } else {
            // Verificar que sea un número válido
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

        return esValido
    }

    /**
     * Obtener el ID del usuario desde el campo de texto
     */
    private fun obtenerIdUsuario(): Int {
        return try {
            etUsuario.text.toString().toInt()
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Verificar si el usuario existe en la base de datos
     */
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

    /**
     * Guardar la membresía completa en la base de datos
     */
    private fun guardarMembresia() {
        // 1. Validar campos
        if (!validarCampos()) {
            Toast.makeText(requireContext(), "Complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // 2. Obtener el nombre del plan seleccionado
            val nombrePlanSeleccionado = actvPlanMembresia.text.toString()

            // 3. Buscar el ID del plan en la base de datos
            val planDao = PlanMembresiaDAO(requireContext())
            val idPlan = planDao.obtenerIdPorNombre(nombrePlanSeleccionado)

            if (idPlan == 0) {
                Toast.makeText(requireContext(), "Plan no encontrado", Toast.LENGTH_SHORT).show()
                return
            }

            // 4. Obtener el ID del usuario
            val idUsuario = obtenerIdUsuario()

            if (idUsuario == 0) {
                Toast.makeText(requireContext(), "Código de usuario no válido", Toast.LENGTH_SHORT).show()
                return
            }

            // 4.1 Verificar que el usuario exista en la base de datos
            if (!verificarUsuarioExiste(idUsuario)) {
                Toast.makeText(
                    requireContext(),
                    "El usuario con código $idUsuario no existe",
                    Toast.LENGTH_LONG
                ).show()
                tilUsuario.error = "Usuario no encontrado"
                return
            }

            // 5. Convertir fechas del formato de visualización (dd/MM/yyyy) al formato de BD (yyyy-MM-dd HH:mm:ss)
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

            // 6. Crear el objeto Membresia
            val nuevaMembresia = Membresia(
                id = 0, // Se autogenera en la BD
                idUsuario = idUsuario,
                idPlan = idPlan,
                fechaInicio = fechaInicioDB,
                fechaFin = fechaFinDB,
                estado = "Activa"
            )

            // 7. Guardar en la base de datos
            val membresiaDao = MembresiaDAO(requireContext())
            val resultado = membresiaDao.insertarMembresia(nuevaMembresia)

            // 8. Verificar resultado
            if (resultado != -1L) {
                Toast.makeText(
                    requireContext(),
                    "✅ Membresía registrada exitosamente",
                    Toast.LENGTH_LONG
                ).show()

                // 9. Limpiar campos
                limpiarCampos()

                // 10. Opcional: Navegar a la pantalla anterior
                // parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(
                    requireContext(),
                    "❌ Error al registrar membresía",
                    Toast.LENGTH_SHORT
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

    /**
     * Limpiar todos los campos después de guardar exitosamente
     */
    private fun limpiarCampos() {
        etUsuario.setText("")
        actvPlanMembresia.setText("")
        etFechaInicio.setText("")
        etFechaFin.setText("")

        // Limpiar errores
        tilUsuario.error = null
        tilPlanMembresia.error = null
        tilFechaInicio.error = null
        tilFechaFin.error = null

        // Resetear el calendario a la fecha actual
        calendar.time = Date()
    }
}