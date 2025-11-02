package com.example.gymsystemmanagement.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.adapter.MembresiaAdapter
import com.example.gymsystemmanagement.data.MembresiaDAO
import com.example.gymsystemmanagement.entity.MembresiaCompleta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

class HistorialMembresiaFragment : Fragment(R.layout.fragment_historial_membresia) {

    private lateinit var rvMembresias: RecyclerView
    private lateinit var membresiaAdapter: MembresiaAdapter
    private lateinit var tvNoData: TextView
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "PE"))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("HistorialMembresia", "=== INICIANDO HistorialMembresiaFragment ===")

        try {
            inicializarVistas(view)
            configurarRecyclerView()
            cargarMembresias()
        } catch (e: Exception) {
            Log.e("HistorialMembresia", "Error en onViewCreated: ${e.message}", e)
            Toast.makeText(requireContext(), "Error al inicializar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun inicializarVistas(view: View) {
        Log.d("HistorialMembresia", "Inicializando vistas...")

        rvMembresias = view.findViewById(R.id.rvMembresias)

        // Intentar encontrar el TextView de "sin datos" si existe
        try {
            tvNoData = view.findViewById(R.id.tvNoData)
        } catch (e: Exception) {
            Log.w("HistorialMembresia", "No se encontró tvNoData en el layout")
        }

        Log.d("HistorialMembresia", "✓ Vistas inicializadas")
    }

    private fun configurarRecyclerView() {
        Log.d("HistorialMembresia", "Configurando RecyclerView...")

        // Inicializar adapter con los listeners
        membresiaAdapter = MembresiaAdapter(
            membresias = emptyList(),
            onDetallesClick = { membresiaCompleta ->
                mostrarDialogDetalles(membresiaCompleta)
            },
            onCancelarClick = { membresiaCompleta ->
                mostrarDialogCancelar(membresiaCompleta)
            },
            onRenovarClick = { membresiaCompleta ->
                mostrarDialogRenovar(membresiaCompleta)
            }
        )

        rvMembresias.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = membresiaAdapter
            setHasFixedSize(true)
        }

        Log.d("HistorialMembresia", "✓ RecyclerView configurado")
    }

    /**
     * Cargar todas las membresías activas con información completa
     */
    private fun cargarMembresias() {
        lifecycleScope.launch {
            try {
                Log.d("HistorialMembresia", "=== CARGANDO MEMBRESÍAS ===")

                val membresias = withContext(Dispatchers.IO) {
                    val dao = MembresiaDAO(requireContext())

                    // Primero intentar obtener todas las membresías
                    val todas = dao.obtenerMembresiasActivasCompletas()
                    Log.d("HistorialMembresia", "→ Total membresías (todas): ${todas.size}")

                    // Luego solo las activas
                    val activas = dao.obtenerMembresiasActivasCompletas()
                    Log.d("HistorialMembresia", "→ Total membresías activas: ${activas.size}")

                    // Retornar las activas (o cambiar a 'todas' si prefieres ver todo)
                    activas
                }

                Log.d("HistorialMembresia", "→ Membresías cargadas para mostrar: ${membresias.size}")

                // Log detallado de cada membresía
                membresias.forEachIndexed { index, mc ->
                    Log.d("HistorialMembresia", """
                        Membresía #$index:
                        - ID: ${mc.membresia.id}
                        - Usuario: ${mc.usuario.nombres} ${mc.usuario.apellidoPaterno}
                        - Plan: ${mc.nombrePlan}
                        - Estado: ${mc.membresia.estado}
                        - Precio: ${mc.precioPlan}
                    """.trimIndent())
                }

                // Actualizar el adapter
                membresiaAdapter.actualizarLista(membresias)

                Log.d("HistorialMembresia", "✓ Adapter actualizado")

                // Mostrar/ocultar mensaje de "sin datos"
                if (membresias.isEmpty()) {
                    rvMembresias.visibility = View.GONE
                    if (::tvNoData.isInitialized) {
                        tvNoData.visibility = View.VISIBLE
                    }

                    Toast.makeText(
                        requireContext(),
                        "No hay membresías activas registradas",
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.w("HistorialMembresia", "⚠️ NO HAY MEMBRESÍAS PARA MOSTRAR")
                } else {
                    rvMembresias.visibility = View.VISIBLE
                    if (::tvNoData.isInitialized) {
                        tvNoData.visibility = View.GONE
                    }

                    Log.d("HistorialMembresia", "✅ MEMBRESÍAS MOSTRADAS CORRECTAMENTE")
                }

            } catch (e: Exception) {
                Log.e("HistorialMembresia", "❌ ERROR al cargar membresías: ${e.message}", e)
                e.printStackTrace()

                Toast.makeText(
                    requireContext(),
                    "Error al cargar membresías: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ... resto de tus funciones (mostrarDialogDetalles, cancelar, renovar, etc.)

    private fun mostrarDialogDetalles(membresiaCompleta: MembresiaCompleta) {
        val membresia = membresiaCompleta.membresia
        val usuario = membresiaCompleta.usuario

        val mensaje = buildString {
            append("━━━━━━━━━━━━━━━━━━━━━━━━\n")
            append("INFORMACIÓN DE MEMBRESÍA\n")
            append("━━━━━━━━━━━━━━━━━━━━━━━━\n\n")

            append("📋 Código: ${membresia.id}\n")
            append("📊 Estado: ${membresia.estado}\n\n")

            append("👤 USUARIO\n")
            append("Nombre: ${usuario.nombres} ${usuario.apellidoPaterno} ${usuario.apellidoMaterno}\n")
            append("DNI: ${usuario.dni}\n")
            if (!usuario.celular.isNullOrEmpty()) {
                append("Teléfono: ${usuario.celular}\n")
            }
            if (!usuario.correo.isNullOrEmpty()) {
                append("Email: ${usuario.correo}\n")
            }
            append("\n")

            append("💳 PLAN\n")
            append("Nombre: ${membresiaCompleta.nombrePlan}\n")
            append("Precio: ${currencyFormat.format(membresiaCompleta.precioPlan)}\n\n")

            append("📅 FECHAS\n")
            append("Inicio: ${membresia.fechaInicioFormateada()}\n")
            append("Fin: ${membresia.fechaFinFormateada()}\n")

            if (membresia.estado == "Activa") {
                val diasRestantes = membresia.diasRestantes()
                append("Días restantes: $diasRestantes\n")

                if (membresia.proximaAVencer()) {
                    append("\n⚠️ Próxima a vencer")
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Detalles de Membresía")
            .setMessage(mensaje)
            .setPositiveButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun mostrarDialogCancelar(membresiaCompleta: MembresiaCompleta) {
        val membresia = membresiaCompleta.membresia
        val usuario = membresiaCompleta.usuario

        val nombreCompleto = "${usuario.nombres} ${usuario.apellidoPaterno} ${usuario.apellidoMaterno}"

        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Cancelar Membresía")
            .setMessage(
                "¿Estás seguro de cancelar la membresía de:\n\n" +
                        "👤 $nombreCompleto\n" +
                        "📋 Código: ${membresia.id}\n" +
                        "💳 Plan: ${membresiaCompleta.nombrePlan}\n\n" +
                        "Esta acción cambiará el estado a 'Cancelada'."
            )
            .setPositiveButton("Sí, Cancelar") { dialog, _ ->
                cancelarMembresia(membresia.id)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun cancelarMembresia(idMembresia: Int) {
        lifecycleScope.launch {
            try {
                val resultado = withContext(Dispatchers.IO) {
                    val dao = MembresiaDAO(requireContext())
                    dao.actualizarEstado(idMembresia, "Cancelada")
                }

                if (resultado > 0) {
                    Toast.makeText(
                        requireContext(),
                        "✅ Membresía cancelada exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    cargarMembresias()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "❌ Error al cancelar la membresía",
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
    }

    private fun mostrarDialogRenovar(membresiaCompleta: MembresiaCompleta) {
        val membresia = membresiaCompleta.membresia
        val usuario = membresiaCompleta.usuario

        val nombreCompleto = "${usuario.nombres} ${usuario.apellidoPaterno} ${usuario.apellidoMaterno}"

        AlertDialog.Builder(requireContext())
            .setTitle("🔄 Renovar Membresía")
            .setMessage(
                "¿Deseas renovar la membresía de:\n\n" +
                        "👤 $nombreCompleto\n" +
                        "📋 Código anterior: ${membresia.id}\n" +
                        "💳 Plan: ${membresiaCompleta.nombrePlan}\n" +
                        "💰 Precio: ${currencyFormat.format(membresiaCompleta.precioPlan)}\n\n" +
                        "Esto creará una nueva membresía con el mismo plan."
            )
            .setPositiveButton("Sí, Renovar") { dialog, _ ->
                navegarARegistroConDatos(membresiaCompleta)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun navegarARegistroConDatos(membresiaCompleta: MembresiaCompleta) {
        val bundle = Bundle().apply {
            putInt("idUsuario", membresiaCompleta.usuario.id)
            putString("nombrePlan", membresiaCompleta.nombrePlan)
            putBoolean("esRenovacion", true)
        }

        val fragment = RegistroMembresiaFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}