package com.example.gymsystemmanagement.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.data.TransaccionDAO
import com.example.gymsystemmanagement.entity.Transaccion
import com.example.gymsystemmanagement.ui.adapter.TransaccionOpcionesAdapter
import com.google.android.material.button.MaterialButton

class HistorialTransaccionesFragment : Fragment(R.layout.fragment_historial_transacciones) {

    private lateinit var rvTransacciones: RecyclerView
    private lateinit var tvNoData: TextView
    private lateinit var transaccionAdapter: TransaccionOpcionesAdapter
    private lateinit var transaccionDAO: TransaccionDAO

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("HistorialTransacciones", "=== INICIANDO HistorialTransaccionesFragment ===")

        inicializarVistas(view)
        configurarRecyclerView()
        cargarTransacciones()
    }

    private fun inicializarVistas(view: View) {
        rvTransacciones = view.findViewById(R.id.rvTransacciones)
        tvNoData = view.findViewById(R.id.tvNoData)
        transaccionDAO = TransaccionDAO(requireContext())
    }

    private fun configurarRecyclerView() {
        transaccionAdapter = TransaccionOpcionesAdapter(
            transacciones = emptyList(),
            onVerDetalles = { transaccion ->
                mostrarDialogDetalles(transaccion)
            },
            onEliminar = { transaccion ->
                mostrarDialogEliminar(transaccion)
            }
        )

        rvTransacciones.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transaccionAdapter
            setHasFixedSize(true)
        }
    }

    private fun cargarTransacciones() {
        try {
            Log.d("HistorialTransacciones", "Cargando todas las transacciones...")

            val transacciones = transaccionDAO.obtenerTodasTransacciones()

            Log.d("HistorialTransacciones", "Total transacciones: ${transacciones.size}")

            if (transacciones.isEmpty()) {
                rvTransacciones.visibility = View.GONE
                tvNoData.visibility = View.VISIBLE
            } else {
                rvTransacciones.visibility = View.VISIBLE
                tvNoData.visibility = View.GONE
                transaccionAdapter.actualizarTransacciones(transacciones)
            }

        } catch (e: Exception) {
            Log.e("HistorialTransacciones", "Error al cargar transacciones: ${e.message}", e)
        }
    }

    private fun mostrarDialogDetalles(transaccion: Transaccion) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_detalle_transaccion, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Llenar datos del diálogo
        dialogView.findViewById<TextView>(R.id.tvDetalleId).text = "ID: ${transaccion.id}"
        dialogView.findViewById<TextView>(R.id.tvDetalleDescripcion).text = transaccion.descripcion
        dialogView.findViewById<TextView>(R.id.tvDetalleMonto).text = transaccion.montoFormateado()
        dialogView.findViewById<TextView>(R.id.tvDetalleTipo).text = transaccion.tipo
        dialogView.findViewById<TextView>(R.id.tvDetalleFecha).text = transaccion.fechaFormateada()

        dialogView.findViewById<MaterialButton>(R.id.btnCerrar).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun mostrarDialogEliminar(transaccion: Transaccion) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Transacción")
            .setMessage("¿Estás seguro de eliminar la transacción #${transaccion.id}?\n\n${transaccion.descripcion}\n${transaccion.montoFormateado()}")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarTransaccion(transaccion)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarTransaccion(transaccion: Transaccion) {
        try {
            transaccionDAO.eliminarTransaccion(transaccion.id)
            Log.d("HistorialTransacciones", "Transacción ${transaccion.id} eliminada")
            cargarTransacciones() // Recargar lista
        } catch (e: Exception) {
            Log.e("HistorialTransacciones", "Error al eliminar: ${e.message}", e)
        }
    }

    override fun onResume() {
        super.onResume()
        if (::transaccionAdapter.isInitialized) {
            cargarTransacciones()
        }
    }
}