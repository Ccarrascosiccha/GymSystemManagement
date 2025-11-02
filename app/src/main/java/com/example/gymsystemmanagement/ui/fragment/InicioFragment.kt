package com.example.gymsystemmanagement.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.adapter.UsuarioAdapter
import com.example.gymsystemmanagement.data.AppDatabaseHelper
import com.example.gymsystemmanagement.data.TransaccionDAO
import com.example.gymsystemmanagement.entity.Usuario
import com.example.gymsystemmanagement.ui.adapter.TransaccionAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InicioFragment : Fragment(R.layout.fragment_inicio) {

    private lateinit var rvOperaciones: RecyclerView
    private lateinit var rvMiembros: RecyclerView
    private lateinit var transaccionAdapter: TransaccionAdapter
    private lateinit var transaccionDAO: TransaccionDAO
    private lateinit var btnSeeAll: TextView
    private lateinit var usuarioAdapter: UsuarioAdapter
    private lateinit var fabAnhiadir: FloatingActionButton

    private lateinit var tvGanancias: TextView
    private lateinit var tvTotalMiembros: TextView
    private lateinit var tvMiembrosNuevos: TextView
    private lateinit var tvUltimaActualizacion: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("InicioFragment", "=== INICIANDO InicioFragment ===")

        try {
            inicializarVistas(view)
            cargarDatosDashboard()
            cargarMiembrosDesdeSQLite()
            configurarRecyclerViewOperaciones()
            cargarUltimasTransacciones()
            configurarListeners()
        } catch (e: Exception) {
            Log.e("InicioFragment", "Error en onViewCreated: ${e.message}", e)
            Toast.makeText(requireContext(), "Error al inicializar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun inicializarVistas(view: View) {
        Log.d("InicioFragment", "Inicializando vistas...")

        rvOperaciones = view.findViewById(R.id.rvOperaciones)
        rvMiembros = view.findViewById(R.id.rvMiembros)
        btnSeeAll = view.findViewById(R.id.btnSeeAll)
        fabAnhiadir = view.findViewById(R.id.fabAnhiadir)
        tvGanancias = view.findViewById(R.id.tvGanancias)
        tvTotalMiembros = view.findViewById(R.id.tvTotalMiembros)
        tvMiembrosNuevos = view.findViewById(R.id.tvMiembrosNuevos)
        tvUltimaActualizacion = view.findViewById(R.id.tvUltimaActualizacion)

        rvMiembros.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        transaccionDAO = TransaccionDAO(requireContext())

        Log.d("InicioFragment", "✓ Vistas inicializadas correctamente")
    }

    private fun configurarRecyclerViewOperaciones() {
        Log.d("InicioFragment", "Configurando RecyclerView...")

        transaccionAdapter = TransaccionAdapter(emptyList())
        rvOperaciones.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transaccionAdapter
            setHasFixedSize(false)
        }

        Log.d("InicioFragment", "✓ RecyclerView configurado")
    }

    private fun cargarUltimasTransacciones() {
        try {
            Log.d("InicioFragment", "=== CARGANDO TRANSACCIONES ===")

            // Obtener todas las transacciones
            val todasTransacciones = transaccionDAO.obtenerTodasTransacciones()

            Log.d("InicioFragment", "→ Total transacciones en DB: ${todasTransacciones.size}")

            if (todasTransacciones.isEmpty()) {
                Log.w("InicioFragment", "⚠️ NO HAY TRANSACCIONES EN LA BASE DE DATOS")
                Toast.makeText(requireContext(), "No hay transacciones registradas", Toast.LENGTH_SHORT).show()
                return
            }

            // Tomar las últimas 5
            val ultimasTransacciones = todasTransacciones.take(5)

            Log.d("InicioFragment", "→ Mostrando ${ultimasTransacciones.size} transacciones")

            // Log detallado de cada transacción
            ultimasTransacciones.forEachIndexed { index, t ->
                Log.d("InicioFragment", """
                    Transacción #$index:
                    - ID: ${t.id}
                    - Descripción: ${t.descripcion}
                    - Monto: ${t.montoFormateado()}
                    - Tipo: ${t.tipo}
                    - Fecha: ${t.fechaSoloFecha()}
                """.trimIndent())
            }

            // Actualizar el adapter
            transaccionAdapter.actualizarTransacciones(ultimasTransacciones)

            Log.d("InicioFragment", "✓ Adapter actualizado con ${ultimasTransacciones.size} transacciones")

        } catch (e: Exception) {
            Log.e("InicioFragment", "❌ ERROR al cargar transacciones: ${e.message}", e)
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "Error al cargar transacciones: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun configurarListeners() {
        btnSeeAll.setOnClickListener {
            Log.d("InicioFragment", "Click en 'Ver todo'")
            Toast.makeText(requireContext(), "Ver todas las transacciones", Toast.LENGTH_SHORT).show()
            // TODO: Navegar a TransaccionesFragment cuando lo crees
        }

        fabAnhiadir.setOnClickListener {
            Log.d("InicioFragment", "Click en FAB - Registrar membresía")

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragmentContainer, RegistroMembresiaFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("InicioFragment", "onResume - Recargando datos")

        // Solo recargar si el adapter ya fue inicializado
        if (::transaccionAdapter.isInitialized) {
            cargarUltimasTransacciones()
        } else {
            Log.w("InicioFragment", "Adapter no inicializado en onResume, se inicializará en onViewCreated")
        }
    }
    private fun cargarMiembrosDesdeSQLite() {
        viewLifecycleOwner.lifecycleScope.launch {
            val usuarios = withContext(Dispatchers.IO) {
                val dbHelper = AppDatabaseHelper(requireContext())
                val db = dbHelper.readableDatabase
                val lista = mutableListOf<Usuario>()

                val cursor = db.rawQuery(
                    """
                    SELECT id, dni, apellidoPaterno, apellidoMaterno, nombres, celular, sexo, correo, direccion, fechaRegistro, rol, clave, estado
                    FROM Usuario
                    WHERE estado='Activo'
                    ORDER BY datetime(fechaRegistro) DESC
                    LIMIT 10
                    """, null
                )

                if (cursor.moveToFirst()) {
                    do {
                        lista.add(
                            Usuario(
                                id = cursor.getInt(0),
                                dni = cursor.getInt(1),
                                apellidoPaterno = cursor.getString(2),
                                apellidoMaterno = cursor.getString(3),
                                nombres = cursor.getString(4),
                                celular = cursor.getString(5),
                                sexo = cursor.getString(6),
                                correo = cursor.getString(7),
                                direccion = cursor.getString(8),
                                fechaRegistro = cursor.getString(9),
                                rol = cursor.getString(10),
                                clave = cursor.getString(11),
                                estado = cursor.getString(12)
                            )
                        )
                    } while (cursor.moveToNext())
                }

                cursor.close()
                db.close()
                lista
            }

            usuarioAdapter = UsuarioAdapter(usuarios)
            rvMiembros.adapter = usuarioAdapter
            usuarioAdapter.notifyDataSetChanged()
        }
    }
    private fun cargarDatosDashboard() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d("InicioFragment", "=== INICIANDO CARGA DE DASHBOARD ===")

                val dbHelper = AppDatabaseHelper(requireContext())
                val db = dbHelper.readableDatabase

                // Total de ganancias - CORREGIDO: busca 'Cr' en lugar de 'Ingreso'
                val cursorGanancias = db.rawQuery(
                    "SELECT IFNULL(SUM(monto), 0) FROM Transaccion WHERE tipo='Cr'", null
                )
                var totalGanancias = 0.0
                if (cursorGanancias.moveToFirst()) {
                    totalGanancias = cursorGanancias.getDouble(0)
                    Log.d("InicioFragment", "→ Total Ganancias: $totalGanancias")
                } else {
                    Log.w("InicioFragment", "⚠️ No se pudo leer ganancias")
                }
                cursorGanancias.close()

                // Total de miembros activos
                val cursorMiembros = db.rawQuery(
                    "SELECT COUNT(*) FROM Usuario WHERE estado='Activo'", null
                )
                var totalMiembros = 0
                if (cursorMiembros.moveToFirst()) {
                    totalMiembros = cursorMiembros.getInt(0)
                    Log.d("InicioFragment", "→ Total Miembros: $totalMiembros")
                } else {
                    Log.w("InicioFragment", "⚠️ No se pudo leer miembros")
                }
                cursorMiembros.close()

                // Miembros nuevos últimos 30 días (cambiado de 7 a 30)
                val cursorNuevos = db.rawQuery(
                    """
                SELECT COUNT(*) FROM Usuario 
                WHERE estado='Activo' 
                AND date(fechaRegistro) >= date('now', '-30 day')
                """, null
                )
                var nuevosMiembros = 0
                if (cursorNuevos.moveToFirst()) {
                    nuevosMiembros = cursorNuevos.getInt(0)
                    Log.d("InicioFragment", "→ Miembros Nuevos (30 días): $nuevosMiembros")
                } else {
                    Log.w("InicioFragment", "⚠️ No se pudo leer miembros nuevos")
                }
                cursorNuevos.close()

                db.close()

                // Mostrar datos en la UI
                withContext(Dispatchers.Main) {
                    Log.d("InicioFragment", "→ Actualizando UI en hilo principal...")

                    val gananciaTexto = "S/. %.2f".format(totalGanancias)
                    tvGanancias.text = gananciaTexto
                    Log.d("InicioFragment", "✓ tvGanancias actualizado: $gananciaTexto")

                    tvTotalMiembros.text = totalMiembros.toString()
                    Log.d("InicioFragment", "✓ tvTotalMiembros actualizado: $totalMiembros")

                    tvMiembrosNuevos.text = nuevosMiembros.toString()
                    Log.d("InicioFragment", "✓ tvMiembrosNuevos actualizado: $nuevosMiembros")

                    val fechaActual = java.text.SimpleDateFormat(
                        "hh:mm a\nMMMM dd, yyyy",
                        java.util.Locale("es", "PE")
                    ).format(java.util.Date())

                    tvUltimaActualizacion.text = fechaActual
                    Log.d("InicioFragment", "✓ tvUltimaActualizacion actualizado: $fechaActual")

                    Log.d("InicioFragment", "✅ DASHBOARD CARGADO COMPLETAMENTE")
                }

            } catch (e: Exception) {
                Log.e("InicioFragment", "❌ Error cargando dashboard: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }

}