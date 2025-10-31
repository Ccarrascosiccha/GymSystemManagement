package com.example.gymsystemmanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.data.MembresiaDAO
import java.text.SimpleDateFormat
import java.util.*

class MembresiaAdapter(
    private var membresias: List<MembresiaDAO.MembresiaDetalle>,
    private val onVerDetalles: (MembresiaDAO.MembresiaDetalle) -> Unit,
    private val onCancelar: (MembresiaDAO.MembresiaDetalle) -> Unit,
    private val onRenovar: (MembresiaDAO.MembresiaDetalle) -> Unit
) : RecyclerView.Adapter<MembresiaAdapter.MembresiaViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    inner class MembresiaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreUsuario: TextView = view.findViewById(R.id.tvNombreUsuario)
        val tvDni: TextView = view.findViewById(R.id.tvDni)
        val tvPlan: TextView = view.findViewById(R.id.tvPlan)
        val tvFechaInicio: TextView = view.findViewById(R.id.tvFechaInicio)
        val tvFechaFin: TextView = view.findViewById(R.id.tvFechaFin)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
        val btnVerDetalles: Button = view.findViewById(R.id.btnVerDetalles)
        val btnCancelar: Button = view.findViewById(R.id.btnCancelar)
        val btnRenovar: Button = view.findViewById(R.id.btnRenovar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembresiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_membresia, parent, false)
        return MembresiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MembresiaViewHolder, position: Int) {
        val membresia = membresias[position]

        holder.tvNombreUsuario.text = membresia.nombreCompleto
        holder.tvDni.text = "DNI: ${membresia.dni}"
        holder.tvPlan.text = membresia.nombrePlan

        // Formatear fechas
        try {
            val fechaInicio = dateFormat.parse(membresia.fechaInicio)
            val fechaFin = dateFormat.parse(membresia.fechaFin)
            holder.tvFechaInicio.text = "Inicio: ${displayFormat.format(fechaInicio)}"
            holder.tvFechaFin.text = "Fin: ${displayFormat.format(fechaFin)}"
        } catch (e: Exception) {
            holder.tvFechaInicio.text = "Inicio: ${membresia.fechaInicio}"
            holder.tvFechaFin.text = "Fin: ${membresia.fechaFin}"
        }

        holder.tvPrecio.text = "S/. ${String.format("%.2f", membresia.precio)}"
        holder.tvEstado.text = membresia.estado

        // Cambiar background según estado
        when (membresia.estado) {
            "Activa" -> {
                holder.tvEstado.setBackgroundResource(R.drawable.bg_estado_activo)
            }
            "Vencida" -> {
                holder.tvEstado.setBackgroundResource(R.drawable.bg_estado_vencido)
            }
            "Cancelada" -> {
                holder.tvEstado.setBackgroundResource(R.drawable.bg_estado_cancelado)
            }
        }

        // Configurar botones según estado
        when (membresia.estado) {
            "Activa" -> {
                holder.btnCancelar.visibility = View.VISIBLE
                holder.btnRenovar.visibility = View.GONE
            }
            "Vencida" -> {
                holder.btnCancelar.visibility = View.GONE
                holder.btnRenovar.visibility = View.VISIBLE
            }
            "Cancelada" -> {
                holder.btnCancelar.visibility = View.GONE
                holder.btnRenovar.visibility = View.VISIBLE
            }
        }

        holder.btnVerDetalles.setOnClickListener { onVerDetalles(membresia) }
        holder.btnCancelar.setOnClickListener { onCancelar(membresia) }
        holder.btnRenovar.setOnClickListener { onRenovar(membresia) }
    }

    override fun getItemCount() = membresias.size

    fun actualizarDatos(nuevasMembresias: List<MembresiaDAO.MembresiaDetalle>) {
        membresias = nuevasMembresias
        notifyDataSetChanged()
    }

    fun filtrar(texto: String) {
        val listaFiltrada = if (texto.isEmpty()) {
            membresias
        } else {
            membresias.filter {
                it.nombreCompleto.contains(texto, ignoreCase = true) ||
                        it.dni.toString().contains(texto) ||
                        it.nombrePlan.contains(texto, ignoreCase = true) ||
                        it.estado.contains(texto, ignoreCase = true)
            }
        }
        actualizarDatos(listaFiltrada)
    }
}